package ru.tinkoff.android.coursework.ui.screens

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.SELF_USER_ID
import ru.tinkoff.android.coursework.data.messages
import ru.tinkoff.android.coursework.data.topics
import ru.tinkoff.android.coursework.databinding.ActivityChatBinding
import ru.tinkoff.android.coursework.model.EmojiWithCount
import ru.tinkoff.android.coursework.model.Message
import ru.tinkoff.android.coursework.model.Topic
import ru.tinkoff.android.coursework.ui.BottomSheetCallback
import ru.tinkoff.android.coursework.ui.customviews.*
import ru.tinkoff.android.coursework.ui.customviews.EmojiWithCountView
import ru.tinkoff.android.coursework.ui.customviews.FlexBoxLayout
import ru.tinkoff.android.coursework.ui.customviews.MessageViewGroup
import ru.tinkoff.android.coursework.ui.customviews.SelfMessageViewGroup
import ru.tinkoff.android.coursework.ui.screens.adapters.ChatMessagesAdapter
import java.time.LocalDateTime

internal class ChatActivity : AppCompatActivity(), BottomSheetCallback {

    private lateinit var binding: ActivityChatBinding
    private lateinit var dialog: EmojiBottomSheetDialog
    private lateinit var chatRecycler: RecyclerView
    private lateinit var adapter: ChatMessagesAdapter
    private lateinit var bottomSheetCallback: BottomSheetCallback
    private val compositeDisposable = CompositeDisposable()
    private var topic: Topic = topics[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createAndConfigureBottomSheet()
        configureEnterMessageSection()
        configureChatRecycler()
        configureToolbar()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun configureToolbar() {
        binding.backIcon.setOnClickListener {
            val navController = findNavController(R.id.nav_chat)
            navController.popBackStack()
        }

        binding.topicName.text = resources.getString(
            R.string.topic_name_text,
            intent.getStringExtra("topicName")?.lowercase()
        )

        binding.channelName.text = resources.getString(
            R.string.channel_name_text,
            intent.getStringExtra("channelName")
        )
    }

    private fun configureChatRecycler() {
        chatRecycler = binding.chat
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        chatRecycler.layoutManager = layoutManager
        adapter = ChatMessagesAdapter(dialog)

        Single.just(messages)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onSuccess = { adapter.messages = it },
                onError = {
                    Toast.makeText(this, "Messages not found", Toast.LENGTH_LONG).show()
                }
            )
            .addTo(compositeDisposable)

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
                    topicName = topic.name,
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
        if (chosenEmojiCode.isNotEmpty()) {
            val emojiBox = when (selectedView) {
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
}
