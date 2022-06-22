package ru.tinkoff.android.coursework.presentation.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
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
import ru.tinkoff.android.coursework.presentation.screens.listeners.*
import ru.tinkoff.android.coursework.utils.*
import vivid.money.elmslie.android.base.ElmActivity
import vivid.money.elmslie.core.store.Store
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@ActivityScope
internal class ChatActivity : ElmActivity<ChatEvent, ChatEffect, ChatState>(), OnEmojiClickListener,
    OnBottomSheetChooseEmojiListener, OnBottomSheetAddReactionListener,
    OnBottomSheetDeleteMessageListener, OnTopicItemClickListener,
    OnBottomSheetCopyToClipboardListener, OnBottomSheetEditMessageListener {

    @Inject
    internal lateinit var chatElmStoreFactory: ChatElmStoreFactory

    override var initEvent: ChatEvent = ChatEvent.Ui.InitEvent
    private lateinit var binding: ActivityChatBinding
    private lateinit var actionsDialog: ChatActionsBottomSheetDialog
    private lateinit var emojisDialog: EmojiBottomSheetDialog
    private lateinit var chatRecycler: RecyclerView
    private lateinit var adapter: ChatMessagesAdapter
    private lateinit var streamName: String
    private lateinit var topicName: String
    private var selectFileResultLauncher = initializeSelectFileResultLauncher()
    private var isFirstClickToEnterMessage = true
    private var editedMessageId = NO_EDITED_MESSAGE_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createAndConfigureActionsBottomSheet()
        createAndConfigureEmojisBottomSheet()
        configureChatRecycler()
        configureEnterMessageSection()
        configureToolbar()
    }

    override fun createStore(): Store<ChatEvent, ChatEffect, ChatState> {
        val chatComponent = DaggerChatComponent.factory().create(
            (this.application as App).applicationComponent,
            (this.application as App).networkComponent
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
                processFileUploadedEffect(effect)
            }
            is ChatEffect.StartEditMessageEffect -> {
                processStartEditMessageEffect(effect)
            }
            is ChatEffect.MessageEditedEffect -> {
                processMessageEditedEffect()
            }
            is ChatEffect.NavigateToChat -> {
                processNavigateToChatEffect(effect)
            }
            is ChatEffect.MessagesLoadingError -> {
                processMessageLoadingError(effect)
            }
            is ChatEffect.MessageSendingError -> {
                processMessageSendingError(effect)
            }
            is ChatEffect.MessageDeletingError -> {
                processMessageDeletingError(effect)
            }
            is ChatEffect.MessageEditingError -> {
                processMessageEditingError(effect)
            }
            is ChatEffect.FileUploadingError -> {
                processFileUploadingError(effect)
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
                    val emojiBox = (emojiView.parent as? FlexBoxLayout)
                    emojiBox?.removeView(emojiView)
                    if (emojiBox?.childCount == 1) {
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
            is ImageView -> selectedView.parent as? FlexBoxLayout
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

    override fun onBottomSheetAddReaction(selectedView: View?) {
        if (selectedView != null) {
            actionsDialog.dismiss()
            emojisDialog.show(selectedView)
        }
    }

    override fun onBottomSheetDeleteMessage(selectedView: View?) {
        if (selectedView is SelfMessageViewGroup) {
            store.accept(ChatEvent.Ui.DeleteMessage(selectedView.messageId))
        }
        actionsDialog.dismiss()
    }

    override fun onBottomSheetEditMessage(selectedView: View?) {
        var messageId: Long? = null
        if (selectedView is SelfMessageViewGroup) {
            messageId = selectedView.messageId
        }
        if (messageId != null) {
            val messageToEdit = adapter.messages.find { it.id == messageId }
            if (messageToEdit != null) {
                store.accept(ChatEvent.Ui.StartEditMessage(messageToEdit))
            }
        }
        actionsDialog.dismiss()
    }

    override fun onBottomSheetCopyToClipboard(selectedView: View?) {
        if (selectedView is MessageViewGroup) {
            copyTextToClipboard(selectedView.binding.messageText.text)
        } else {
            if (selectedView is SelfMessageViewGroup) {
                copyTextToClipboard(selectedView.binding.message.text)
            }
        }
        actionsDialog.dismiss()
    }

    override fun onTopicItemClick(topicName: String?, streamName: String?) {
        if (topicName != null) {
            store.accept(ChatEvent.Ui.LoadChat(topicName))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (permissionGranted(grantResults)) {
                selectFileResultLauncher.launch("*/*")
            }
        }
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
        adapter = ChatMessagesAdapter(actionsDialog, emojisDialog, chatRecycler, this, this)

        topicName = intent.getStringExtra(TOPIC_NAME_KEY) ?: ""
        adapter.topicNameValue = topicName
        adapter.topicNameText = resources.getString(
            R.string.topic_name_text,
            topicName
        )

        streamName = intent.getStringExtra(STREAM_NAME_KEY) ?: ""
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

                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (firstVisibleItemPosition == SCROLL_POSITION_FOR_NEXT_PORTION_LOADING
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

        sendButton.setOnClickListener { sendButtonOnClickFunc(enterMessage, topicEditText) }
    }

    private val sendButtonOnClickFunc: (EditText, AutoCompleteTextView) -> (Unit) = { enterMessage, topicEditText ->
        if (enterMessage.text.isNotEmpty()) {
            if (editedMessageId != NO_EDITED_MESSAGE_ID) {
                store.accept(
                    ChatEvent.Ui.EditMessage(
                        messageId = editedMessageId,
                        topicName = binding.topicEditText.text.toString(),
                        content = enterMessage.text.toString()
                    )
                )
            } else {
                val topicName = getTopicNameForSendingMessage(topicEditText)
                store.accept(
                    ChatEvent.Ui.SendMessage(
                        topicName = topicName,
                        streamName = streamName,
                        content = enterMessage.text.toString()
                    )
                )
            }
            hideKeyboard(enterMessage)
        } else {
            if (!hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_REQUEST_CODE
                )
            } else {
                selectFileResultLauncher.launch("*/*")
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
            if (hasFocus && (view as? EditText)?.text?.isEmpty() == true) {
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

    private fun createAndConfigureActionsBottomSheet() {
        val bottomSheetLayout =
            layoutInflater.inflate(R.layout.layout_bottom_sheet_chat_actions, null) as LinearLayout
        actionsDialog = ChatActionsBottomSheetDialog(
            context = this,
            theme = R.style.BottomSheetDialogTheme,
            bottomSheetAddReactionListener = this,
            bottomSheetDeleteMessageListener = this,
            bottomSheetEditMessageListener = this,
            bottomSheetCopyToClipboardListener = this
        )
        actionsDialog.setContentView(bottomSheetLayout)
    }

    private fun createAndConfigureEmojisBottomSheet() {
        val bottomSheetLayout =
            layoutInflater.inflate(R.layout.layout_bottom_sheet_emojis, null) as LinearLayout
        emojisDialog = EmojiBottomSheetDialog(
            context = this,
            theme = R.style.BottomSheetDialogTheme,
            bottomSheet = bottomSheetLayout,
            bottomSheetChooseEmojiListener = this
        )
        emojisDialog.setContentView(bottomSheetLayout)
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
                    else -> ILLEGAL_MESSAGE_ID_FOR_UNKNOWN_VIEW_TYPE
                }
            }
            else -> ILLEGAL_MESSAGE_ID_FOR_UNKNOWN_VIEW_TYPE
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

    private fun processFileUploadedEffect(effect: ChatEffect.FileUploadedEffect) {
        binding.enterMessage.text.append("[${effect.fileName}](${effect.fileUri})\n\n")
        binding.sendButton.showActionIcon(R.drawable.ic_send, R.color.teal_500)
    }

    private fun processStartEditMessageEffect(effect: ChatEffect.StartEditMessageEffect) {
        with(binding) {
            topicEditText.visibility = View.VISIBLE
            topicEditText.setText(effect.message.topicName)
            enterMessage.setText(getFormattedContentFromHtml(effect.message.content)?.trim())
            sendButton.showActionIcon(R.drawable.ic_confirm_edit, R.color.teal_500)
        }
        editedMessageId = effect.message.id
    }

    private fun processMessageEditedEffect() {
        with(binding) {
            sendButton.showActionIcon(R.drawable.ic_add, R.color.grey_500)
            enterMessage.text.clear()
            if (topicName != ALL_TOPICS_IN_STREAM) {
                topicEditText.visibility = View.GONE
            } else {
                topicEditText.text.clear()
            }
        }
        editedMessageId = NO_EDITED_MESSAGE_ID
    }

    private fun processNavigateToChatEffect(effect: ChatEffect.NavigateToChat) {
        startActivity<ChatActivity>(
            Pair(STREAM_NAME_KEY, streamName),
            Pair(TOPIC_NAME_KEY, effect.topicName)
        )
    }

    private fun processMessageLoadingError(effect: ChatEffect.MessagesLoadingError) {
        if (!checkUnknownHostException(effect.error)
            && !checkHttpTooManyRequestsException(effect.error)
        ) {
            binding.root.showSnackBarWithRetryAction(
                resources.getString(R.string.messages_not_found_error_text),
                Snackbar.LENGTH_LONG
            ) { configureChatRecycler() }
        }
    }

    private fun processMessageSendingError(effect: ChatEffect.MessageSendingError) {
        if (!checkUnknownHostException(effect.error)
            && !checkHttpTooManyRequestsException(effect.error)
        ) {
            Toast.makeText(
                this,
                resources.getString(R.string.sending_message_error_text),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun processMessageDeletingError(effect: ChatEffect.MessageDeletingError) {
        if (!checkUnknownHostException(effect.error)
            && !checkHttpTooManyRequestsException(effect.error)
        ) {
            Toast.makeText(
                this,
                resources.getString(R.string.deleting_message_error_text),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun processMessageEditingError(effect: ChatEffect.MessageEditingError) {
        if (!checkUnknownHostException(effect.error)
            && !checkHttpTooManyRequestsException(effect.error)
        ) {
            Toast.makeText(
                this,
                resources.getString(R.string.editing_message_error_text),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun processFileUploadingError(effect: ChatEffect.FileUploadingError) {
        if (!checkUnknownHostException(effect.error)
            && !checkHttpTooManyRequestsException(effect.error)
        ) {
            binding.root.showSnackBarWithRetryAction(
                resources.getString(R.string.uploading_file_error_text),
                Snackbar.LENGTH_LONG
            ) { store.accept(ChatEvent.Ui.UploadFile(effect.fileName, effect.fileBody)) }
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

    private fun copyTextToClipboard(text: CharSequence) {
        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(CLIP_DATA_TEXT_LABEL, text)
        clipboard.setPrimaryClip(clip)
    }

    companion object {

        internal const val STREAM_NAME_KEY = "streamName"
        internal const val TOPIC_NAME_KEY = "topicName"
        internal const val NO_TOPIC_STRING_VALUE = "(no topic)"
        private const val SCROLL_POSITION_FOR_NEXT_PORTION_LOADING = 5
        private const val CLIP_DATA_TEXT_LABEL = "text"
        private const val NO_EDITED_MESSAGE_ID = -1L
        private const val ILLEGAL_MESSAGE_ID_FOR_UNKNOWN_VIEW_TYPE = -1L
        private const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 81
    }

}
