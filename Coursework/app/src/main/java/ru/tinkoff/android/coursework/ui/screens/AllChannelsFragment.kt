package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.tinkoff.android.coursework.data.channels
import ru.tinkoff.android.coursework.databinding.FragmentAllChannelsBinding
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListAdapter

internal class AllChannelsFragment: Fragment() {

    private lateinit var binding: FragmentAllChannelsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllChannelsBinding.inflate(inflater,container,false)

        configureChannelListRecycler()
        return binding.root
    }

    private fun configureChannelListRecycler() {
        val channelListRecycle = binding.channelsList
        val layoutManager = LinearLayoutManager(context)
        channelListRecycle.layoutManager = layoutManager
        val adapter = ChannelsListAdapter()
        adapter.channels = channels
        channelListRecycle.adapter = adapter
    }
}
