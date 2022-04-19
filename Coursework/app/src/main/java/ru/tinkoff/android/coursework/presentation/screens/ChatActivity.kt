package ru.tinkoff.android.coursework.presentation.screens

import android.Manifest
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.api.NetworkService
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi.Companion.LAST_MESSAGE_ANCHOR
import ru.tinkoff.android.coursework.data.api.ZulipJsonApi.Companion.NUMBER_OF_MESSAGES_BEFORE_ANCHOR
import ru.tinkoff.android.coursework.data.api.model.EmojiWithCountDto
import ru.tinkoff.android.coursework.data.api.model.request.NarrowRequest
import ru.tinkoff.android.coursework.data.api.model.toMessageDbList
import ru.tinkoff.android.coursework.data.EmojiCodes
import ru.tinkoff.android.coursework.databinding.ActivityChatBinding
import ru.tinkoff.android.coursework.data.db.AppDatabase
import ru.tinkoff.android.coursework.data.db.model.Message
import ru.tinkoff.android.coursework.presentation.customviews.*
import ru.tinkoff.android.coursework.presentation.screens.adapters.ChatMessagesAdapter
import ru.tinkoff.android.coursework.presentation.screens.adapters.OnBottomSheetChooseEmojiListener
import ru.tinkoff.android.coursework.presentation.screens.adapters.OnEmojiClickListener
import ru.tinkoff.android.coursework.utils.copy
import ru.tinkoff.android.coursework.utils.getFileNameFromContentUri
import ru.tinkoff.android.coursework.utils.hasPermissions
import ru.tinkoff.android.coursework.utils.showSnackBarWithRetryAction
import java.io.*


