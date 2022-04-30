package ru.tinkoff.android.coursework.presentation.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.api.model.TopicDto
import ru.tinkoff.android.coursework.databinding.FragmentStreamsListBinding
import ru.tinkoff.android.coursework.data.db.AppDatabase
import ru.tinkoff.android.coursework.di.GlobalDi
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEffect
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEvent
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsState
import ru.tinkoff.android.coursework.presentation.screens.adapters.StreamsListAdapter
import ru.tinkoff.android.coursework.presentation.screens.adapters.OnTopicItemClickListener
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store

internal abstract class StreamsListFragment
    : ElmFragment<StreamsEvent, StreamsEffect, StreamsState>(), OnTopicItemClickListener {

    lateinit var binding: FragmentStreamsListBinding
    protected lateinit var adapter: StreamsListAdapter
    private var db: AppDatabase? = null

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
        db = AppDatabase.getAppDatabase(requireContext())
        adapter = StreamsListAdapter(this)
        binding.streamsList.adapter = adapter
    }

    override fun createStore(): Store<StreamsEvent, StreamsEffect, StreamsState> {
        return GlobalDi.INSTANCE.streamsElmStoreFactory.provide()
    }

    override fun render(state: StreamsState) {
        with(adapter) {
            showShimmer = state.isLoading
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
        }
    }

    override fun onTopicItemClick(topic: TopicDto, streamName: String) {
        val bundle = bundleOf(
            ChatActivity.STREAM_NAME_KEY to streamName,
            ChatActivity.TOPIC_NAME_KEY to topic.name
        )
        store.accept(StreamsEvent.Ui.LoadChat(bundle))
    }

}
