package ru.tinkoff.android.coursework.ui.customviews

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.EmojiCodes
import ru.tinkoff.android.coursework.model.EmojiWithCount
import ru.tinkoff.android.coursework.ui.screens.adapters.OnBottomSheetChooseEmojiListener
import ru.tinkoff.android.coursework.ui.screens.adapters.OnEmojiClickListener

internal class EmojiBottomSheetDialog(
    context: Context,
    @StyleRes theme: Int,
    private var bottomSheet: LinearLayout,
    private val emojiClickListener: OnEmojiClickListener,
    private val bottomSheetChooseEmojiListener: OnBottomSheetChooseEmojiListener
) : BottomSheetDialog(context, theme) {

    private var chosenEmojiCode = ""
    private var selectedView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createEmojiViews()
    }

    fun show(view: View) {
        selectedView = view
        show()
    }

    override fun dismiss() {
        super.dismiss()
        processSelectedEmoji(selectedView, chosenEmojiCode)
    }

    private fun processSelectedEmoji(selectedView: View?, chosenEmojiCode: String) {
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
                val messageId = when (selectedView) {
                    is MessageViewGroup -> selectedView.messageId
                    is SelfMessageViewGroup -> selectedView.messageId
                    else -> 0L
                }
                if (emojiBox != null) {
                    val emojiView = EmojiWithCountView.createEmojiWithCountView(
                        emojiBox,
                        EmojiWithCount(chosenEmojiCode, 1),
                        messageId,
                        emojiClickListener
                    )
                    emojiView.isSelected = true
                    emojiBox.addView(emojiView, emojiBox.childCount - 1)
                    if (emojiBox.childCount > 1) {
                        emojiBox.getChildAt(emojiBox.childCount - 1).visibility = View.VISIBLE
                    }
                    bottomSheetChooseEmojiListener.onBottomSheetChooseEmoji(
                        emojiView.isSelected,
                        emojiView.emojiCode,
                        messageId
                    )
                }
            }
        }
    }

    private fun createEmojiViews() {
        EmojiCodes.emojiMap.forEach { emoji ->
            val emojiView = LayoutInflater.from(context).inflate(
                R.layout.layout_bottom_sheet_emoji, null
            ) as TextView
            emojiView.text = emoji.key
            emojiView.setOnClickListener {
                chosenEmojiCode = emojiView.text.toString()
                dismiss()
            }
            ((bottomSheet.getChildAt(1) as NestedScrollView)
                .getChildAt(0) as FlexBoxLayout).addView(emojiView)
        }
    }

}
