package ru.tinkoff.android.coursework.presentation.screens.adapters

import android.animation.LayoutTransition
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.databinding.ItemStreamInListBinding
import ru.tinkoff.android.coursework.domain.model.Stream
import ru.tinkoff.android.coursework.presentation.screens.listeners.OnStreamItemClickListener
import ru.tinkoff.android.coursework.presentation.screens.listeners.OnTopicItemClickListener

internal class StreamsListAdapter(
    private val streamItemClickListener: OnStreamItemClickListener,
    private val topicItemClickListener: OnTopicItemClickListener
) : RecyclerView.Adapter<StreamsListAdapter.StreamListViewHolder>() {

    var showShimmer = true

    var streams: List<Stream>
        set(value) = differ.submitList(value.sortedBy { it.name })
        get() = differ.currentList

    private val differ = AsyncListDiffer(this, DiffCallback())

    class DiffCallback: DiffUtil.ItemCallback<Stream>() {

        override fun areItemsTheSame(oldItem: Stream, newItem: Stream): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Stream, newItem: Stream): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamListViewHolder {
        val streamItemBinding = ItemStreamInListBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            (streamItemBinding.streamItem).layoutTransition
                .enableTransitionType(LayoutTransition.CHANGING)
        }
        return StreamListViewHolder(streamItemBinding)
    }

    override fun onBindViewHolder(holder: StreamListViewHolder, position: Int) {
        if (showShimmer) {
            holder.shimmedText.visibility = View.VISIBLE
            holder.shimmerFrameLayout.startShimmer()
        } else {
            holder.shimmerFrameLayout.stopShimmer()
            holder.shimmerFrameLayout.setShimmer(null)
            holder.shimmedText.visibility = View.GONE
            holder.streamNameTextView.visibility = View.VISIBLE

            val stream = streams[position]
            holder.initOpenIconClickListener(stream)
            holder.bind(stream)
        }
    }

    override fun getItemCount(): Int {
        return if (showShimmer) SHIMMER_ITEM_COUNT else streams.size
    }

    inner class StreamListViewHolder(private val binding: ItemStreamInListBinding)
        : RecyclerView.ViewHolder(binding.root) {

        internal val streamNameTextView = binding.streamName
        internal val shimmedText = binding.shimmedText
        internal val shimmerFrameLayout = binding.shimmerLayout
        private val arrowIcon = binding.arrowIcon
        private lateinit var streamName: String

        fun bind(stream: Stream) {
            streamNameTextView.text =
                binding.root.resources.getString(R.string.stream_name_text, stream.name)
            streamName = stream.name
        }

        fun initOpenIconClickListener(stream: Stream) {
            val topItemAdapter: TopicItemAdapter
            if (binding.topicsList.adapter == null) {
                topItemAdapter = TopicItemAdapter(this@StreamsListAdapter.topicItemClickListener)
            } else {
                topItemAdapter = binding.topicsList.adapter as TopicItemAdapter
                if (stream.isOpenedInChannelsList) {
                    with(topItemAdapter) {
                        topics = stream.topics
                        streamName = stream.name
                    }
                } else {
                    topItemAdapter.topics = listOf()
                }
            }

            binding.root.setOnClickListener {
                this@StreamsListAdapter.streamItemClickListener.onStreamItemClick(streamName)
            }

            binding.arrowIcon.setOnClickListener {
                if (!stream.isOpenedInChannelsList) {
                    with(topItemAdapter) {
                        showShimmer = false
                        topics = stream.topics
                        streamName = stream.name
                    }
                    arrowIcon.setImageResource(R.drawable.ic_arrow_up)
                    stream.isOpenedInChannelsList = true
                } else {
                    with(topItemAdapter) {
                        showShimmer = false
                        topics = listOf()
                    }
                    arrowIcon.setImageResource(R.drawable.ic_arrow_down)
                    stream.isOpenedInChannelsList = false
                }
            }

            binding.topicsList.adapter = topItemAdapter
        }
    }

    companion object {

        const val SHIMMER_ITEM_COUNT = 3
    }

}
