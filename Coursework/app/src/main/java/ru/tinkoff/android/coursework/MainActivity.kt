package ru.tinkoff.android.coursework

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tinkoff.android.coursework.data.SELF_USER_ID
import ru.tinkoff.android.coursework.data.messages
import ru.tinkoff.android.coursework.model.Message
import ru.tinkoff.android.coursework.ui.BottomSheetCallback
import ru.tinkoff.android.coursework.ui.ChatMessagesAdapter
import ru.tinkoff.android.coursework.databinding.ActivityMainBinding
import ru.tinkoff.android.coursework.model.EmojiWithCount
import ru.tinkoff.android.coursework.ui.customviews.*
import ru.tinkoff.android.coursework.ui.customviews.EmojiWithCountView
import java.time.LocalDateTime

class MainActivity : AppCompatActivity(), BottomSheetCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dialog: EmojiBottomSheetDialog
    private lateinit var chatRecycler: RecyclerView
    private lateinit var adapter: ChatMessagesAdapter
    private lateinit var bottomSheetCallback: BottomSheetCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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
                messages.add(Message(
                    id = (messages.size + 1).toLong(),
                    userId = SELF_USER_ID,
                    content = enterMessage.text.toString(),
                    reactions = listOf(),
                    sendDateTime = LocalDateTime.now()
                ))
                adapter.update(messages, messages.size - 1)
                chatRecycler.layoutManager?.scrollToPosition(adapter.messages.size - 1)
                enterMessage.text.clear()
                val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(enterMessage.windowToken, 0)
            }
        }
    }

    private fun createAndConfigureBottomSheet() {
        val bottomSheetLayout = layoutInflater.inflate(R.layout.layout_bottom_sheet, null) as LinearLayout
        bottomSheetCallback = this
        dialog = EmojiBottomSheetDialog(
            this,
            R.style.BottomSheetDialogTheme,
            bottomSheetLayout,
            bottomSheetCallback
        )
        dialog.setContentView(bottomSheetLayout)
    }

    override fun callbackMethod(selectedView: View?, chosenEmojiCode: String) {
        val emojiBox = when(selectedView) {
            is MessageViewGroup -> selectedView.binding.emojiBox
            is SelfMessageViewGroup -> selectedView.binding.emojiBox
            is ImageView -> selectedView.parent as FlexBoxLayout
            else -> null
        }
        val emoji = emojiBox?.children?.firstOrNull {
            it is EmojiWithCountView && it.emojiCode == chosenEmojiCode
        }
        if (emoji is EmojiWithCountView) {
            if (!emoji.isSelected) {
                emoji.isSelected = true
                emoji.emojiCount++
            }
        } else {
            if (emojiBox != null) {
                val emojiView = EmojiWithCountView.createEmojiWithCountView(
                    emojiBox,
                    EmojiWithCount(chosenEmojiCode, 1)
                )
                emojiView.isSelected = true
                emojiBox.addView(emojiView, emojiBox.childCount - 1)
                if (emojiBox.childCount > 1) {
                    emojiBox.getChildAt(emojiBox.childCount - 1).visibility = View.VISIBLE
                }
            }
        }
    }
}