internal class ChatActivity : AppCompatActivity(), OnEmojiClickListener,
    OnBottomSheetChooseEmojiListener {

    private lateinit var binding: ActivityChatBinding
    private lateinit var dialog: EmojiBottomSheetDialog
    private lateinit var chatRecycler: RecyclerView
    private lateinit var adapter: ChatMessagesAdapter
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var topicName: String
    private var db: AppDatabase? = null
    private var selectFileResultLauncher = initializeSelectFileResultLauncher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        compositeDisposable = CompositeDisposable()
        db = AppDatabase.getAppDatabase(this)
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

        adapter.streamName = resources.getString(
            R.string.stream_name_text,
            intent.getStringExtra(STREAM_NAME_KEY)
        )

        loadMessagesFromDb(topicName)
        loadMessagesFromApi(isFirstPortion = true)

        chatRecycler.adapter = adapter

        chatRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItemPosition == SCROLL_POSITION_FOR_NEXT_PORTION_LOADING) {
                    this@ChatActivity.loadMessagesFromApi(isFirstPortion = false)
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
                sendMessage(
                    content = enterMessage.text.toString(),
                    stream = intent.getStringExtra(STREAM_NAME_KEY) ?: "",
                    topic = intent.getStringExtra(TOPIC_NAME_KEY) ?: ""
                )
                enterMessage.text.clear()
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(enterMessage.windowToken, 0)
            } else {
                if (!hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
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

    private fun removeReaction(
        emojiView: EmojiWithCountView,
        emojiName: String
    ) {
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
                emoji = EmojiWithCountDto(chosenEmojiCode, 0),
                messageId = messageId,
                emojiClickListener = this
            )
            val emojiName = EmojiCodes.emojiMap[emojiView.emojiCode]
            if (emojiName != null) {
                addReaction(emojiView, emojiName, true, emojiBox)
            }
        }
    }

    private fun loadMessagesFromApi(isFirstPortion: Boolean) {
        binding.progress.visibility = View.VISIBLE
        NetworkService.getZulipJsonApi().getMessages(
            numBefore = adapter.messagesBefore,
            anchor = adapter.anchor.toString(),
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
                    binding.progress.visibility = View.GONE
                    if (it.messages.isNotEmpty() && adapter.anchor != it.messages[0].id - 1) {
                        val newMessages = it.messages.toMessageDbList()
                        adapter.update(newMessages, isFirstPortion)
                        cacheMessages(newMessages)
                    }
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
    ) {
        NetworkService.getZulipJsonApi().sendMessage(
            to = stream,
            content = content,
            topic = topic
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onSuccess = {
                    adapter.anchor = LAST_MESSAGE_ANCHOR
                    loadMessagesFromApi(isFirstPortion = true)
                },
                onError = {
                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.sending_message_error_text),
                        Snackbar.LENGTH_LONG
                    ) { sendMessage(content, stream, topic) }
                }
            )
            .addTo(compositeDisposable)
    }

    private fun loadMessagesFromDb(topicName: String) {
        db?.messageDao()?.getAllByTopic(topicName)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeBy(
                onSuccess = {
                    if (it.isNotEmpty()) {
                        with(adapter) {
                            binding.progress.visibility = View.GONE
                            messages = it
                            notifyDataSetChanged()
                        }
                    }
                },
                onError = {
                    Log.e(TAG, resources.getString(R.string.loading_messages_from_db_error_text), it)
                }
            )
            ?.addTo(compositeDisposable)
    }

    private fun cacheMessages(newMessages: List<Message>) {
        if (adapter.messages.size <= MAX_NUMBER_OF_MESSAGES_IN_CACHE) {
            val remainingMessagesLimit =
                if (MAX_NUMBER_OF_MESSAGES_IN_CACHE - adapter.messages.size > NUMBER_OF_MESSAGES_PER_PORTION) {
                    NUMBER_OF_MESSAGES_PER_PORTION
                } else {
                    MAX_NUMBER_OF_MESSAGES_IN_CACHE - adapter.messages.size
                }
            saveMessagesToDb(newMessages.takeLast(remainingMessagesLimit))
        } else {
            saveMessagesToDb(adapter.messages.takeLast(MAX_NUMBER_OF_MESSAGES_IN_CACHE))
            val actualMessageIds = adapter.messages.takeLast(MAX_NUMBER_OF_MESSAGES_IN_CACHE).map { it.id }
            removeRedundantMessagesFromDb(topicName, actualMessageIds)
        }

    }

    private fun removeRedundantMessagesFromDb(topicName: String, actualMessageIds: List<Long>) {
        db?.messageDao()?.removeRedundant(topicName, actualMessageIds)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeBy(
                onError = {
                    Log.e(TAG, resources.getString(R.string.removing_messages_from_db_error_text), it)
                }
            )
            ?.addTo(compositeDisposable)
    }

    private fun saveMessagesToDb(messages: List<Message>) {
        db?.messageDao()?.saveAll(messages)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeBy(
                onError = {
                    Log.e(TAG, resources.getString(R.string.saving_messages_to_db_error_text), it)
                }
            )
            ?.addTo(compositeDisposable)
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
            uploadFile(body, fileName)
        }

    private fun uploadFile(fileBody: MultipartBody.Part, fileName: String) {
        NetworkService.getZulipJsonApi().uploadFile(fileBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onSuccess = {
                    binding.enterMessage.text.append("[$fileName](${it.uri})\n\n")
                    binding.sendButton.showActionIcon(R.drawable.ic_send, R.color.teal_500)
                },
                onError = {
                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.uploading_file_error_text),
                        Snackbar.LENGTH_LONG
                    ) { uploadFile(fileBody, fileName) }
                }
            )
            .addTo(compositeDisposable)
    }

    companion object {

        const val STREAM_NAME_KEY = "streamName"
        const val TOPIC_NAME_KEY = "topicName"
        const val TOPIC_NARROW_OPERATOR_KEY = "topic"
        private const val TAG = "ChatActivity"
        private const val MAX_NUMBER_OF_MESSAGES_IN_CACHE = 50
        private const val NUMBER_OF_MESSAGES_PER_PORTION = NUMBER_OF_MESSAGES_BEFORE_ANCHOR
        private const val SCROLL_POSITION_FOR_NEXT_PORTION_LOADING = 5
    }

}
