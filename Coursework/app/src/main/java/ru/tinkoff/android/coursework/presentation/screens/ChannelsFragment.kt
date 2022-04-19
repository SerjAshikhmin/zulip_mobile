package ru.tinkoff.android.coursework.presentation.screens

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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.databinding.FragmentChannelsBinding
import ru.tinkoff.android.coursework.data.api.NetworkService
import ru.tinkoff.android.coursework.presentation.screens.adapters.StreamsListPagerAdapter
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
                val allStreamsTab = binding.tabLayout.getTabAt(1)
                allStreamsTab?.select()
                val query = text?.toString().orEmpty()
                queryEvents.onNext(query)
            }
        }, 100)

        binding.searchIcon.setOnClickListener {
            binding.searchEditText.requestFocus()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        subscribeOnSearchStreamsEvents()
    }

    private fun subscribeOnSearchStreamsEvents() {
        queryEvents
            .map { query -> query.trim() }
            .distinctUntilChanged()
            .debounce(DELAY_BETWEEN_ENTERING_CHARACTERS, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .map { query ->
                searchStreamsByQuery(query)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
            .addTo(disposables)
    }

    private fun searchStreamsByQuery(query: String) {
        NetworkService.getZulipJsonApi().getAllStreams()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    val streamsListPagerAdapter = (binding.pager.adapter as StreamsListPagerAdapter)

                    if (streamsListPagerAdapter.isAllStreamsListFragment()) {
                        streamsListPagerAdapter.allStreamsListFragment.updateStreams(
                            it.streams.filter { stream ->
                                stream.name.lowercase().contains(query.lowercase())
                            }
                        )
                    }
                },
                onError = {
                    (binding.pager.adapter as StreamsListPagerAdapter)
                        .allStreamsListFragment.updateStreams(listOf())

                    subscribeOnSearchStreamsEvents()

                    Toast.makeText(
                        context,
                        resources.getString(R.string.search_streams_error_text),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            )
            .addTo(disposables)
    }

    private fun configureViewPager() {
        val viewPager = binding.pager
        val tabLayout = binding.tabLayout

        val pagerAdapter = StreamsListPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabNames = listOf(
                resources.getString(R.string.subscribed_tab_name),
                resources.getString(R.string.all_streams_tab_name)
            )
            tab.text = tabNames[position]
        }.attach()
    }

    companion object {

        private const val DELAY_BETWEEN_ENTERING_CHARACTERS = 500L
    }

}
