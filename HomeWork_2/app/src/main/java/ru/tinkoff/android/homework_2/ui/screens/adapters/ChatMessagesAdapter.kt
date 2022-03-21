package ru.tinkoff.android.homework_2.ui.screens.adapters

import android.content.Context
import android.util.DisplayMetrics
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
import ru.tinkoff.android.homework_2.R
import ru.tinkoff.android.homework_2.data.SELF_USER_ID
import ru.tinkoff.android.homework_2.data.getEmojisForMessage
import ru.tinkoff.android.homework_2.data.getUserById
import ru.tinkoff.android.homework_2.model.Message
import ru.tinkoff.android.homework_2.ui.customviews.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private const val TYPE_MESSAGE = 0
private const val TYPE_SELF_MESSAGE = 1
private const val TYPE_SEND_DATE = 2

class ChatMessagesAdapter(private val dialog: EmojiBottomSheetDialog)
    : RecyclerView.Adapter<ChatMessagesAdapter.BaseViewHolder>() {

    var messages: List<Any>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    private val differ = AsyncListDiffer(this, DiffCallback())

    class DiffCallback: DiffUtil.ItemCallback<Any>() {
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
        val item = messages[position]
        if (item is LocalDate) {
            return TYPE_SEND_DATE
        }
        return if (item is Message && item.userId == SELF_USER_ID) {
            TYPE_SELF_MESSAGE
        } else {
            TYPE_MESSAGE
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
                layoutParams.setMargins(dpToPx(DEFAULT_MARGIN_DP, parent.context))
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
                layoutParams.setMargins(dpToPx(DEFAULT_MARGIN_DP, parent.context))
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
        return when(holder) {
            is MessageViewHolder -> holder.bind(messages[position] as Message)
            is SelfMessageViewHolder -> holder.bind(messages[position] as Message)
            is SendDateViewHolder -> holder.bind(messages[position] as LocalDate)
        }
    }

    fun update(newMessages: List<Any>, position: Int) {
        messages = newMessages
        notifyItemInserted(position)
    }

    override fun getItemCount(): Int = messages.size

    sealed class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class MessageViewHolder(messageView: MessageViewGroup) : BaseViewHolder(messageView) {
        private val avatar = messageView.binding.avatarImage
        private val username = messageView.binding.username
        private val messageView = messageView.binding.messageText
        private val emojiBox = messageView.binding.emojiBox

        fun bind(message: Message?) {
            message?.let {
                val user = getUserById(it.userId)
                username.text = user?.name
                messageView.text = it.content
                fillEmojiBox(it, emojiBox)
            }
        }
    }

    inner class SelfMessageViewHolder(selfMessageView: SelfMessageViewGroup) :
        BaseViewHolder(selfMessageView) {
        private val messageView = selfMessageView.binding.message
        private val emojiBox = selfMessageView.binding.emojiBox

        fun bind(message: Message?) {
            message?.let {
                messageView.text = it.content
                fillEmojiBox(it, emojiBox)
            }
        }
    }

    class SendDateViewHolder(private val sendDateView: FrameLayout) : BaseViewHolder(sendDateView) {

        fun bind(sendDate: LocalDate?) {
            var sendDateStr =
                sendDate?.format(DateTimeFormatter.ofPattern("dd MMM"))?.replace(".", "")
            sendDateStr?.let {
                sendDateStr = it.replaceRange(it.length - 3, it.length - 2, it[it.length - 3].uppercaseChar().toString())
            }
            (sendDateView.getChildAt(0) as TextView).text = sendDateStr
        }
    }

    private fun messageOnClickFunc(dialog: EmojiBottomSheetDialog, view: View): Boolean {
        dialog.show(view)
        return true
    }

    private fun fillEmojiBox(message: Message, emojiBox: FlexBoxLayout) {
        val emojis = getEmojisForMessage(message.id)

        val addEmojiView = LayoutInflater.from(emojiBox.context).inflate(
            R.layout.view_image_add_emoji,
            emojiBox,
            false
        ) as ImageView
        addEmojiView.setOnClickListener {
            this@ChatMessagesAdapter.dialog.show(addEmojiView)
        }
        emojiBox.addView(addEmojiView)

        if (emojis.isNotEmpty()) {
            emojis.forEach { emoji ->
                val emojiView = EmojiWithCountView.createEmojiWithCountView(emojiBox, emoji)
                if (emoji.selectedByCurrentUser) emojiView.isSelected = true
                emojiBox.addView(emojiView, emojiBox.childCount - 1)
            }
            addEmojiView.visibility = View.VISIBLE
        }
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    companion object {

        private const val DEFAULT_MARGIN_DP = 15
    }
}