package ru.tinkoff.android.coursework.presentation.screens

import android.Manifest
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.tinkoff.android.coursework.App
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.EmojiCodes
import ru.tinkoff.android.coursework.databinding.ActivityChatBinding
import ru.tinkoff.android.coursework.di.ActivityScope
import ru.tinkoff.android.coursework.di.chat.DaggerChatComponent
import ru.tinkoff.android.coursework.domain.model.EmojiWithCount
import ru.tinkoff.android.coursework.presentation.customviews.*
import ru.tinkoff.android.coursework.presentation.elm.chat.ChatElmStoreFactory
import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatEffect
import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatEvent
import ru.tinkoff.android.coursework.presentation.elm.chat.models.ChatState
import ru.tinkoff.android.coursework.presentation.screens.StreamsListFragment.Companion.ALL_TOPICS_IN_STREAM
import ru.tinkoff.android.coursework.presentation.screens.adapters.ChatMessagesAdapter
import ru.tinkoff.android.coursework.presentation.screens.adapters.OnBottomSheetChooseEmojiListener
import ru.tinkoff.android.coursework.presentation.screens.adapters.OnEmojiClickListener
import ru.tinkoff.android.coursework.presentation.screens.adapters.OnTopicItemClickListener
import ru.tinkoff.android.coursework.utils.copy
import ru.tinkoff.android.coursework.utils.getFileNameFromContentUri
import ru.tinkoff.android.coursework.utils.hasPermissions
import ru.tinkoff.android.coursework.utils.showSnackBarWithRetryAction
import vivid.money.elmslie.android.base.ElmActivity
import vivid.money.elmslie.core.store.Store
import java.io.*
import javax.inject.Inject


