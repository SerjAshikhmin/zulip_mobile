package ru.tinkoff.android.coursework.ui.screens

import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.api.NetworkService
import ru.tinkoff.android.coursework.api.model.ChannelDto
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListAdapter
import ru.tinkoff.android.coursework.ui.screens.utils.showSnackBarWithRetryAction

internal class AllChannelsFragment: ChannelsListFragment() {

    override fun loadChannelsFromApi(adapter: ChannelsListAdapter) {
        NetworkService.getZulipJsonApi().getAllStreams()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    with(adapter) {
                        showShimmer = false
                        channels = it.streams
                        it.streams.forEach { channel ->
                            getTopicsInChannel(channel)
                        }
                        notifyDataSetChanged()
                    }
                },
                onError = {
                    with(adapter) {
                        showShimmer = false
                        channels = listOf()
                        notifyDataSetChanged()
                    }

                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.channels_not_found_error_text),
                        Snackbar.LENGTH_LONG
                    ) { configureChannelsListRecyclerAdapter() }
                }
            )
            .addTo(compositeDisposable)
    }

    fun updateChannels(newChannels: List<ChannelDto>) {
        (binding.channelsList.adapter as ChannelsListAdapter).apply {
            showShimmer = false
            channels = newChannels
            notifyDataSetChanged()
        }
    }

}
