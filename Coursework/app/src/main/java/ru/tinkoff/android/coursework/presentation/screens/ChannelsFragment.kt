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
import ru.tinkoff.android.coursework.App
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.databinding.FragmentChannelsBinding
import ru.tinkoff.android.coursework.di.streams.DaggerStreamsComponent
import ru.tinkoff.android.coursework.presentation.elm.channels.StreamsElmStoreFactory
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEffect
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEvent
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsState
import ru.tinkoff.android.coursework.presentation.screens.adapters.StreamsListPagerAdapter
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

internal class ChannelsFragment: ElmFragment<StreamsEvent, StreamsEffect, StreamsState>() {

    @Inject
    internal lateinit var streamsElmStoreFactory: StreamsElmStoreFactory

    override var initEvent: StreamsEvent = StreamsEvent.Ui.SubscribeOnSearchStreamsEvents
    private lateinit var binding: FragmentChannelsBinding
    private lateinit var streamsListPagerAdapter: StreamsListPagerAdapter

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
        streamsListPagerAdapter = (binding.pager.adapter as StreamsListPagerAdapter)

        // задержка, чтобы листенер не отрабатывал на пустом запросе при создании фрагмента
        Handler(Looper.getMainLooper()).postDelayed({
            binding.searchEditText.doAfterTextChanged { text ->
                val allStreamsTab = binding.tabLayout.getTabAt(1)
                allStreamsTab?.select()
                val query = text?.toString().orEmpty()
                store.accept(StreamsEvent.Ui.SearchStreamsByQuery(query))
                if (streamsListPagerAdapter.isAllStreamsListFragment()) {
                    streamsListPagerAdapter.allStreamsListFragment.searchQuery = query
                }
            }
        }, 100)

        binding.searchIcon.setOnClickListener {
            binding.searchEditText.requestFocus()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun createStore(): Store<StreamsEvent, StreamsEffect, StreamsState> {
        val streamsComponent = DaggerStreamsComponent.factory().create(
            (activity?.application as App).applicationComponent
        )
        streamsComponent.inject(this)
        return streamsElmStoreFactory.provide()
    }

    override fun render(state: StreamsState) {
        if (streamsListPagerAdapter.isAllStreamsListFragment()
            && streamsListPagerAdapter.allStreamsListFragment.searchQuery.isNotEmpty()) {
            streamsListPagerAdapter.allStreamsListFragment.updateStreams(
                state.items
            )
        }
    }

    override fun handleEffect(effect: StreamsEffect) {
        when(effect) {
            is StreamsEffect.StreamsListLoadError -> {
                (binding.pager.adapter as? StreamsListPagerAdapter)
                    ?.allStreamsListFragment?.updateStreams(listOf())

                store.accept(StreamsEvent.Ui.SubscribeOnSearchStreamsEvents)

                Toast.makeText(
                    context,
                    resources.getString(R.string.search_streams_error_text),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
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

}
