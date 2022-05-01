package ru.tinkoff.android.coursework.presentation.screens

import com.google.android.material.snackbar.Snackbar
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.domain.model.Stream
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEffect
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEvent
import ru.tinkoff.android.coursework.utils.showSnackBarWithRetryAction

internal class AllStreamsListFragment: StreamsListFragment() {

    override var initEvent: StreamsEvent = StreamsEvent.Ui.LoadAllStreamsList

    override fun handleEffect(effect: StreamsEffect) {
        super.handleEffect(effect)
        when(effect) {
            is StreamsEffect.StreamsListLoadError -> {
                binding.root.showSnackBarWithRetryAction(
                    resources.getString(R.string.streams_not_found_error_text),
                    Snackbar.LENGTH_LONG
                ) { store.accept(StreamsEvent.Ui.LoadAllStreamsList) }
            }
        }
    }

    fun updateStreams(newStreams: List<Stream>) {
        adapter.apply {
            showShimmer = false
            streams = newStreams
            notifyDataSetChanged()
        }
    }

}
