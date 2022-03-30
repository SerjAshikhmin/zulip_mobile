package ru.tinkoff.android.coursework.ui.screens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.databinding.ItemChannelInListBinding
import ru.tinkoff.android.coursework.databinding.ItemTopicInListBinding
import ru.tinkoff.android.coursework.model.Channel
import ru.tinkoff.android.coursework.model.Topic
import ru.tinkoff.android.coursework.ui.screens.ChatActivity.Companion.CHANNEL_NAME_KEY
import ru.tinkoff.android.coursework.ui.screens.ChatActivity.Companion.TOPIC_NAME_KEY

internal class ChannelsListAdapter: RecyclerView.Adapter<ChannelsListAdapter.ChannelListViewHolder>() {

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
        val channel = channels[position]
        holder.initChannelListener(channel)
        holder.bind(channel)
    }

    override fun getItemCount(): Int = channels.size

    class ChannelListViewHolder(private val binding: ItemChannelInListBinding): RecyclerView.ViewHolder(binding.root) {

        private val channelName = binding.channelName
        private val arrowIcon = binding.arrowIcon
        private val topicContainer = binding.topicContainer
        private var isOpened = false

        fun bind(channel: Channel) {
            channelName.text = binding.root.resources.getString(R.string.channel_name_text, channel.name)
        }

        fun initChannelListener(channel: Channel) {
            binding.root.setOnClickListener {
                if (!isOpened) {
                    channel.topics.forEach { topic ->
                        val topicItemBinding = ItemTopicInListBinding
                            .inflate(LayoutInflater.from(binding.root.context), binding.root as ViewGroup, false)
                        topicItemBinding.topicName.text = topic.name
                        topicItemBinding.messagesCount.text =
                            binding.root.resources.getString(
                                R.string.messages_count_text,
                                topic.messages.size.toString()
                            )
                        topicItemBinding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, topic.color))
                        initTopicListener(topicItemBinding.root, channel, topic)
                        topicContainer.addView(topicItemBinding.root)
                        val separatorView = LayoutInflater.from(binding.root.context)
                            .inflate(R.layout.fragment_item_in_list_separator, binding.root, false)
                        topicContainer.addView(separatorView)
                    }
                    arrowIcon.setImageResource(R.drawable.ic_arrow_up)
                    isOpened = true
                } else {
                    topicContainer.removeAllViews()
                    arrowIcon.setImageResource(R.drawable.ic_arrow_down)
                    isOpened = false
                }
            }
        }

        private fun initTopicListener(topicItemView: View?, channel: Channel, topic: Topic) {
            topicItemView?.setOnClickListener {
                val bundle = bundleOf(
                    CHANNEL_NAME_KEY to channel.name,
                    TOPIC_NAME_KEY to topic.name
                )
                NavHostFragment.findNavController(binding.root.findFragment())
                    .navigate(R.id.action_nav_channels_to_nav_chat, bundle)
            }
        }
    }

}
