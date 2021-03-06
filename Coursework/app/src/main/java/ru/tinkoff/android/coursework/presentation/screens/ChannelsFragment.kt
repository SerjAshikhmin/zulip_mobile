package ru.tinkoff.android.coursework.presentation.screens

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
    private var streamsListPagerAdapter: StreamsListPagerAdapter? = null
    private lateinit var binding: FragmentChannelsBinding

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

        // ????????????????, ?????????? ???????????????? ???? ?????????????????????? ???? ???????????? ?????????????? ?????? ???????????????? ??????????????????
        Handler(Looper.getMainLooper()).postDelayed({
            binding.searchEditText.doAfterTextChanged { text ->
                val allStreamsTab = binding.tabLayout.getTabAt(1)
                allStreamsTab?.select()
                val query = text?.toString().orEmpty()
                store.accept(StreamsEvent.Ui.SearchStreamsByQuery(query))
                if (streamsListPagerAdapter?.isAllStreamsListFragment() == true) {
                    streamsListPagerAdapter?.allStreamsListFragment?.searchQuery = query
                }
            }
        }, 100)

        binding.searchIcon.setOnClickListener {
            binding.searchEditText.requestFocus()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onResume() {
        super.onResume()
        configureViewPager()
        streamsListPagerAdapter = (binding.pager.adapter as StreamsListPagerAdapter)
    }

    override fun createStore(): Store<StreamsEvent, StreamsEffect, StreamsState> {
        val streamsComponent = DaggerStreamsComponent.factory().create(
            (activity?.application as App).applicationComponent,
            (activity?.application as App).networkComponent
        )
        streamsComponent.inject(this)
        return streamsElmStoreFactory.provide()
    }

    override fun render(state: StreamsState) {
        if (streamsListPagerAdapter?.isAllStreamsListFragment() == true
            && streamsListPagerAdapter?.allStreamsListFragment?.searchQuery?.isNotEmpty() == true
        ) {
            streamsListPagerAdapter?.allStreamsListFragment?.updateStreams(
                state.items
            )
        }
    }

    override fun handleEffect(effect: StreamsEffect) {
        when(effect) {
            is StreamsEffect.StreamsListLoadError -> {
                if (streamsListPagerAdapter?.isAllStreamsListFragment() == true) {
                    (binding.pager.adapter as? StreamsListPagerAdapter)
                        ?.allStreamsListFragment?.updateStreams(listOf())
                }
                store.accept(StreamsEvent.Ui.SubscribeOnSearchStreamsEvents)
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
