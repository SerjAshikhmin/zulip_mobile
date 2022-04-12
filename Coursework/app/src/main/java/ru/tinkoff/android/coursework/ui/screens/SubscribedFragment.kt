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
import ru.tinkoff.android.coursework.databinding.FragmentSubscribedBinding
import ru.tinkoff.android.coursework.api.model.Topic
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListAdapter
import ru.tinkoff.android.coursework.ui.screens.adapters.OnTopicItemClickListener
import ru.tinkoff.android.coursework.ui.screens.utils.showSnackBarWithRetryAction

internal class SubscribedFragment: CompositeDisposableFragment(), OnTopicItemClickListener {

    private lateinit var binding: FragmentSubscribedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        binding = FragmentSubscribedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureSubscribedChannelsRecyclerAdapter()
    }

    override fun onTopicItemClick(topic: Topic, channelName: String) {
        val bundle = bundleOf(
            ChatActivity.CHANNEL_NAME_KEY to channelName,
            ChatActivity.TOPIC_NAME_KEY to topic.name
        )
        NavHostFragment.findNavController(binding.root.findFragment())
            .navigate(R.id.action_nav_channels_to_nav_chat, bundle)
    }

    private fun configureSubscribedChannelsRecyclerAdapter() {
        val adapter = ChannelsListAdapter(this)

        NetworkService.getZulipJsonApi().getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onSuccess = {
                    adapter.apply {
                        showShimmer = false
                        channels = it.subscriptions
                        notifyDataSetChanged()
                    }
                },
                onError = {
                    adapter.apply {
                        showShimmer = false
                        channels = listOf()
                        notifyDataSetChanged()
                    }

                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.channels_not_found_error_text),
                        Snackbar.LENGTH_LONG
                    ) { configureSubscribedChannelsRecyclerAdapter() }
                }
            )
            .addTo(compositeDisposable)

        binding.channelsList.adapter = adapter
    }

}
