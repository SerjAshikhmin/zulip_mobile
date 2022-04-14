package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.util.Log
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
import ru.tinkoff.android.coursework.api.model.ChannelDto
import ru.tinkoff.android.coursework.api.model.TopicDto
import ru.tinkoff.android.coursework.databinding.FragmentChannelsListBinding
import ru.tinkoff.android.coursework.db.AppDatabase
import ru.tinkoff.android.coursework.db.model.Channel
import ru.tinkoff.android.coursework.db.model.toChannelsDtoList
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListAdapter
import ru.tinkoff.android.coursework.ui.screens.adapters.OnTopicItemClickListener
import ru.tinkoff.android.coursework.ui.screens.utils.showSnackBarWithRetryAction

internal abstract class ChannelsListFragment: CompositeDisposableFragment(), OnTopicItemClickListener {

    lateinit var binding: FragmentChannelsListBinding
    var db: AppDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        binding = FragmentChannelsListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getAppDatabase(requireContext())
        configureChannelsListRecyclerAdapter()
    }

    override fun onTopicItemClick(topic: TopicDto, channelName: String) {
        val bundle = bundleOf(
            ChatActivity.CHANNEL_NAME_KEY to channelName,
            ChatActivity.TOPIC_NAME_KEY to topic.name
        )
        NavHostFragment.findNavController(binding.root.findFragment())
            .navigate(R.id.action_nav_channels_to_nav_chat, bundle)
    }

    abstract fun loadChannelsFromApi(adapter: ChannelsListAdapter)

    fun configureChannelsListRecyclerAdapter() {
        val adapter = ChannelsListAdapter(this)

        loadChannelsFromDb(adapter)
        loadChannelsFromApi(adapter)

        binding.channelsList.adapter = adapter
    }

    fun getTopicsInChannel(channel: ChannelDto) {
        NetworkService.getZulipJsonApi().getTopicsInStream(streamId = channel.streamId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    channel.topics = it.topics
                    saveChannelToDb(channel.toChannelDb())
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

    private fun loadChannelsFromDb(adapter: ChannelsListAdapter) {
        db?.channelDao()?.getAll()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeBy(
                onSuccess = {
                    if (it.isNotEmpty()) {
                        with(adapter) {
                            showShimmer = false
                            channels = it.toChannelsDtoList()
                            notifyDataSetChanged()
                        }
                    }
                },
                onError = {
                    Log.e(TAG, resources.getString(R.string.loading_channels_from_db_error_text), it)
                }
            )
            ?.addTo(compositeDisposable)
    }

    private fun saveChannelToDb(channel: Channel) {
        db?.channelDao()?.save(channel)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeBy(
                onError = {
                    Log.e(TAG, resources.getString(R.string.saving_channels_to_db_error_text), it)
                }
            )
            ?.addTo(compositeDisposable)
    }

    companion object {

        private const val TAG = "ChannelsListFragment"
    }

}
