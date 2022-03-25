package ru.tinkoff.android.coursework.ui.screens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.model.Channel
import ru.tinkoff.android.coursework.model.Topic

internal class ChannelsListAdapter: RecyclerView.Adapter<ChannelsListAdapter.ChannelListViewHolder>() {

    internal var channels: List<Channel>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    private val differ = AsyncListDiffer(this, DiffCallback())

    internal class DiffCallback: DiffUtil.ItemCallback<Channel>() {
        override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem == newItem
        }
    }

    internal class ChannelListViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        private val channelName = view.findViewById<TextView>(R.id.channel_name)
        private val arrowIcon = view.findViewById<ImageView>(R.id.arrow_icon)
        private val topicContainer = view.findViewById<LinearLayout>(R.id.topic_container)
        private var isOpened = false

        fun bind(channel: Channel) {
            channelName.text = view.resources.getString(R.string.channel_name_text, channel.name)
            initChannelListener(channel)
        }

        private fun initChannelListener(channel: Channel) {
            view.setOnClickListener {
                if (!isOpened) {
                    channel.topics.forEach { topic ->
                        val topicItemView = LayoutInflater.from(view.context)
                            .inflate(R.layout.item_topic_in_list, view as ViewGroup, false)
                        topicItemView.findViewById<TextView>(R.id.topic_name).text = topic.name
                        topicItemView.findViewById<TextView>(R.id.messages_count).text =
                            view.resources.getString(
                                R.string.messages_count_text,
                                topic.messages.size.toString()
                            )
                        topicItemView.setBackgroundColor(ContextCompat.getColor(view.context, topic.color))
                        initTopicListener(topicItemView, channel, topic)
                        topicContainer.addView(topicItemView)
                        val separatorView = LayoutInflater.from(view.context)
                            .inflate(R.layout.fragment_item_in_list_separator, view, false)
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
                    "channelName" to channel.name,
                    "topicName" to topic.name
                )
                NavHostFragment.findNavController(view.findFragment())
                    .navigate(R.id.nav_chat, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelListViewHolder {
        val channelItemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_channel_in_list, parent, false)
        return ChannelListViewHolder(channelItemView)
    }

    override fun onBindViewHolder(holder: ChannelListViewHolder, position: Int) {
        holder.bind(channels[position])
    }

    override fun getItemCount(): Int = channels.size
}
