package ru.tinkoff.android.coursework.ui.screens

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.SELF_USER_ID
import ru.tinkoff.android.coursework.data.messagesTestData
import ru.tinkoff.android.coursework.data.messagesTestDataWithDelay
import ru.tinkoff.android.coursework.data.topicsTestData
import ru.tinkoff.android.coursework.databinding.ActivityChatBinding
import ru.tinkoff.android.coursework.model.Message
import ru.tinkoff.android.coursework.model.Topic
import ru.tinkoff.android.coursework.ui.customviews.*
import ru.tinkoff.android.coursework.ui.screens.adapters.ChatMessagesAdapter
import java.time.LocalDateTime

internal class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var dialog: EmojiBottomSheetDialog
    private lateinit var chatRecycler: RecyclerView
    private lateinit var adapter: ChatMessagesAdapter
    private lateinit var compositeDisposable: CompositeDisposable
    private var topic: Topic = topicsTestData[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        compositeDisposable = CompositeDisposable()
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
            onBackPressed()
        }

        binding.topicName.text = resources.getString(
            R.string.topic_name_text,
            intent.getStringExtra(TOPIC_NAME_KEY)?.lowercase()
        )

        binding.channelName.text = resources.getString(
            R.string.channel_name_text,
            intent.getStringExtra(CHANNEL_NAME_KEY)
        )
    }

    private fun configureChatRecycler() {
        chatRecycler = binding.chat
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        chatRecycler.layoutManager = layoutManager
        adapter = ChatMessagesAdapter(dialog)

        messagesTestDataWithDelay()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onSuccess = { adapter.messages = it },
                onError = {
                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.messages_not_found_error_text),
                        Snackbar.LENGTH_LONG
                    ) { configureChatRecycler() }
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
                messagesTestData.add(Message(
                    id = (messagesTestData.size + 1).toLong(),
                    userId = SELF_USER_ID,
                    topicName = topic.name,
                    content = enterMessage.text.toString(),
                    reactions = listOf(),
                    sendDateTime = LocalDateTime.now()
                ))
                adapter.update(messagesTestData, messagesTestData.size - 1)
                chatRecycler.layoutManager?.scrollToPosition(adapter.messages.size - 1)
                enterMessage.text.clear()
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(enterMessage.windowToken, 0)
            }
        }
    }

    private fun createAndConfigureBottomSheet() {
        val bottomSheetLayout =
            layoutInflater.inflate(R.layout.layout_bottom_sheet, null) as LinearLayout
        dialog = EmojiBottomSheetDialog(
            this,
            R.style.BottomSheetDialogTheme,
            bottomSheetLayout
        )
        dialog.setContentView(bottomSheetLayout)
    }

    companion object {

        const val CHANNEL_NAME_KEY = "channelName"
        const val TOPIC_NAME_KEY = "topicName"
    }

}
