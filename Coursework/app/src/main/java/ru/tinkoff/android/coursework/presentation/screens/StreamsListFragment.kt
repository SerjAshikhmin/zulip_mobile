package ru.tinkoff.android.coursework.presentation.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import ru.tinkoff.android.coursework.App
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.databinding.FragmentStreamsListBinding
import ru.tinkoff.android.coursework.di.ActivityScope
import ru.tinkoff.android.coursework.di.streams.DaggerStreamsComponent
import ru.tinkoff.android.coursework.presentation.elm.channels.StreamsElmStoreFactory
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEffect
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEvent
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsState
import ru.tinkoff.android.coursework.presentation.screens.listeners.OnStreamItemClickListener
import ru.tinkoff.android.coursework.presentation.screens.adapters.StreamsListAdapter
import ru.tinkoff.android.coursework.presentation.screens.listeners.OnTopicItemClickListener
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

@ActivityScope
internal abstract class StreamsListFragment
    : ElmFragment<StreamsEvent, StreamsEffect, StreamsState>(),
    OnStreamItemClickListener, OnTopicItemClickListener {

    @Inject
    internal lateinit var streamsElmStoreFactory: StreamsElmStoreFactory

    lateinit var binding: FragmentStreamsListBinding
    protected lateinit var adapter: StreamsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        binding = FragmentStreamsListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = StreamsListAdapter(this, this)
        binding.streamsListRecycler.adapter = adapter
        binding.createChannel.setOnClickListener {
            store.accept(StreamsEvent.Ui.CreateStreamInit)
        }
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
        if (!state.isLoading) {
            binding.swipeRefreshLayout.isRefreshing = false
        }
        with(adapter) {
            showShimmer = state.isLoading && !binding.swipeRefreshLayout.isRefreshing
            streams = state.items
            notifyDataSetChanged()
        }
    }

    override fun handleEffect(effect: StreamsEffect) {
        super.handleEffect(effect)
        when(effect) {
            is StreamsEffect.NavigateToChat -> {
                NavHostFragment.findNavController(binding.root.findFragment())
                    .navigate(R.id.action_nav_channels_to_nav_chat, effect.bundle)
            }
            is StreamsEffect.NavigateToCreateStream -> {
                NavHostFragment.findNavController(binding.root.findFragment())
                    .navigate(R.id.action_nav_channels_to_nav_create_stream)
            }
        }
    }

    override fun onTopicItemClick(topicName: String?, streamName: String?) {
        val bundle = bundleOf(
            ChatActivity.STREAM_NAME_KEY to streamName,
            ChatActivity.TOPIC_NAME_KEY to topicName
        )
        store.accept(StreamsEvent.Ui.LoadChat(bundle))
    }

    override fun onStreamItemClick(streamName: String) {
        val bundle = bundleOf(
            ChatActivity.STREAM_NAME_KEY to streamName,
            ChatActivity.TOPIC_NAME_KEY to ALL_TOPICS_IN_STREAM
        )
        store.accept(StreamsEvent.Ui.LoadChat(bundle))
    }

    companion object {

        internal const val ALL_TOPICS_IN_STREAM = "allstreamtopics"
    }

}
