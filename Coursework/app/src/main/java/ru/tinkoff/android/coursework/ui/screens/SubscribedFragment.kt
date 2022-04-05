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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.api.NetworkService
import ru.tinkoff.android.coursework.databinding.FragmentSubscribedBinding
import ru.tinkoff.android.coursework.model.Topic
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListAdapter
import ru.tinkoff.android.coursework.ui.screens.adapters.OnTopicItemClickListener
import ru.tinkoff.android.coursework.ui.screens.utils.showSnackBarWithRetryAction

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
        configureSubscribedChannelsRecyclerAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }

    override fun onTopicItemClick(topicItemView: View?, topic: Topic, channelName: String) {
        topicItemView?.setOnClickListener {
            val bundle = bundleOf(
                ChatActivity.TOPIC_NAME_KEY to topic.name,
                ChatActivity.CHANNEL_NAME_KEY to channelName
            )
            NavHostFragment.findNavController(binding.root.findFragment())
                .navigate(R.id.action_nav_channels_to_nav_chat, bundle)
        }
    }

    private fun configureSubscribedChannelsRecyclerAdapter() {
        val adapter = ChannelsListAdapter(this)

        NetworkService.getZulipJsonApi().getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onSuccess = {
                    adapter.showShimmer = false
                    adapter.channels = it.subscriptions
                    adapter.notifyDataSetChanged()
                },
                onError = {
                    adapter.showShimmer = false
                    adapter.channels = mutableListOf()
                    adapter.notifyDataSetChanged()

                    showSnackBarWithRetryAction(
                        binding.root,
                        "Channels not found",
                        Snackbar.LENGTH_LONG
                    ) { configureSubscribedChannelsRecyclerAdapter() }
                }
            )
            .addTo(compositeDisposable)

        binding.channelsList.adapter = adapter
    }

}
