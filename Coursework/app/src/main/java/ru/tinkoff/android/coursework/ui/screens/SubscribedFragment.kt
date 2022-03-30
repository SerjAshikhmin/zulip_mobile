package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.tinkoff.android.coursework.data.channelsTestData
import ru.tinkoff.android.coursework.databinding.FragmentSubscribedBinding
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListAdapter

internal class SubscribedFragment: Fragment() {

    private lateinit var binding: FragmentSubscribedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubscribedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureChannelListRecycler()
    }

    private fun configureChannelListRecycler() {
        binding.channelsList.adapter = ChannelsListAdapter().apply { channels = channelsTestData }
    }

}
