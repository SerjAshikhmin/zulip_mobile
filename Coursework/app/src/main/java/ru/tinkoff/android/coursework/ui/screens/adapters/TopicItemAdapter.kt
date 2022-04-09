package ru.tinkoff.android.coursework.ui.screens.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.databinding.ItemTopicInListBinding
import ru.tinkoff.android.coursework.model.Topic

internal class TopicItemAdapter(private val topicItemClickListener: OnTopicItemClickListener)
    : RecyclerView.Adapter<TopicItemAdapter.TopicItemViewHolder>() {

    var topics: List<Topic>
        set(value) = differ.submitList(value)
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
        holder.bind(topics[position])
    }

    override fun getItemCount() = topics.size

    inner class TopicItemViewHolder(private val binding: ItemTopicInListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(topic: Topic) {
            binding.topicName.text = topic.name
            binding.messagesCount.text =
                binding.root.resources.getString(
                    R.string.messages_count_text,
                    topic.messages.size.toString()
                )
            binding.root.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    topic.color
                )
            )
            binding.root.setOnClickListener {
                this@TopicItemAdapter.topicItemClickListener.onTopicItemClickListener(topic)
            }
        }
    }

}
