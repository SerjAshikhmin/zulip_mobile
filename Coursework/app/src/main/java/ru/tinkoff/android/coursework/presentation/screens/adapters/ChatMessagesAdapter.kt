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
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi.Companion.NUMBER_OF_MESSAGES_BEFORE_ANCHOR
import ru.tinkoff.android.coursework.data.api.model.SELF_USER_ID
import ru.tinkoff.android.coursework.data.db.model.Message
import ru.tinkoff.android.coursework.presentation.customviews.*
import ru.tinkoff.android.coursework.utils.dpToPx
import ru.tinkoff.android.coursework.utils.getDateTimeFromTimestamp
import ru.tinkoff.android.coursework.utils.getFormattedContentFromHtml
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal class ChatMessagesAdapter(
    private val dialog: EmojiBottomSheetDialog,
    private val chatRecycler: RecyclerView,
    private val emojiClickListener: OnEmojiClickListener,
) : RecyclerView.Adapter<ChatMessagesAdapter.BaseViewHolder>() {

    var streamName = ""
    var topicName = ""

    var anchor = LAST_MESSAGE_ANCHOR

    var messagesWithDateSeparators: List<Any>
        set(value) {
            // переходим на последнее сообщение в чате, если было добавлено новое сообщение
            if (messages.isNotEmpty() && value.isNotEmpty() && messagesWithDateSeparators.isNotEmpty()
                && messagesWithDateSeparators.last() != value.last()) {
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
            messagesWithDateSeparators = insertDateSeparators(value)
        }

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
        val message = messagesWithDateSeparators[position]
        return when {
            message is LocalDate -> TYPE_SEND_DATE
            message is Message && message.userId == SELF_USER_ID -> TYPE_SELF_MESSAGE
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
                    return@setOnLongClickListener messageOnClickFunc(dialog, messageView)
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
                    return@setOnLongClickListener messageOnClickFunc(dialog, selfMessageView)
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
            else -> throw IllegalStateException("Wrong view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return when (holder) {
            is MessageViewHolder -> holder.bind(messagesWithDateSeparators[position] as Message)
            is SelfMessageViewHolder -> holder.bind(messagesWithDateSeparators[position] as Message)
            is SendDateViewHolder -> holder.bind(messagesWithDateSeparators[position] as LocalDate)
        }
    }

    fun updateWithNextPortion(newMessages: List<Message>, isFirstPortion: Boolean) {
        if (isFirstPortion) messages = mutableListOf()
        anchor = newMessages[0].id - 1
        val oldMessages = messagesWithDateSeparators
        messages = newMessages.plus(messages)

        val isLastChanged = !oldMessages.isNullOrEmpty()
                && messagesWithDateSeparators.last() != oldMessages.last()
        if (isLastChanged) notifyItemChanged(messagesWithDateSeparators.size - 1)
    }

    override fun getItemCount(): Int = messagesWithDateSeparators.size

    sealed class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class MessageViewHolder(private val messageView: MessageViewGroup) : BaseViewHolder(messageView) {

        private val avatar = messageView.binding.avatarImage
        private val username = messageView.binding.username
        private val messageTextView = messageView.binding.messageText
        private val emojiBox = messageView.binding.emojiBox

        internal fun bind(message: Message?) {
            message?.let {
                messageView.messageId = message.id
                username.text = it.userFullName
                messageTextView.text = getFormattedContentFromHtml(it.content)

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
                messageTextView.text = getFormattedContentFromHtml(it.content)
                fillEmojiBox(it, emojiBox)
            }
        }
    }

    class SendDateViewHolder(private val sendDateView: FrameLayout) : BaseViewHolder(sendDateView) {

        fun bind(sendDate: LocalDate?) {
            var sendDateStr =
                sendDate?.format(DateTimeFormatter.ofPattern("dd MMM"))?.replace(".", "")
            sendDateStr?.let {
                sendDateStr = it.replaceRange(
                    it.length - 3,
                    it.length - 2, it[it.length - 3].uppercaseChar().toString()
                )
            }
            (sendDateView.getChildAt(0) as TextView).text = sendDateStr
        }
    }

    private fun insertDateSeparators(messages: List<Message>): List<Any> {
        val messagesWithDateSeparators = mutableListOf<Any>()
        for (curIndex in messages.indices) {
            val curDate = getDateTimeFromTimestamp(messages[curIndex].timestamp).toLocalDate()
            if (curIndex == 0) {
                messagesWithDateSeparators.add(curDate)
            } else {
                val prevDate = getDateTimeFromTimestamp(messages[curIndex - 1].timestamp).toLocalDate()
                if (prevDate != curDate) {
                    messagesWithDateSeparators.add(curDate)
                }
            }
            messagesWithDateSeparators.add(messages[curIndex])
        }
        return messagesWithDateSeparators
    }

    private fun messageOnClickFunc(dialog: EmojiBottomSheetDialog, view: View): Boolean {
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
            this@ChatMessagesAdapter.dialog.show(addEmojiView)
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
            addEmojiView?.visibility = View.VISIBLE
        }
    }

    companion object {

        private const val DEFAULT_MARGIN_DP = 15
        private const val TYPE_MESSAGE = 0
        private const val TYPE_SELF_MESSAGE = 1
        private const val TYPE_SEND_DATE = 2
    }

}
