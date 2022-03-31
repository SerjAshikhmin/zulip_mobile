package ru.tinkoff.android.coursework.ui.screens.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.databinding.ItemChannelInListBinding
import ru.tinkoff.android.coursework.model.Channel

internal class ChannelsListAdapter(private val topicItemClickListener: OnTopicItemClickListener)
    : RecyclerView.Adapter<ChannelsListAdapter.ChannelListViewHolder>() {

    var showShimmer = true

    var channels: List<Channel>
        set(value) = differ.submitList(value)
        get() = differ.currentList

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
            holder.shimmerFrameLayout.startShimmer()
        } else {
            holder.shimmerFrameLayout.stopShimmer()
            holder.shimmerFrameLayout.setShimmer(null)
            holder.channelName.foreground = null
            holder.arrowIcon.foreground = null

            val channel = channels[position]
            holder.initChannelListener(channel)
            holder.bind(channel)
        }
    }

    override fun getItemCount(): Int {
        return if (showShimmer) SHIMMER_ITEM_COUNT else channels.size
    }

    inner class ChannelListViewHolder(private val binding: ItemChannelInListBinding): RecyclerView.ViewHolder(binding.root) {

        internal val channelName = binding.channelName
        internal val arrowIcon = binding.arrowIcon
        internal val shimmerFrameLayout = binding.shimmerLayout
        private var isOpened = false

        fun bind(channel: Channel) {
            channelName.text =
                binding.root.resources.getString(R.string.channel_name_text, channel.name)
        }

        fun initChannelListener(channel: Channel) {
            binding.root.setOnClickListener {
                val topItemAdapter = TopicItemAdapter(this@ChannelsListAdapter.topicItemClickListener)
                if (!isOpened) {
                    topItemAdapter.topics = channel.topics
                    arrowIcon.setImageResource(R.drawable.ic_arrow_up)
                    isOpened = true
                } else {
                    topItemAdapter.topics = listOf()
                    topItemAdapter.notifyDataSetChanged()
                    arrowIcon.setImageResource(R.drawable.ic_arrow_down)
                    isOpened = false
                }
                binding.topicsList.adapter = topItemAdapter
            }
        }
    }

    companion object {

        const val SHIMMER_ITEM_COUNT = 4
    }
}
