package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.getChannelsByPartOfName
import ru.tinkoff.android.coursework.databinding.FragmentChannelsBinding
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListAdapter
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListPagerAdapter
import java.util.concurrent.TimeUnit

internal class ChannelsFragment: Fragment() {

    private lateinit var binding: FragmentChannelsBinding
    private val queryEvents: PublishSubject<String> = PublishSubject.create()
    private lateinit var compositeDisposable: CompositeDisposable

        override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChannelsBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        compositeDisposable = CompositeDisposable()
        configureViewPager()

        val channelsSearch = binding.searchEditText
        channelsSearch.addTextChangedListener { text ->
            val allChannelsTab = binding.tabLayout.getTabAt(1)
            allChannelsTab?.select()
            val query = text?.toString().orEmpty()
            queryEvents.onNext(query)
        }

        subscribeOnSearchChannelsEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }

    private fun subscribeOnSearchChannelsEvents() {
        queryEvents
            .map { query -> query.trim() }
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.io())
            .switchMapSingle { query ->
                getChannelsByPartOfName(query)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    val allChannelsRecycler = view?.findViewById<RecyclerView>(R.id.all_channels_list)
                    (allChannelsRecycler?.adapter as ChannelsListAdapter).channels = it
                },
                onError = {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                }
            )
            .addTo(compositeDisposable)
    }

    private fun configureViewPager() {
        val viewPager = binding.pager
        val tabLayout = binding.tabLayout

        val pagerAdapter = ChannelsListPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabNames = listOf("Subscribed", "All streams")
            tab.text = tabNames[position]
        }.attach()
    }
}
