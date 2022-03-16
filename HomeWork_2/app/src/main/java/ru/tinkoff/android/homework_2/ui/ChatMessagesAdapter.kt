package ru.tinkoff.android.homework_2.ui

import android.content.Context
import android.util.DisplayMetrics
import android.util.LayoutDirection
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.tinkoff.android.homework_2.R
import ru.tinkoff.android.homework_2.data.Messages
import ru.tinkoff.android.homework_2.model.Message
import ru.tinkoff.android.homework_2.ui.customviews.MessageViewGroup
import ru.tinkoff.android.homework_2.ui.customviews.SelfMessageViewGroup
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private const val TYPE_MESSAGE = 0
private const val TYPE_SELF_MESSAGE = 1
private const val TYPE_SEND_DATE = 2

class ChatMessagesAdapter(private val messages: List<Message?>, val dialog: BottomSheetDialog)
    : RecyclerView.Adapter<ChatMessagesAdapter.BaseViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        /*if (position == 0 || (position != messages.size - 1 &&
            messages[position].sendDateTime.toLocalDate() !=
            messages[position + 1].sendDateTime.toLocalDate())*/
        if (messages[position] == null) {
            return TYPE_SEND_DATE
        }
        return if (messages[position]?.userName == Messages.SELF_USER_NAME) {
            TYPE_SELF_MESSAGE
        } else {
            TYPE_MESSAGE
        }
    }

    sealed class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class MessageViewHolder(messageView: MessageViewGroup) : BaseViewHolder(messageView) {
        private val avatar = messageView.binding.avatarImage
        private val username = messageView.binding.username
        private val messageView = messageView.binding.messageText
        private val emojiBox = messageView.binding.emojiBox

        fun bind(message: Message?) {
            message?.let {
                username.text = it.userName
                messageView.text = it.content
            }
        }
    }

    class SelfMessageViewHolder(selfMessageView: SelfMessageViewGroup) : BaseViewHolder(selfMessageView) {
        private val messageView = selfMessageView.binding.message
        private val emojiBox = selfMessageView.binding.emojiBox

        fun bind(message: Message?) {
            message?.let {
                messageView.text = it.content
            }
        }
    }

    class SendDateViewHolder(sendDateView: FrameLayout) : BaseViewHolder(sendDateView) {
        private val sendDateView = sendDateView

        fun bind(sendDate: LocalDate?) {
            val sendDateStr = sendDate?.format(DateTimeFormatter.ofPattern("dd MMM"))?.replace(".", "")
                //sendDateStr[sendDateStr.length - 2] = sendDateStr[sendDateStr.length - 2].uppercaseChar()
            (sendDateView.getChildAt(0) as TextView).text = sendDateStr
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
                    return@setOnLongClickListener messageOnClickFunc(dialog)
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
                    return@setOnLongClickListener messageOnClickFunc(dialog)
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
            is MessageViewHolder -> holder.bind(messages[position])
            is SelfMessageViewHolder -> holder.bind(messages[position])
            is SendDateViewHolder -> holder.bind(messages[position + 1]?.sendDateTime?.toLocalDate())
        }
    }

    override fun getItemCount(): Int = messages.size

    private fun dpToPx(dp: Int, context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    private fun messageOnClickFunc(dialog: BottomSheetDialog): Boolean {
        dialog.show()
        return true
    }

    companion object {

        private const val DEFAULT_MARGIN_DP = 15
    }
}
