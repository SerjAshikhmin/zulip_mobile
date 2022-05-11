package ru.tinkoff.android.coursework.presentation.customviews

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.StyleRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.tinkoff.android.coursework.databinding.LayoutBottomSheetChatActionsBinding
import ru.tinkoff.android.coursework.presentation.screens.listeners.OnBottomSheetAddReactionListener
import ru.tinkoff.android.coursework.presentation.screens.listeners.OnBottomSheetDeleteMessageListener

internal class ChatActionsBottomSheetDialog (
    context: Context,
    @StyleRes theme: Int,
    private val bottomSheetAddReactionListener: OnBottomSheetAddReactionListener,
    private val bottomSheetDeleteMessageListener: OnBottomSheetDeleteMessageListener,
) : BottomSheetDialog(context, theme) {

    private lateinit var binding: LayoutBottomSheetChatActionsBinding
    private var selectedView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutBottomSheetChatActionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initActionsClickListeners()
    }

    fun show(view: View) {
        selectedView = view
        show()
    }

    private fun initActionsClickListeners() {
        binding.addReactionAction.setOnClickListener {
            bottomSheetAddReactionListener.onBottomSheetAddReaction(selectedView)
        }

        binding.deleteMessageAction.setOnClickListener {
            bottomSheetDeleteMessageListener.onBottomSheetDeleteMessage(selectedView)
        }
    }

}
