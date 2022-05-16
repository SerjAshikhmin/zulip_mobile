package ru.tinkoff.android.coursework.presentation.screens

import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEffect
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEvent
import ru.tinkoff.android.coursework.utils.showSnackBarWithRetryAction

internal class SubscribedStreamsListFragment: StreamsListFragment() {

    override var initEvent: StreamsEvent = StreamsEvent.Ui.InitEvent

    override fun handleEffect(effect: StreamsEffect) {
        super.handleEffect(effect)
        when(effect) {
            is StreamsEffect.StreamsListLoadError -> {
                binding.root.showSnackBarWithRetryAction(
                    resources.getString(R.string.streams_not_found_error_text),
                    Snackbar.LENGTH_LONG
                ) { store.accept(StreamsEvent.Ui.LoadSubscribedStreamsList) }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        store.accept(StreamsEvent.Ui.LoadSubscribedStreamsList)

        binding.swipeRefreshLayout.setOnRefreshListener {
            store.accept(StreamsEvent.Ui.UpdateSubscribedStreamsList)
        }
    }

}
