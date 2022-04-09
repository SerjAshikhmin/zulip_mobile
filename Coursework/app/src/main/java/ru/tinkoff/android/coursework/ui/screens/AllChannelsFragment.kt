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
import ru.tinkoff.android.coursework.data.channelsWithTestErrorAndDelay
import ru.tinkoff.android.coursework.databinding.FragmentAllChannelsBinding
import ru.tinkoff.android.coursework.model.Topic
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListAdapter
import ru.tinkoff.android.coursework.ui.screens.adapters.OnTopicItemClickListener

internal class AllChannelsFragment: CompositeDisposableFragment(), OnTopicItemClickListener {

    lateinit var binding: FragmentAllChannelsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        binding = FragmentAllChannelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureChannelListRecycler()
    }

    override fun onTopicItemClickListener(topic: Topic) {
        val bundle = bundleOf(
            ChatActivity.CHANNEL_NAME_KEY to topic.channelName,
            ChatActivity.TOPIC_NAME_KEY to topic.name
        )
        NavHostFragment.findNavController(binding.root.findFragment())
            .navigate(R.id.action_nav_channels_to_nav_chat, bundle)
    }

    private fun configureChannelListRecycler() {
        val adapter = ChannelsListAdapter(this)

        channelsWithTestErrorAndDelay()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onSuccess = {
                    adapter.apply {
                        showShimmer = false
                        channels = it
                        notifyDataSetChanged()
                    }
                },
                onError = {
                    adapter.apply {
                        showShimmer = false
                        channels = listOf()
                        notifyDataSetChanged()
                    }

                    showSnackBarWithRetryAction(
                        binding.root,
                        resources.getString(R.string.channels_not_found_error_text),
                        Snackbar.LENGTH_LONG
                    ) { configureChannelListRecycler() }
                }
            )
            .addTo(compositeDisposable)

        binding.allChannelsList.adapter = adapter
    }

}
