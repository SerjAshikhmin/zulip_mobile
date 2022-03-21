package ru.tinkoff.android.homework_2.ui.screens

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import ru.tinkoff.android.homework_2.R
import ru.tinkoff.android.homework_2.data.EmojiCodes
import ru.tinkoff.android.homework_2.data.SELF_USER_ID
import ru.tinkoff.android.homework_2.data.messages
import ru.tinkoff.android.homework_2.databinding.ActivityChatBinding
import ru.tinkoff.android.homework_2.model.Message
import ru.tinkoff.android.homework_2.ui.BottomSheetCallback
import ru.tinkoff.android.homework_2.ui.customviews.EmojiBottomSheetDialog
import ru.tinkoff.android.homework_2.ui.screens.adapters.ChatMessagesAdapter
import java.time.LocalDateTime

class ChatActivity : AppCompatActivity(), BottomSheetCallback {

    private lateinit var binding: ActivityChatBinding
    private lateinit var dialog: EmojiBottomSheetDialog
    private lateinit var chatRecycler: RecyclerView
    private lateinit var adapter: ChatMessagesAdapter
    private lateinit var bottomSheetCallback: BottomSheetCallback
    private var chosenEmojiCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createAndConfigureBottomSheet()
        configureEnterMessageSection()
        configureChatRecycler()
    }

    private fun configureChatRecycler() {
        chatRecycler = binding.chat
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        chatRecycler.layoutManager = layoutManager
        adapter = ChatMessagesAdapter(dialog)
        adapter.messages = messages
        chatRecycler.adapter = adapter
    }

    private fun configureEnterMessageSection() {
        val enterMessage = binding.enterMessage
        val sendButton = binding.sendButton

        enterMessage.doAfterTextChanged {
            // TODO сделать кнопку с "+" с прозрачным бэкграундом и менять цвет при вводе
            if (enterMessage.text.isEmpty()) {
                sendButton.setImageResource(R.drawable.baseline_add_20)
                //sendButton.backgroundTintList =
                //    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkGreyVariant))
            }
            if (enterMessage.text.length == 1) {
                sendButton.setImageResource(R.drawable.baseline_send_20)
                //sendButton.backgroundTintList =
                //    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.teal_500))
            }
        }

        sendButton.setOnClickListener {
            if (enterMessage.text.isNotEmpty()) {
                messages.add(
                    Message(
                    (messages.size + 1).toLong(),
                    SELF_USER_ID,
                    enterMessage.text.toString(),
                    listOf(),
                    LocalDateTime.now()
                )
                )
                adapter.update(messages, messages.size - 1)
                chatRecycler.layoutManager?.scrollToPosition(adapter.messages.size - 1)
                enterMessage.text.clear()
                val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(enterMessage.windowToken, 0)
            }
        }
    }

    private fun createAndConfigureBottomSheet() {
        val bottomSheet = layoutInflater.inflate(R.layout.layout_bottom_sheet, null) as LinearLayout

        EmojiCodes.values.forEach { emojiCode ->
            val emojiView = LayoutInflater.from(this).inflate(
                R.layout.layout_bottom_sheet_emoji, null
            ) as TextView
            emojiView.text = emojiCode
            emojiView.setOnClickListener {
                chosenEmojiCode = emojiView.text.toString()
                dialog.dismiss()
            }
            (bottomSheet.getChildAt(1) as FlexboxLayout).addView(emojiView)
        }
        bottomSheetCallback = this
        dialog = EmojiBottomSheetDialog(this, R.style.BottomSheetDialogTheme, bottomSheetCallback)
        dialog.setContentView(bottomSheet)
    }

    override fun callbackMethod() = chosenEmojiCode
}
