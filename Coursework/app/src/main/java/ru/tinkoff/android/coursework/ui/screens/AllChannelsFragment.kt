package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.channelsTestData
import ru.tinkoff.android.coursework.databinding.FragmentAllChannelsBinding
import ru.tinkoff.android.coursework.model.Topic
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListAdapter
import ru.tinkoff.android.coursework.ui.screens.adapters.OnTopicItemClickListener

internal class AllChannelsFragment: Fragment(), OnTopicItemClickListener {

    private lateinit var binding: FragmentAllChannelsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllChannelsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureChannelListRecycler()
    }

    override fun onTopicItemClickListener(topicItemView: View?, topic: Topic) {
        topicItemView?.setOnClickListener {
            val bundle = bundleOf(
                ChatActivity.CHANNEL_NAME_KEY to topic.channelName,
                ChatActivity.TOPIC_NAME_KEY to topic.name
            )
            NavHostFragment.findNavController(binding.root.findFragment())
                .navigate(R.id.action_nav_channels_to_nav_chat, bundle)
        }
    }

    private fun configureChannelListRecycler() {
        binding.channelsList.adapter = ChannelsListAdapter(this).apply { channels = channelsTestData }
    }

}
