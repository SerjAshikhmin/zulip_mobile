package ru.tinkoff.android.coursework.ui.screens

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
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
import ru.tinkoff.android.coursework.api.model.request.NarrowRequest
import ru.tinkoff.android.coursework.api.model.response.SendMessageResponse
import ru.tinkoff.android.coursework.data.EmojiCodes
import ru.tinkoff.android.coursework.api.model.EmojiWithCount
import ru.tinkoff.android.coursework.ui.customviews.*
import ru.tinkoff.android.coursework.ui.screens.adapters.ChatMessagesAdapter
import ru.tinkoff.android.coursework.ui.screens.adapters.OnBottomSheetChooseEmojiListener
import ru.tinkoff.android.coursework.ui.screens.adapters.OnEmojiClickListener
import ru.tinkoff.android.coursework.ui.screens.utils.showSnackBarWithRetryAction

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

    override fun onEmojiClick(emojiView: EmojiWithCountView) {
        val emojiName = EmojiCodes.emojiMap[emojiView.emojiCode]
        if (emojiName != null) {
            if (!emojiView.isSelected) {
                addReaction(emojiView, emojiName, false, null)
            } else {
                removeReaction(emojiView, emojiName)
            }
        }
    }

    private fun addReaction(
        emojiView: EmojiWithCountView,
        emojiName: String,
        isNewEmojiView: Boolean,
        emojiBox: FlexBoxLayout?
    ) {
        NetworkService.getZulipJsonApi().addReaction(
            messageId = emojiView.messageId,
            emojiName = emojiName
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    emojiView.isSelected = true
                    emojiView.emojiCount++
                    if (isNewEmojiView) {
                        addNewEmojiToEmojiBox(emojiBox, emojiView)
                    }
                },
                onError = {
                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.adding_emoji_error_text),
                        Snackbar.LENGTH_LONG
                    ) { addReaction(emojiView, emojiName, isNewEmojiView, emojiBox) }
                }
            )
            .addTo(compositeDisposable)
    }

    private fun addNewEmojiToEmojiBox(
        emojiBox: FlexBoxLayout?,
        emojiView: EmojiWithCountView
    ) {
        if (emojiBox != null) {
            emojiBox.addView(emojiView, emojiBox.childCount - 1)
            if (emojiBox.childCount > 1) {
                emojiBox.getChildAt(emojiBox.childCount - 1).visibility = View.VISIBLE
            }
        }
    }

    private fun removeReaction(emojiView: EmojiWithCountView, emojiName: String) {
        NetworkService.getZulipJsonApi().removeReaction(
            messageId = emojiView.messageId,
            emojiName = emojiName
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    emojiView.isSelected = false
                    emojiView.emojiCount--
                    if (emojiView.emojiCount == 0) {
                        val emojiBox = (emojiView.parent as FlexBoxLayout)
                        emojiBox.removeView(emojiView)
                        if (emojiBox.childCount == 1) {
                            emojiBox.getChildAt(0).visibility = View.GONE
                        }
                    }
                },
                onError = {
                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.removing_emoji_error_text),
                        Snackbar.LENGTH_LONG
                    ) { removeReaction(emojiView, emojiName) }
                }
            )
            .addTo(compositeDisposable)
    }

    override fun onBottomSheetChooseEmoji(selectedView: View?, chosenEmojiCode: String) {
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
            addNewEmojiToMessage(selectedView, emojiBox, chosenEmojiCode)
        }
    }

    private fun addNewEmojiToMessage(
        selectedView: View?,
        emojiBox: FlexBoxLayout?,
        chosenEmojiCode: String
    ) {
        val messageId = when (selectedView) {
            is MessageViewGroup -> selectedView.messageId
            is SelfMessageViewGroup -> selectedView.messageId
            is ImageView -> {
                when (val parentViewGroup = selectedView.parent.parent) {
                    is MessageViewGroup -> parentViewGroup.messageId
                    is SelfMessageViewGroup -> parentViewGroup.messageId
                    else -> 0L
                }
            }
            else -> 0L
        }
        if (emojiBox != null) {
            val emojiView = EmojiWithCountView.createEmojiWithCountView(
                emojiBox = emojiBox,
                emoji = EmojiWithCount(chosenEmojiCode, 0),
                messageId = messageId,
                emojiClickListener = this
            )
            val emojiName = EmojiCodes.emojiMap[emojiView.emojiCode]
            if (emojiName != null) {
                addReaction(emojiView, emojiName, true, emojiBox)
            }
        }
    }

    private fun configureToolbar() {
        with(binding) {
            backIcon.setOnClickListener {
                onBackPressed()
            }
            topicName.text = adapter.topicName
            channelName.text = adapter.channelName
        }
    }

    private fun configureChatRecycler() {
        chatRecycler = binding.chatRecycler
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
            context = this,
            theme = R.style.BottomSheetDialogTheme,
            bottomSheet = bottomSheetLayout,
            bottomSheetChooseEmojiListener = this
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
                    val oldMessages = adapter.messagesWithDateSeparators
                    adapter.messages = it.messages
                    val isLastChanged = !oldMessages.isNullOrEmpty()
                            && adapter.messagesWithDateSeparators.last() != oldMessages.last()
                    if (isLastChanged) adapter.notifyItemChanged(
                        adapter.messagesWithDateSeparators.size - 1
                    )
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
                binding.root.showSnackBarWithRetryAction(
                    resources.getString(R.string.sending_message_error_text),
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