@ActivityScope
internal class ChatActivity : ElmActivity<ChatEvent, ChatEffect, ChatState>(),
    OnEmojiClickListener, OnBottomSheetChooseEmojiListener, OnTopicItemClickListener {

    @Inject
    internal lateinit var chatElmStoreFactory: ChatElmStoreFactory

    override var initEvent: ChatEvent = ChatEvent.Ui.InitEvent
    private lateinit var binding: ActivityChatBinding
    private lateinit var dialog: EmojiBottomSheetDialog
    private lateinit var chatRecycler: RecyclerView
    private lateinit var adapter: ChatMessagesAdapter
    private lateinit var streamName: String
    private lateinit var topicName: String
    private var selectFileResultLauncher = initializeSelectFileResultLauncher()
    private var isFirstClickToEnterMessage = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createAndConfigureBottomSheet()
        configureChatRecycler()
        configureEnterMessageSection()
        configureToolbar()
    }

    override fun createStore(): Store<ChatEvent, ChatEffect, ChatState> {
        val chatComponent = DaggerChatComponent.factory().create(
            (this.application as App).applicationComponent
        )
        chatComponent.inject(this)
        return chatElmStoreFactory.provide()
    }

    override fun render(state: ChatState) {
        binding.progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        with (adapter) {
            messages = state.items
            anchor = state.anchor
        }
    }

    override fun handleEffect(effect: ChatEffect) {
        when(effect) {
            is ChatEffect.MessageSentEffect -> {
                binding.enterMessage.text.clear()
            }
            is ChatEffect.FileUploadedEffect -> {
                binding.enterMessage.text.append("[${effect.fileName}](${effect.fileUri})\n\n")
                binding.sendButton.showActionIcon(R.drawable.ic_send, R.color.teal_500)
            }
            is ChatEffect.NavigateToChat -> {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra(STREAM_NAME_KEY, streamName)
                intent.putExtra(TOPIC_NAME_KEY, effect.topicName)
                startActivity(intent)
            }
            is ChatEffect.MessagesLoadingError -> {
                binding.root.showSnackBarWithRetryAction(
                    resources.getString(R.string.messages_not_found_error_text),
                    Snackbar.LENGTH_LONG
                ) { configureChatRecycler() }
            }
            is ChatEffect.MessageSendingError -> {
                binding.root.showSnackBarWithRetryAction(
                    resources.getString(R.string.sending_message_error_text),
                    Snackbar.LENGTH_LONG
                ) { }
            }
            is ChatEffect.FileUploadingError -> {
                binding.root.showSnackBarWithRetryAction(
                    resources.getString(R.string.uploading_file_error_text),
                    Snackbar.LENGTH_LONG
                ) { store.accept(ChatEvent.Ui.UploadFile(effect.fileName, effect.fileBody)) }
            }
        }
    }

    override fun onEmojiClick(emojiView: EmojiWithCountView) {
        val emojiName = EmojiCodes.emojiMap[emojiView.emojiCode]
        if (emojiName != null) {
            if (!emojiView.isSelected) {
                store.accept(
                    ChatEvent.Ui.AddReaction(
                        emojiView.messageId,
                        emojiName,
                        emojiView.emojiCode
                    )
                )
                emojiView.isSelected = true
                emojiView.emojiCount = emojiView.emojiCount.plus(1)
            } else {
                store.accept(
                    ChatEvent.Ui.RemoveReaction(
                        emojiView.messageId,
                        emojiName,
                        emojiView.emojiCode
                    )
                )
                emojiView.isSelected = false
                emojiView.emojiCount = emojiView.emojiCount.minus(1)
                if (emojiView.emojiCount == 0) {
                    val emojiBox = (emojiView.parent as FlexBoxLayout)
                    emojiBox.removeView(emojiView)
                    if (emojiBox.childCount == 1) {
                        emojiBox.getChildAt(0).visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onBottomSheetChooseEmoji(
        selectedView: View?,
        chosenEmojiCode: String
    ) {
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

    override fun onTopicItemClick(topicName: String, streamName: String) {
        store.accept(ChatEvent.Ui.LoadChat(topicName))
    }

    private fun configureToolbar() {
        with(binding) {
            backIcon.setOnClickListener {
                onBackPressed()
            }
            streamName.text = adapter.streamNameText
        }
    }

    private fun configureChatRecycler() {
        chatRecycler = binding.chatRecycler
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        chatRecycler.layoutManager = layoutManager
        adapter = ChatMessagesAdapter(dialog, chatRecycler, this, this)

        topicName = intent.getStringExtra(TOPIC_NAME_KEY)?.lowercase() ?: ""
        adapter.topicNameValue = topicName
        adapter.topicNameText = resources.getString(
            R.string.topic_name_text,
            topicName
        )

        streamName = intent.getStringExtra(STREAM_NAME_KEY)?.lowercase() ?: ""
        adapter.streamNameValue = streamName
        adapter.streamNameText = resources.getString(
            R.string.stream_name_text,
            intent.getStringExtra(STREAM_NAME_KEY)
        )

        store.accept(
            ChatEvent.Ui.LoadLastMessages(
                streamName = streamName,
                topicName = topicName,
                anchor = adapter.anchor
            )
        )

        chatRecycler.adapter = adapter

        chatRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            private var isNewPortionLoading = false

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                isNewPortionLoading = false
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItemPosition == SCROLL_POSITION_FOR_NEXT_PORTION_LOADING
                    && !isNewPortionLoading) {
                        isNewPortionLoading = true
                        store.accept(ChatEvent.Ui.LoadPortionOfMessages(anchor = adapter.anchor))
                }
            }
        })
    }

    private fun configureEnterMessageSection() {
        val enterMessage = binding.enterMessage
        val sendButton = binding.sendButton
        val topicEditText = binding.topicEditText
        if (topicName == ALL_TOPICS_IN_STREAM) {
            configureTopicEditTextView(topicEditText)
        }

        enterMessage.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && topicName == ALL_TOPICS_IN_STREAM && isFirstClickToEnterMessage) {
                topicEditText.visibility = View.VISIBLE
                if (topicEditText.text.isEmpty()) {
                    topicEditText.requestFocus()
                    isFirstClickToEnterMessage = false
                }
            }
        }

        enterMessage.doAfterTextChanged {
            if (enterMessage.text.isEmpty()) {
                sendButton.showActionIcon(R.drawable.ic_add, R.color.grey_500)
            }
            if (enterMessage.text.length == 1) {
                sendButton.showActionIcon(R.drawable.ic_send, R.color.teal_500)
            }
        }

        sendButton.setOnClickListener {
            if (enterMessage.text.isNotEmpty()) {
                val topicName = getTopicNameForSendingMessage(topicEditText)
                store.accept(
                    ChatEvent.Ui.SendMessage(
                        topicName = topicName,
                        streamName = streamName,
                        content = enterMessage.text.toString()
                    )
                )
                hideKeyboard(enterMessage)
            } else {
                if (!hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1
                    )
                } else {
                    selectFileResultLauncher.launch("*/*")
                }
            }
        }
    }

    private fun configureTopicEditTextView(topicEditText: AutoCompleteTextView) {
        topicEditText.setOnClickListener {
            if (topicEditText.text.isEmpty()) {
                updateTopicsAdapter(topicEditText)
                topicEditText.showDropDown()
            }
        }

        topicEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && (view as EditText).text.isEmpty()) {
                updateTopicsAdapter(topicEditText)
                topicEditText.showDropDown()
            }
        }
    }

    private fun updateTopicsAdapter(topicEditText: AutoCompleteTextView) {
        topicEditText.setAdapter(
            ArrayAdapter(
                this,
                R.layout.view_topic_name_in_drop_down,
                adapter.topics.toMutableList().map { it.name }.sorted()
            )
        )
    }

    private fun getTopicNameForSendingMessage(topicEditText: AutoCompleteTextView) =
        if (topicName != ALL_TOPICS_IN_STREAM) {
            topicName
        } else {
            if (topicEditText.text.isNotEmpty()) {
                topicEditText.text.toString()
            } else {
                NO_TOPIC_STRING_VALUE
            }
        }

    private fun FloatingActionButton.showActionIcon(drawableId: Int, colorId: Int) {
        setImageResource(drawableId)
        imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this.context, colorId))
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
                store.accept(
                    ChatEvent.Ui.AddReaction(
                        emojiView.messageId,
                        emojiName,
                        emojiView.emojiCode
                    )
                )
            }
        }
    }

    private fun initializeSelectFileResultLauncher() =
        registerForActivityResult(ActivityResultContracts.GetContent()) { contentUri ->
            val fileName = getFileNameFromContentUri(this, contentUri)
            val file = File(cacheDir, fileName)
            file.createNewFile()

            try {
                val oStream = FileOutputStream(file)
                val inputStream = contentResolver.openInputStream(contentUri)
                inputStream?.let { copy(inputStream, oStream) }
                oStream.flush()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val requestFile: RequestBody = file
                .asRequestBody(contentResolver.getType(contentUri)?.toMediaTypeOrNull())
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData("file", file.name, requestFile)
            store.accept(ChatEvent.Ui.UploadFile(fileName, body))
        }

    private fun hideKeyboard(view: View) {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {

        internal const val STREAM_NAME_KEY = "streamName"
        internal const val TOPIC_NAME_KEY = "topicName"
        internal const val NO_TOPIC_STRING_VALUE = "(no topic)"
        private const val SCROLL_POSITION_FOR_NEXT_PORTION_LOADING = 5
    }

}
