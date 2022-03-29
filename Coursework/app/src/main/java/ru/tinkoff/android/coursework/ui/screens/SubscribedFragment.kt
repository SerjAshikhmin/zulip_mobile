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
import ru.tinkoff.android.coursework.data.channelsWithTestErrorAndDelay
import ru.tinkoff.android.coursework.databinding.FragmentSubscribedBinding
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListAdapter

internal class SubscribedFragment: Fragment() {

    private lateinit var binding: FragmentSubscribedBinding
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubscribedBinding.inflate(inflater, container,false)
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

    private fun configureChannelListRecycler() {
        val channelListRecycle = binding.channelsList
        val layoutManager = LinearLayoutManager(context)
        channelListRecycle.layoutManager = layoutManager
        val adapter = ChannelsListAdapter()

        Single.fromCallable { channelsWithTestErrorAndDelay() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onSuccess = {
                    adapter.showShimmer = false
                    adapter.channels = it
                    adapter.notifyDataSetChanged()
                },
                onError = {
                    adapter.showShimmer = false
                    adapter.channels = mutableListOf()
                    adapter.notifyDataSetChanged()
                    Toast.makeText(context, "Channels not found", Toast.LENGTH_LONG).show()

                }
            )
            .addTo(compositeDisposable)

        channelListRecycle.adapter = adapter
    }
}
