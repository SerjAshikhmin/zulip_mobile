package ru.tinkoff.android.coursework.presentation.screens.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.databinding.ItemTopicInListBinding
import ru.tinkoff.android.coursework.domain.model.Topic
import ru.tinkoff.android.coursework.presentation.screens.listeners.OnTopicItemClickListener

internal class TopicItemAdapter(private val topicItemClickListener: OnTopicItemClickListener)
    : RecyclerView.Adapter<TopicItemAdapter.TopicItemViewHolder>() {

    var showShimmer = false
    var streamName = ""

    var topics: List<Topic>
        set(value) = differ.submitList(value.sortedBy { it.name })
        get() = differ.currentList

    private val differ = AsyncListDiffer(this, DiffCallback())

    class DiffCallback : DiffUtil.ItemCallback<Topic>() {

        override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicItemViewHolder {
        val topicItemBinding = ItemTopicInListBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return TopicItemViewHolder(topicItemBinding)
    }

    override fun onBindViewHolder(holder: TopicItemViewHolder, position: Int) {
        if (showShimmer) {
            holder.shimmerFrameLayout.startShimmer()
        } else {
            holder.shimmerFrameLayout.stopShimmer()
            holder.shimmerFrameLayout.setShimmer(null)
            holder.topicItem.foreground = null

            holder.bind(topics[position])
        }
    }

    override fun getItemCount(): Int {
        return if (showShimmer) SHIMMER_ITEM_COUNT else topics.size
    }

    inner class TopicItemViewHolder(private val binding: ItemTopicInListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        internal val shimmerFrameLayout = binding.shimmerLayout
        internal val topicItem = binding.topicItem

        fun bind(topic: Topic) {
            binding.topicName.text = topic.name
            binding.root.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    TOPIC_BACKGROUND_COLOR
                )
            )
            binding.root.setOnClickListener {
                topicItemClickListener.onTopicItemClick(topic.name, streamName)
            }
        }
    }

    companion object {

        const val SHIMMER_ITEM_COUNT = 3
        const val TOPIC_BACKGROUND_COLOR = R.color.teal_500
    }
}
