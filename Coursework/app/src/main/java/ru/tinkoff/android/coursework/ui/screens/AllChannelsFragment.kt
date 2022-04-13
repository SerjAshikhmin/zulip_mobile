package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.api.NetworkService
import ru.tinkoff.android.coursework.databinding.FragmentAllChannelsBinding
import ru.tinkoff.android.coursework.api.model.Channel
import ru.tinkoff.android.coursework.api.model.Topic
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListAdapter
import ru.tinkoff.android.coursework.ui.screens.adapters.OnTopicItemClickListener
import ru.tinkoff.android.coursework.ui.screens.utils.showSnackBarWithRetryAction

internal class AllChannelsFragment: CompositeDisposableFragment(), OnTopicItemClickListener {

    lateinit var binding: FragmentAllChannelsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllChannelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureAllChannelsRecyclerAdapter()
    }

    override fun onTopicItemClick(topic: Topic, channelName: String) {
        val bundle = bundleOf(
            ChatActivity.CHANNEL_NAME_KEY to channelName,
            ChatActivity.TOPIC_NAME_KEY to topic.name
        )
        NavHostFragment.findNavController(binding.root.findFragment())
            .navigate(R.id.action_nav_channels_to_nav_chat, bundle)
    }

    fun updateChannels(newChannels: List<Channel>) {
        (binding.allChannelsList.adapter as ChannelsListAdapter).apply {
            showShimmer = false
            channels = newChannels
            notifyDataSetChanged()
        }
    }

    private fun configureAllChannelsRecyclerAdapter() {
        val adapter = ChannelsListAdapter(this)

        NetworkService.getZulipJsonApi().getAllStreams()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onSuccess = {
                    adapter.apply {
                        showShimmer = false
                        channels = it.streams
                        channels.forEach { channel ->
                            getTopicsInChannel(channel)
                        }
                        notifyDataSetChanged()
                    }
                },
                onError = {
                    adapter.apply {
                        showShimmer = false
                        channels = listOf()
                        notifyDataSetChanged()
                    }

                    it.printStackTrace()
                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.channels_not_found_error_text),
                        Snackbar.LENGTH_LONG
                    ) { configureAllChannelsRecyclerAdapter() }
                }
            )
            .addTo(compositeDisposable)

        binding.allChannelsList.adapter = adapter
    }

    // TODO перенести отсюда/убрать дублирование в ДЗ по архитектуре
    private fun getTopicsInChannel(channel: Channel) {
        NetworkService.getZulipJsonApi().getTopicsInStream(streamId = channel.streamId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    channel.topics = it.topics
                },
                onError = {
                    channel.topics = listOf()

                    binding.root.showSnackBarWithRetryAction(
                        binding.root.resources.getString(R.string.topics_not_found_error_text),
                        Snackbar.LENGTH_LONG
                    ) { getTopicsInChannel(channel) }
                }
            )
            .addTo(compositeDisposable)
    }

}
