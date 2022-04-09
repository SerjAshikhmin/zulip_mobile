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
import ru.tinkoff.android.coursework.api.NetworkService
import ru.tinkoff.android.coursework.databinding.ActivityChatBinding
import ru.tinkoff.android.coursework.model.Message
import ru.tinkoff.android.coursework.model.request.NarrowRequest
import ru.tinkoff.android.coursework.model.response.SendMessageResponse
import ru.tinkoff.android.coursework.testdata.EmojiCodes
import ru.tinkoff.android.coursework.ui.customviews.*
import ru.tinkoff.android.coursework.ui.screens.adapters.ChatMessagesAdapter
import ru.tinkoff.android.coursework.ui.screens.adapters.OnBottomSheetChooseEmojiListener
import ru.tinkoff.android.coursework.ui.screens.adapters.OnEmojiClickListener
import ru.tinkoff.android.coursework.ui.screens.utils.getDateTimeFromTimestamp

internal class ChatActivity : AppCompatActivity(), OnEmojiClickListener,
    OnBottomSheetChooseEmojiListener {

    private lateinit var binding: ActivityChatBinding
    private lateinit var dialog: EmojiBottomSheetDialog
    private lateinit var chatRecycler: RecyclerView
    private lateinit var adapter: ChatMessagesAdapter
    private lateinit var compositeDisposable: CompositeDisposable

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

    override fun onEmojiClick(
        isSelected: Boolean,
        emojiCode: String,
        messageId: Long
    ) {
        val emojiName = EmojiCodes.emojiMap[emojiCode]
        if (emojiName != null) {
            if (isSelected) {
                addReaction(messageId, emojiName)
            } else {
                removeReaction(messageId, emojiName)
            }
        }
    }

    override fun onBottomSheetChooseEmoji(isSelected: Boolean, emojiCode: String, messageId: Long) {
        val emojiName = EmojiCodes.emojiMap[emojiCode]
        if (emojiName != null) {
            addReaction(messageId, emojiName)
        }
    }

    private fun addReaction(messageId: Long, emojiName: String) {
        NetworkService.getZulipJsonApi().addReaction(
            messageId = messageId,
            emojiName = emojiName
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                showSnackBarWithRetryAction(
                    binding.root,
                    "Adding emoji error",
                    Snackbar.LENGTH_LONG
                ) { addReaction(messageId, emojiName) }
            }
            .subscribe()
            .addTo(compositeDisposable)
    }

    private fun removeReaction(messageId: Long, emojiName: String) {
        NetworkService.getZulipJsonApi().removeReaction(
            messageId = messageId,
            emojiName = emojiName
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                showSnackBarWithRetryAction(
                    binding.root,
                    "Removing emoji error",
                    Snackbar.LENGTH_LONG
                ) { removeReaction(messageId, emojiName) }
            }
            .subscribe()
            .addTo(compositeDisposable)
    }

    private fun configureToolbar() {
        binding.backIcon.setOnClickListener {
            onBackPressed()
        }

        binding.topicName.text = adapter.topicName
        binding.channelName.text = adapter.channelName
    }

    private fun configureChatRecycler() {
        chatRecycler = binding.chat
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        chatRecycler.layoutManager = layoutManager
        adapter = ChatMessagesAdapter(dialog, chatRecycler, this)

        adapter.topicName = resources.getString(
            R.string.topic_name_text,
            intent.getStringExtra(TOPIC_NAME_KEY)?.lowercase()
        )

        adapter.channelName = resources.getString(
            R.string.channel_name_text,
            intent.getStringExtra(CHANNEL_NAME_KEY)
        )

        getMessagesForChat()

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
                sendMessage(
                    content = enterMessage.text.toString(),
                    stream = intent.getStringExtra(CHANNEL_NAME_KEY) ?: "",
                    topic = intent.getStringExtra(TOPIC_NAME_KEY) ?: ""
                )
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
            bottomSheetLayout,
            this,
            this
        )
        dialog.setContentView(bottomSheetLayout)
    }

    private fun getMessagesForChat() {
        NetworkService.getZulipJsonApi().getMessages(
            narrow = arrayOf(
                NarrowRequest(
                    operator = TOPIC_NARROW_OPERATOR_KEY,
                    operand = intent.getStringExtra(TOPIC_NAME_KEY) ?: ""
                )
            ).contentToString()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    val messages = insertDateSeparators(it.messages)
                    val isLastChanged = !adapter.messages.isNullOrEmpty() && adapter.messages.last() != messages.last()
                    adapter.messages = messages
                    if (isLastChanged) adapter.notifyItemChanged(messages.size - 1)
                },
                onError = {
                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.messages_not_found_error_text),
                        Snackbar.LENGTH_LONG
                    ) { configureChatRecycler() }
                }
            )
            .addTo(compositeDisposable)
    }

    private fun insertDateSeparators(messages: List<Message>): List<Any> {
        val messagesWithDateSeparators = mutableListOf<Any>()
        for (i in messages.indices) {
            val curDate = getDateTimeFromTimestamp(messages[i].timestamp).toLocalDate()
            if (i == 0) {
                messagesWithDateSeparators.add(curDate)
            } else {
                val prevDate = getDateTimeFromTimestamp(messages[i - 1].timestamp).toLocalDate()
                if (prevDate != curDate) {
                    messagesWithDateSeparators.add(curDate)
                }
            }
            messagesWithDateSeparators.add(messages[i])
        }
        return messagesWithDateSeparators
    }

    private fun sendMessage(
        content: String,
        stream: String,
        topic: String,
    ): SendMessageResponse {
        return NetworkService.getZulipJsonApi().sendMessage(
            to = stream,
            content = content,
            topic = topic
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                getMessagesForChat()
            }
            .doOnError {
                showSnackBarWithRetryAction(
                    binding.root,
                    "Sending message error",
                    Snackbar.LENGTH_LONG
                ) { sendMessage(content, stream, topic) }
            }
            .blockingGet()
    }

    companion object {

        const val CHANNEL_NAME_KEY = "channelName"
        const val TOPIC_NAME_KEY = "topicName"
        const val TOPIC_NARROW_OPERATOR_KEY = "topic"
    }

}
