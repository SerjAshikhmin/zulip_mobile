package ru.tinkoff.android.coursework.presentation.screens.adapters

import android.util.LayoutDirection
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.setMargins
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi.Companion.LAST_MESSAGE_ANCHOR
import ru.tinkoff.android.coursework.data.api.model.SELF_USER_ID
import ru.tinkoff.android.coursework.domain.model.Message
import ru.tinkoff.android.coursework.presentation.customviews.*
import ru.tinkoff.android.coursework.presentation.screens.ChatActivity.Companion.NO_TOPIC_STRING_VALUE
import ru.tinkoff.android.coursework.presentation.screens.StreamsListFragment.Companion.ALL_TOPICS_IN_STREAM
import ru.tinkoff.android.coursework.presentation.screens.listeners.OnEmojiClickListener
import ru.tinkoff.android.coursework.presentation.screens.listeners.OnTopicItemClickListener
import ru.tinkoff.android.coursework.utils.dpToPx
import ru.tinkoff.android.coursework.utils.getDateTimeFromTimestamp
import ru.tinkoff.android.coursework.utils.getFormattedContentFromHtml
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal class ChatMessagesAdapter(
    private var actionsDialog: ChatActionsBottomSheetDialog,
    private val emojisDialog: EmojiBottomSheetDialog,
    private val chatRecycler: RecyclerView,
    private val emojiClickListener: OnEmojiClickListener,
    private val topicItemClickListener: OnTopicItemClickListener
) : RecyclerView.Adapter<ChatMessagesAdapter.BaseViewHolder>() {

    var streamNameValue = ""
    var topicNameValue = ""
    var streamNameText = ""
    var topicNameText = ""

    var anchor = LAST_MESSAGE_ANCHOR

    private var items: List<Any>
        set(value) {
            // переходим на последнее сообщение в чате, если было добавлено новое сообщение
            if (messages.isNotEmpty() && value.isNotEmpty() && items.isNotEmpty()
                && items.last() != value.last()) {
                differ.submitList(value) {
                    chatRecycler.scrollToPosition(value.size - 1)
                }
            } else {
                differ.submitList(value)
            }
        }
        get() = differ.currentList

    var messages: List<Message> = mutableListOf()
        set(value) {
            field = value
            items = insertDateSeparatorsAndTopicNames(value)
        }

    var topics: MutableSet<TopicName> = mutableSetOf()

    private val differ = AsyncListDiffer(this, DiffCallback())

    class DiffCallback : DiffUtil.ItemCallback<Any>() {

        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            if (oldItem is Message && newItem is Message) {
                return oldItem.id == newItem.id
            }
            if (oldItem is LocalDate && newItem is LocalDate) {
                return oldItem == newItem
            }
            return false
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            if (oldItem is Message && newItem is Message) {
                return oldItem == newItem
            }
            if (oldItem is LocalDate && newItem is LocalDate) {
                return oldItem == newItem
            }
            return false
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when {
            item is TopicName -> TYPE_TOPIC_NAME
            item is LocalDate -> TYPE_SEND_DATE
            item is Message && item.userId == SELF_USER_ID -> TYPE_SELF_MESSAGE
            else -> TYPE_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE_MESSAGE -> {
                val messageView = MessageViewGroup(parent.context)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(parent.context.dpToPx(DEFAULT_MARGIN_DP))
                messageView.layoutParams = layoutParams
                messageView.setOnLongClickListener {
                    return@setOnLongClickListener messageOnClickFunc(actionsDialog, messageView)
                }
                MessageViewHolder(messageView)
            }
            TYPE_SELF_MESSAGE -> {
                val selfMessageView = SelfMessageViewGroup(parent.context)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(parent.context.dpToPx(DEFAULT_MARGIN_DP))
                layoutParams.gravity = Gravity.END
                parent.layoutParams.resolveLayoutDirection(LayoutDirection.RTL)
                selfMessageView.layoutParams = layoutParams
                selfMessageView.setOnLongClickListener {
                    return@setOnLongClickListener messageOnClickFunc(actionsDialog, selfMessageView)
                }
                SelfMessageViewHolder(selfMessageView)
            }
            TYPE_SEND_DATE -> {
                val sendDateView = LayoutInflater.from(parent.context).inflate(
                    R.layout.view_send_date,
                    parent,
                    false
                ) as FrameLayout
                SendDateViewHolder(sendDateView)
            }
            TYPE_TOPIC_NAME -> {
                val topicNameView = LayoutInflater.from(parent.context).inflate(
                    R.layout.view_topic_name_in_chat,
                    parent,
                    false
                ) as TextView
                TopicNameViewHolder(topicNameView)
            }
            else -> throw IllegalStateException("Wrong view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return when (holder) {
            is MessageViewHolder -> holder.bind(items[position] as? Message)
            is SelfMessageViewHolder -> holder.bind(items[position] as? Message)
            is SendDateViewHolder -> holder.bind(items[position] as? LocalDate)
            is TopicNameViewHolder -> holder.bind(items[position] as? TopicName)
        }
    }

    override fun getItemCount(): Int = items.size

    sealed class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class MessageViewHolder(private val messageView: MessageViewGroup)
        : BaseViewHolder(messageView) {

        private val avatar = messageView.binding.avatarImage
        private val username = messageView.binding.username
        private val messageTextView = messageView.binding.messageText
        private val emojiBox = messageView.binding.emojiBox

        internal fun bind(message: Message?) {
            message?.let {
                messageView.messageId = message.id
                username.text = it.userFullName
                messageTextView.text = getFormattedContentFromHtml(it.content)?.trim()

                if (it.avatarUrl != null) {
                    Glide.with(messageTextView)
                        .asBitmap()
                        .load(it.avatarUrl)
                        .error(R.drawable.default_avatar)
                        .into(avatar)
                } else {
                    avatar.setImageResource(R.drawable.default_avatar)
                }

                fillEmojiBox(it, emojiBox)
            }
        }
    }

    inner class SelfMessageViewHolder(private val selfMessageView: SelfMessageViewGroup) :
        BaseViewHolder(selfMessageView) {

        private val messageTextView = selfMessageView.binding.message
        private val emojiBox = selfMessageView.binding.emojiBox

        fun bind(message: Message?) {
            message?.let {
                selfMessageView.messageId = message.id
                messageTextView.text = getFormattedContentFromHtml(it.content)?.trim()
                fillEmojiBox(it, emojiBox)
            }
        }
    }

    class SendDateViewHolder(private val sendDateView: FrameLayout)
        : BaseViewHolder(sendDateView) {

        fun bind(sendDate: LocalDate?) {
            var sendDateStr =
                sendDate?.format(DateTimeFormatter.ofPattern("dd MMM"))?.replace(".", "")
            sendDateStr?.let {
                sendDateStr = it.replaceRange(
                    it.length - 3,
                    it.length - 2, it[it.length - 3].uppercaseChar().toString()
                )
            }
            (sendDateView.getChildAt(0) as? TextView)?.text = sendDateStr
        }
    }

    inner class TopicNameViewHolder(private val topicNameView: TextView)
        : BaseViewHolder(topicNameView) {

        fun bind(topicName: TopicName?) {
            topicNameView.text = if (topicName?.name != NO_TOPIC_STRING_VALUE) {
                topicNameView.resources.getString(
                    R.string.topic_name_text,
                    topicName?.name
                )
            } else {
                topicName.name
            }
            if (this@ChatMessagesAdapter.topicNameValue == ALL_TOPICS_IN_STREAM) {
                topicNameView.setOnClickListener {
                    topicItemClickListener.onTopicItemClick(topicName?.name, streamNameValue)
                }
            }
        }
    }

    private fun insertDateSeparatorsAndTopicNames(messages: List<Message>): List<Any> {
        val items = mutableListOf<Any>()
        for (curIndex in messages.indices) {
            val curTopic = messages[curIndex].topicName
            val curDate = getDateTimeFromTimestamp(messages[curIndex].timestamp).toLocalDate()
            if (curIndex == 0) {
                val topic = TopicName(curTopic)
                items.add(topic)
                items.add(curDate)
                topics.add(topic)
            } else {
                val prevTopic = messages[curIndex - 1].topicName
                val prevDate = getDateTimeFromTimestamp(messages[curIndex - 1].timestamp).toLocalDate()
                if (prevTopic != curTopic) {
                    val topic = TopicName(curTopic)
                    items.add(topic)
                    topics.add(topic)
                }
                if (prevDate != curDate) {
                    items.add(curDate)
                }
            }
            items.add(messages[curIndex])
        }
        return items
    }

    private fun messageOnClickFunc(dialog: ChatActionsBottomSheetDialog, view: View): Boolean {
        dialog.show(view)
        return true
    }

    private fun fillEmojiBox(message: Message, emojiBox: FlexBoxLayout) {
        emojiBox.removeAllViews()
        val addEmojiView = LayoutInflater.from(emojiBox.context).inflate(
            R.layout.view_image_add_emoji,
            emojiBox,
            false
        ) as ImageView
        addEmojiView.setOnClickListener {
            this@ChatMessagesAdapter.emojisDialog.show(addEmojiView)
        }
        emojiBox.addView(addEmojiView)

        if (message.emojis.isNotEmpty()) {
            message.emojis.forEach { emoji ->
                val emojiView = EmojiWithCountView.createEmojiWithCountView(
                    emojiBox = emojiBox,
                    emoji = emoji,
                    messageId = message.id,
                    emojiClickListener = emojiClickListener
                )
                if (emoji.selectedByCurrentUser) emojiView.isSelected = true
                emojiBox.addView(emojiView, emojiBox.childCount - 1)
            }
            addEmojiView.visibility = View.VISIBLE
        }
    }

    companion object {

        private const val DEFAULT_MARGIN_DP = 15
        private const val TYPE_MESSAGE = 0
        private const val TYPE_SELF_MESSAGE = 1
        private const val TYPE_SEND_DATE = 2
        private const val TYPE_TOPIC_NAME = 3
    }

}
