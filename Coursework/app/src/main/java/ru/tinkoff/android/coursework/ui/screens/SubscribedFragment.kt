package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.channelsWithTestErrorAndDelay
import ru.tinkoff.android.coursework.databinding.FragmentSubscribedBinding
import ru.tinkoff.android.coursework.model.Topic
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListAdapter
import ru.tinkoff.android.coursework.ui.screens.adapters.OnTopicItemClickListener

internal class SubscribedFragment: Fragment(), OnTopicItemClickListener {

    private lateinit var binding: FragmentSubscribedBinding
    private lateinit var compositeDisposable: CompositeDisposable

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
        compositeDisposable = CompositeDisposable()
        configureChannelListRecycler()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
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
                        channels = mutableListOf()
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

        binding.channelsList.adapter = adapter
    }

}
