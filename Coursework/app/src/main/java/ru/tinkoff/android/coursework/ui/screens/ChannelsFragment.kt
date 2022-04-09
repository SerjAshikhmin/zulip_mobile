package ru.tinkoff.android.coursework.ui.screens

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.channelsTestData
import ru.tinkoff.android.coursework.databinding.FragmentChannelsBinding
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListAdapter
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListPagerAdapter
import java.util.concurrent.TimeUnit

internal class ChannelsFragment: CompositeDisposableFragment() {

    private lateinit var binding: FragmentChannelsBinding
    private val queryEvents: PublishSubject<String> = PublishSubject.create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        binding = FragmentChannelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureViewPager()

        // задержка, чтобы листенер не отрабатывал на пустом запросе при создании фрагмента
        Handler(Looper.getMainLooper()).postDelayed({
            binding.searchEditText.doAfterTextChanged { text ->
                val allChannelsTab = binding.tabLayout.getTabAt(1)
                allChannelsTab?.select()
                val query = text?.toString().orEmpty()
                queryEvents.onNext(query)
            }
        }, 100)

        binding.searchIcon.setOnClickListener {
            binding.searchEditText.requestFocus()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        subscribeOnSearchChannelsEvents()
    }

    private fun subscribeOnSearchChannelsEvents() {
        queryEvents
            .map { query -> query.trim() }
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .switchMapSingle { query ->
                Single.fromCallable {
                    if (query.isBlank()) {
                        channelsTestData
                    } else {
                        channelsTestData
                            .filter { it.name.lowercase().contains(query.lowercase()) }
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    val allChannelsRecycler = (binding.pager.adapter as ChannelsListPagerAdapter)
                        .allChannelsFragment.binding.allChannelsList
                    (allChannelsRecycler.adapter as ChannelsListAdapter).apply {
                        showShimmer = false
                        channels = it
                        notifyDataSetChanged()
                    }
                },
                onError = {
                    val allChannelsRecycler = (binding.pager.adapter as ChannelsListPagerAdapter)
                        .allChannelsFragment.binding.allChannelsList
                    (allChannelsRecycler.adapter as ChannelsListAdapter).apply {
                        showShimmer = false
                        channels = listOf()
                        notifyDataSetChanged()
                    }
                    subscribeOnSearchChannelsEvents()
                    Toast.makeText(
                        context,
                        resources.getString(R.string.search_channels_error_text),
                        Toast.LENGTH_LONG
                    )
                        .show()
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
            val tabNames = listOf(
                resources.getString(R.string.subscribed_tab_name),
                resources.getString(R.string.allChannels_tab_name)
            )
            tab.text = tabNames[position]
        }.attach()
    }

}
