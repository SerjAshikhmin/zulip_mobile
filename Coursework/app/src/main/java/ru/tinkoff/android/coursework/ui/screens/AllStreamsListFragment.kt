package ru.tinkoff.android.coursework.ui.screens

import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.api.NetworkService
import ru.tinkoff.android.coursework.api.model.StreamDto
import ru.tinkoff.android.coursework.utils.showSnackBarWithRetryAction

internal class AllStreamsListFragment: StreamsListFragment() {

    override fun loadStreamsFromApi() {
        NetworkService.getZulipJsonApi().getAllStreams()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    with(adapter) {
                        showShimmer = false
                        streams = it.streams
                        it.streams.forEach { stream ->
                            getTopicsInStream(stream)
                        }
                        notifyDataSetChanged()
                    }
                },
                onError = {
                    with(adapter) {
                        showShimmer = false
                        streams = listOf()
                        notifyDataSetChanged()
                    }

                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.streams_not_found_error_text),
                        Snackbar.LENGTH_LONG
                    ) { configureStreamsListRecyclerAdapter() }
                }
            )
            .addTo(compositeDisposable)
    }

    fun updateStreams(newStreams: List<StreamDto>) {
        adapter.apply {
            showShimmer = false
            streams = newStreams
            notifyDataSetChanged()
        }
    }

}
