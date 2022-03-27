package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.data.channels
import ru.tinkoff.android.coursework.databinding.FragmentAllChannelsBinding
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListAdapter

internal class AllChannelsFragment: Fragment() {

    private lateinit var binding: FragmentAllChannelsBinding
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllChannelsBinding.inflate(inflater, container,false)

        configureChannelListRecycler()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }

    private fun configureChannelListRecycler() {
        val channelListRecycle = binding.allChannelsList
        val layoutManager = LinearLayoutManager(context)
        channelListRecycle.layoutManager = layoutManager
        val adapter = ChannelsListAdapter()

        Single.just(channels)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onSuccess = { adapter.channels = it },
                onError = {
                    Toast.makeText(context, "Channels not found", Toast.LENGTH_LONG).show()
                }
            )
            .addTo(compositeDisposable)

        channelListRecycle.adapter = adapter
    }
}
