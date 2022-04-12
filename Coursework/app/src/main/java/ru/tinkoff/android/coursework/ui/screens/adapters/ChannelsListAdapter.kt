package ru.tinkoff.android.coursework.ui.screens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.api.NetworkService
import ru.tinkoff.android.coursework.databinding.ItemChannelInListBinding
import ru.tinkoff.android.coursework.api.model.Channel
import ru.tinkoff.android.coursework.ui.screens.utils.showSnackBarWithRetryAction

internal class ChannelsListAdapter(private val topicItemClickListener: OnTopicItemClickListener)
    : RecyclerView.Adapter<ChannelsListAdapter.ChannelListViewHolder>() {

    var showShimmer = true

    var channels: List<Channel>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    private var compositeDisposable = CompositeDisposable()

    private val differ = AsyncListDiffer(this, DiffCallback())

    class DiffCallback: DiffUtil.ItemCallback<Channel>() {

        override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelListViewHolder {
        val channelItemBinding = ItemChannelInListBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ChannelListViewHolder(channelItemBinding)
    }

    override fun onBindViewHolder(holder: ChannelListViewHolder, position: Int) {
        if (showShimmer) {
            holder.shimmedText.visibility = View.VISIBLE
            holder.shimmerFrameLayout.startShimmer()
        } else {
            holder.shimmerFrameLayout.stopShimmer()
            holder.shimmerFrameLayout.setShimmer(null)
            holder.shimmedText.visibility = View.GONE
            holder.channelName.visibility = View.VISIBLE

            val channel = channels[position]
            holder.initChannelListener(channel)
            holder.bind(channel)
        }
    }

    override fun getItemCount(): Int {
        return if (showShimmer) SHIMMER_ITEM_COUNT else channels.size
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        compositeDisposable.dispose()
    }

    inner class ChannelListViewHolder(private val binding: ItemChannelInListBinding)
        : RecyclerView.ViewHolder(binding.root) {

        internal val channelName = binding.channelName
        internal val shimmedText = binding.shimmedText
        internal val shimmerFrameLayout = binding.shimmerLayout
        private val arrowIcon = binding.arrowIcon
        private var isOpened = false

        fun bind(channel: Channel) {
            channelName.text =
                binding.root.resources.getString(R.string.channel_name_text, channel.name)
        }

        fun initChannelListener(channel: Channel) {
            binding.root.setOnClickListener {
                configureTopicItemAdapter(channel)
            }
        }

        private fun configureTopicItemAdapter(channel: Channel) {
            val topItemAdapter = TopicItemAdapter(this@ChannelsListAdapter.topicItemClickListener)

            if (!isOpened) {
                // TODO убрать во ViewModel
                NetworkService.getZulipJsonApi().getTopicsInStream(streamId = channel.streamId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onSuccess = {
                            topItemAdapter.showShimmer = false
                            topItemAdapter.topics = it.topics
                            topItemAdapter.channelName = channel.name
                            topItemAdapter.notifyDataSetChanged()
                        },
                        onError = {
                            topItemAdapter.showShimmer = false
                            topItemAdapter.topics = listOf()
                            topItemAdapter.notifyDataSetChanged()

                            binding.root.showSnackBarWithRetryAction(
                                binding.root.resources.getString(R.string.sending_message_error_text),
                                Snackbar.LENGTH_LONG
                            ) { configureTopicItemAdapter(channel) }
                        }
                    )
                    .addTo(compositeDisposable)

                arrowIcon.setImageResource(R.drawable.ic_arrow_up)
                isOpened = true
            } else {
                topItemAdapter.showShimmer = false
                topItemAdapter.topics = listOf()
                topItemAdapter.notifyDataSetChanged()
                arrowIcon.setImageResource(R.drawable.ic_arrow_down)
                isOpened = false
            }
            binding.topicsList.adapter = topItemAdapter
        }
    }

    companion object {

        const val SHIMMER_ITEM_COUNT = 4
    }

}
