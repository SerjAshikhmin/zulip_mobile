package ru.tinkoff.android.coursework.presentation.screens

import android.Manifest
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
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
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi.Companion.LAST_MESSAGE_ANCHOR
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
import ru.tinkoff.android.coursework.presentation.screens.adapters.ChatMessagesAdapter
import ru.tinkoff.android.coursework.presentation.screens.adapters.OnBottomSheetChooseEmojiListener
import ru.tinkoff.android.coursework.presentation.screens.adapters.OnEmojiClickListener
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
    OnEmojiClickListener, OnBottomSheetChooseEmojiListener {

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
    private var selectedEmojiView: EmojiWithCountView? = null
    private var selectedEmojiName: String? = null
    private var isNewSelectedEmojiView: Boolean = false
    private var emojiBox: FlexBoxLayout? = null
    private var uploadingFileName: String? = null
    private var uploadingFileBody: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createAndConfigureBottomSheet()
        configureEnterMessageSection()
        configureChatRecycler()
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
        when {
            state.updateAllMessages -> {
                with (adapter) {
                    anchor = LAST_MESSAGE_ANCHOR
                    messages = state.items
                    notifyDataSetChanged()
                }
            }
            state.updateWithPortion && state.items.isNotEmpty()
                    && adapter.anchor != state.items[0].id - 1 -> {
                adapter.updateWithNextPortion(state.items, state.isFirstPortion)
            }
            state.isReactionAdded -> {
                selectedEmojiView?.isSelected = true
                selectedEmojiView?.emojiCount = selectedEmojiView?.emojiCount?.plus(1)!!
                if (isNewSelectedEmojiView) {
                    selectedEmojiView?.let { addNewEmojiToEmojiBox(emojiBox, it) }
                }
            }
            state.isReactionRemoved -> {
                selectedEmojiView?.isSelected = false
                selectedEmojiView?.emojiCount = selectedEmojiView?.emojiCount?.minus(1)!!
                if (selectedEmojiView?.emojiCount == 0) {
                    val emojiBox = (selectedEmojiView?.parent as FlexBoxLayout)
                    emojiBox.removeView(selectedEmojiView)
                    if (emojiBox.childCount == 1) {
                        emojiBox.getChildAt(0).visibility = View.GONE
                    }
                }
            }
            state.isFileUploaded -> {
                binding.enterMessage.text.append("[$uploadingFileName](${state.fileUri})\n\n")
                binding.sendButton.showActionIcon(R.drawable.ic_send, R.color.teal_500)
            }
        }
    }

    override fun handleEffect(effect: ChatEffect) {
        when(effect) {
            is ChatEffect.MessageSentEffect -> {
                binding.enterMessage.text.clear()
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
                ) { uploadingFileBody?.let { ChatEvent.Ui.UploadFile(it) }?.let { store.accept(it) } }
            }
        }
    }

    override fun onEmojiClick(emojiView: EmojiWithCountView) {
        val emojiName = EmojiCodes.emojiMap[emojiView.emojiCode]
        if (emojiName != null) {
            if (!emojiView.isSelected) {
                selectedEmojiView = emojiView
                selectedEmojiName = emojiName
                isNewSelectedEmojiView = false
                emojiBox = null
                store.accept(ChatEvent.Ui.AddReaction(emojiView.messageId, emojiName))
            } else {
                selectedEmojiView = emojiView
                selectedEmojiName = emojiName
                store.accept(ChatEvent.Ui.RemoveReaction(emojiView.messageId, emojiName))
            }
        }
    }

    private fun configureToolbar() {
        with(binding) {
            backIcon.setOnClickListener {
                onBackPressed()
            }
            topicName.text = adapter.topicName
            streamName.text = adapter.streamName
        }
    }

    private fun configureChatRecycler() {
        chatRecycler = binding.chatRecycler
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        chatRecycler.layoutManager = layoutManager
        adapter = ChatMessagesAdapter(dialog, chatRecycler, this)

        topicName = intent.getStringExtra(TOPIC_NAME_KEY)?.lowercase() ?: ""
        adapter.topicName = resources.getString(
            R.string.topic_name_text,
            topicName
        )

        streamName = intent.getStringExtra(STREAM_NAME_KEY)?.lowercase() ?: ""
        adapter.streamName = resources.getString(
            R.string.stream_name_text,
            intent.getStringExtra(STREAM_NAME_KEY)
        )

        store.accept(ChatEvent.Ui.LoadLastMessages(
            topicName = topicName,
            currentAnchor = adapter.anchor,
            isFirstPortion = true,
            updateAllMessages = true
        ))

        chatRecycler.adapter = adapter

        chatRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItemPosition == SCROLL_POSITION_FOR_NEXT_PORTION_LOADING) {
                    store.accept(ChatEvent.Ui.LoadPortionOfMessages(
                        topicName = topicName,
                        currentAnchor = adapter.anchor,
                        isFirstPortion = false
                    ))
                }
            }
        })
    }

    private fun configureEnterMessageSection() {
        val enterMessage = binding.enterMessage
        val sendButton = binding.sendButton

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
                store.accept(ChatEvent.Ui.SendMessage(
                    topicName = topicName,
                    streamName = streamName,
                    content = enterMessage.text.toString()
                ))
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(enterMessage.windowToken, 0)
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
                selectedEmojiView = emojiView
                selectedEmojiName = emojiName
                isNewSelectedEmojiView = true
                this.emojiBox = emojiBox
                store.accept(ChatEvent.Ui.AddReaction(emojiView.messageId, emojiName))
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
            uploadingFileName = fileName
            uploadingFileBody = body
            store.accept(ChatEvent.Ui.UploadFile(body))
        }

    companion object {

        const val STREAM_NAME_KEY = "streamName"
        const val TOPIC_NAME_KEY = "topicName"
        const val TOPIC_NARROW_OPERATOR_KEY = "topic"
        private const val SCROLL_POSITION_FOR_NEXT_PORTION_LOADING = 5
    }

}
