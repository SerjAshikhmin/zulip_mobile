package ru.tinkoff.android.coursework.presentation.screens

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import ru.tinkoff.android.coursework.App
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.databinding.ActivityCreateStreamBinding
import ru.tinkoff.android.coursework.di.streams.DaggerStreamsComponent
import ru.tinkoff.android.coursework.presentation.elm.channels.StreamsElmStoreFactory
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEffect
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsEvent
import ru.tinkoff.android.coursework.presentation.elm.channels.models.StreamsState
import vivid.money.elmslie.android.base.ElmActivity
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

internal class CreateStreamActivity : ElmActivity<StreamsEvent, StreamsEffect, StreamsState>() {

    @Inject
    internal lateinit var streamsElmStoreFactory: StreamsElmStoreFactory

    override var initEvent: StreamsEvent = StreamsEvent.Ui.CreateStreamInit
    private lateinit var binding: ActivityCreateStreamBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStreamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createButton.setOnClickListener {
            store.accept(
                StreamsEvent.Ui.CreateStreamRequest(
                    binding.streamNameEditText.text.toString(),
                    binding.streamDescriptionEditText.text.toString(),
                    binding.privateSwitch.isChecked
                )
            )
        }

        binding.backIcon.setOnClickListener {
            onBackPressed()
        }

        binding.streamNameEditText.doAfterTextChanged {
            if (it != null) {
                binding.createButton.isEnabled = it.isNotBlank()
            }
        }
    }

    override fun createStore(): Store<StreamsEvent, StreamsEffect, StreamsState> {
        val streamsComponent = DaggerStreamsComponent.factory().create(
            (this.application as App).applicationComponent
        )
        streamsComponent.inject(this)
        return streamsElmStoreFactory.provide()
    }

    override fun render(state: StreamsState) { }

    override fun handleEffect(effect: StreamsEffect) {
        when(effect) {
            is StreamsEffect.StreamCreated -> {
                onBackPressed()
            }
            is StreamsEffect.StreamCreationError -> {
                Log.e(TAG, resources.getString(R.string.stream_creation_error_text), effect.error)
                Toast.makeText(
                    this,
                    resources.getString(R.string.stream_creation_error_text),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    companion object {

        private const val TAG = "CreateStreamActivity"
    }

}
