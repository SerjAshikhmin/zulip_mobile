package ru.tinkoff.android.coursework.presentation.customviews

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.StyleRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.tinkoff.android.coursework.databinding.LayoutBottomSheetChatActionsBinding
import ru.tinkoff.android.coursework.presentation.screens.listeners.OnBottomSheetAddReactionListener
import ru.tinkoff.android.coursework.presentation.screens.listeners.OnBottomSheetCopyToClipboardListener
import ru.tinkoff.android.coursework.presentation.screens.listeners.OnBottomSheetDeleteMessageListener
import ru.tinkoff.android.coursework.presentation.screens.listeners.OnBottomSheetEditMessageListener

internal class ChatActionsBottomSheetDialog (
    context: Context,
    @StyleRes theme: Int,
    private val bottomSheetAddReactionListener: OnBottomSheetAddReactionListener,
    private val bottomSheetDeleteMessageListener: OnBottomSheetDeleteMessageListener,
    private val bottomSheetEditMessageListener: OnBottomSheetEditMessageListener,
    private val bottomSheetCopyToClipboardListener: OnBottomSheetCopyToClipboardListener
) : BottomSheetDialog(context, theme) {

    private lateinit var binding: LayoutBottomSheetChatActionsBinding
    private var selectedView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutBottomSheetChatActionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initActionsClickListeners()
        checkActionsToShow()
    }

    fun show(view: View) {
        selectedView = view
        if (::binding.isInitialized) {
            checkActionsToShow()
        }
        show()
    }

    private fun checkActionsToShow() {
        if (selectedView is MessageViewGroup) {
            binding.deleteMessageAction.visibility = View.GONE
            binding.editMessageAction.visibility = View.GONE
        }
        if (selectedView is SelfMessageViewGroup) {
            binding.deleteMessageAction.visibility = View.VISIBLE
            binding.editMessageAction.visibility = View.VISIBLE
        }
    }

    private fun initActionsClickListeners() {
        binding.addReactionAction.setOnClickListener {
            bottomSheetAddReactionListener.onBottomSheetAddReaction(selectedView)
        }

        binding.deleteMessageAction.setOnClickListener {
            bottomSheetDeleteMessageListener.onBottomSheetDeleteMessage(selectedView)
        }

        binding.copyToClipboardAction.setOnClickListener {
            bottomSheetCopyToClipboardListener.onBottomSheetCopyToClipboard(selectedView)
        }

        binding.editMessageAction.setOnClickListener {
            bottomSheetEditMessageListener.onBottomSheetEditMessage(selectedView)
        }
    }

}
