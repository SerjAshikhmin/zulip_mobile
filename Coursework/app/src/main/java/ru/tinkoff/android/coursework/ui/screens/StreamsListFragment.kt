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
import ru.tinkoff.android.coursework.api.model.StreamDto
import ru.tinkoff.android.coursework.api.model.TopicDto
import ru.tinkoff.android.coursework.databinding.FragmentStreamsListBinding
import ru.tinkoff.android.coursework.db.AppDatabase
import ru.tinkoff.android.coursework.db.model.Stream
import ru.tinkoff.android.coursework.db.model.toStreamsDtoList
import ru.tinkoff.android.coursework.ui.screens.adapters.StreamsListAdapter
import ru.tinkoff.android.coursework.ui.screens.adapters.OnTopicItemClickListener
import ru.tinkoff.android.coursework.utils.showSnackBarWithRetryAction

internal abstract class StreamsListFragment: CompositeDisposableFragment(), OnTopicItemClickListener {

    lateinit var binding: FragmentStreamsListBinding
    protected lateinit var adapter: StreamsListAdapter
    private var db: AppDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        binding = FragmentStreamsListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getAppDatabase(requireContext())
        configureStreamsListRecyclerAdapter()
    }

    override fun onTopicItemClick(topic: TopicDto, streamName: String) {
        val bundle = bundleOf(
            ChatActivity.STREAM_NAME_KEY to streamName,
            ChatActivity.TOPIC_NAME_KEY to topic.name
        )
        NavHostFragment.findNavController(binding.root.findFragment())
            .navigate(R.id.action_nav_channels_to_nav_chat, bundle)
    }

    abstract fun loadStreamsFromApi()

    fun configureStreamsListRecyclerAdapter() {
        adapter = StreamsListAdapter(this)

        loadStreamsFromDb()
        loadStreamsFromApi()

        binding.streamsList.adapter = adapter
    }

    fun getTopicsInStream(stream: StreamDto) {
        NetworkService.getZulipJsonApi().getTopicsInStream(streamId = stream.streamId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    stream.topics = it.topics
                    saveStreamsToDb(stream.toStreamDb())
                },
                onError = {
                    stream.topics = listOf()

                    binding.root.showSnackBarWithRetryAction(
                        binding.root.resources.getString(R.string.topics_not_found_error_text),
                        Snackbar.LENGTH_LONG
                    ) { getTopicsInStream(stream) }
                }
            )
            .addTo(compositeDisposable)
    }

    private fun loadStreamsFromDb() {
        db?.streamDao()?.getAll()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeBy(
                onSuccess = {
                    if (it.isNotEmpty()) {
                        with(adapter) {
                            showShimmer = false
                            streams = it.toStreamsDtoList()
                            notifyDataSetChanged()
                        }
                    }
                },
                onError = {
                    Log.e(TAG, resources.getString(R.string.loading_streams_from_db_error_text), it)
                }
            )
            ?.addTo(compositeDisposable)
    }

    private fun saveStreamsToDb(stream: Stream) {
        db?.streamDao()?.save(stream)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeBy(
                onError = {
                    Log.e(TAG, resources.getString(R.string.saving_streams_to_db_error_text), it)
                }
            )
            ?.addTo(compositeDisposable)
    }

    companion object {

        private const val TAG = "StreamsListFragment"
    }

}
