package ru.tinkoff.android.coursework.ui.screens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.databinding.ItemChannelInListBinding
import ru.tinkoff.android.coursework.api.model.ChannelDto

internal class ChannelsListAdapter(private val topicItemClickListener: OnTopicItemClickListener)
    : RecyclerView.Adapter<ChannelsListAdapter.ChannelListViewHolder>() {

    var showShimmer = true

    var channels: List<ChannelDto>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    private var compositeDisposable = CompositeDisposable()

    private val differ = AsyncListDiffer(this, DiffCallback())

    class DiffCallback: DiffUtil.ItemCallback<ChannelDto>() {

        override fun areItemsTheSame(oldItem: ChannelDto, newItem: ChannelDto): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: ChannelDto, newItem: ChannelDto): Boolean {
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

        fun bind(channel: ChannelDto) {
            channelName.text =
                binding.root.resources.getString(R.string.channel_name_text, channel.name)
        }

        fun initChannelListener(channel: ChannelDto) {
            binding.root.setOnClickListener {
                configureTopicItemAdapter(channel)
            }
        }

        private fun configureTopicItemAdapter(channel: ChannelDto) {
            val topItemAdapter = TopicItemAdapter(this@ChannelsListAdapter.topicItemClickListener)

            if (!isOpened) {
                with(topItemAdapter) {
                    showShimmer = false
                    topics = channel.topics
                    channelName = channel.name
                }
                arrowIcon.setImageResource(R.drawable.ic_arrow_up)
                isOpened = true
            } else {
                with(topItemAdapter) {
                    showShimmer = false
                    topics = listOf()
                }
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
