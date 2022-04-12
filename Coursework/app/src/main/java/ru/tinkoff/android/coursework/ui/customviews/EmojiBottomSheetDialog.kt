package ru.tinkoff.android.coursework.ui.customviews

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.EmojiCodes
import ru.tinkoff.android.coursework.ui.screens.adapters.OnBottomSheetChooseEmojiListener

internal class EmojiBottomSheetDialog(
    context: Context,
    @StyleRes theme: Int,
    private var bottomSheet: LinearLayout,
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
        if (chosenEmojiCode.isEmpty()) return
        bottomSheetChooseEmojiListener.onBottomSheetChooseEmoji(selectedView, chosenEmojiCode)
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
