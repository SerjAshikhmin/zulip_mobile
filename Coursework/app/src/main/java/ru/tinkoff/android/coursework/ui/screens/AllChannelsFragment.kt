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
import ru.tinkoff.android.coursework.databinding.FragmentAllChannelsBinding
import ru.tinkoff.android.coursework.model.Topic
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListAdapter
import ru.tinkoff.android.coursework.ui.screens.adapters.OnTopicItemClickListener
import ru.tinkoff.android.coursework.ui.screens.utils.showSnackBarWithRetryAction

internal class AllChannelsFragment: Fragment(), OnTopicItemClickListener {

    private lateinit var binding: FragmentAllChannelsBinding
    private lateinit var compositeDisposable: CompositeDisposable

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
        compositeDisposable = CompositeDisposable()
        configureAllChannelsRecyclerAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }

    override fun onTopicItemClickListener(topicItemView: View?, topic: Topic) {
        topicItemView?.setOnClickListener {
            val bundle = bundleOf(
                ChatActivity.TOPIC_NAME_KEY to topic.name
            )
            NavHostFragment.findNavController(binding.root.findFragment())
                .navigate(R.id.action_nav_channels_to_nav_chat, bundle)
        }
    }

    private fun configureAllChannelsRecyclerAdapter() {
        val adapter = ChannelsListAdapter(this)

        NetworkService.getZulipJsonApi().getAllStreams()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onSuccess = {
                    adapter.showShimmer = false
                    adapter.channels = it.streams
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
                    ) { configureAllChannelsRecyclerAdapter() }
                }
            )
            .addTo(compositeDisposable)

        binding.allChannelsList.adapter = adapter
    }

}
