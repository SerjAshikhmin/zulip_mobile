package ru.tinkoff.android.coursework.ui.customviews

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StyleRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.EmojiCodes
import ru.tinkoff.android.coursework.ui.BottomSheetCallback

internal class EmojiBottomSheetDialog(
    context: Context,
    @StyleRes theme: Int,
    private var bottomSheet: LinearLayout,
    private val bottomSheetCallback: BottomSheetCallback
): BottomSheetDialog(context, theme) {

    private var chosenEmojiCode = ""
    private var selectedView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createEmojiViews()
    }

    fun show(view: View) {
        selectedView = view
        this.show()
    }

    override fun dismiss() {
        super.dismiss()
        bottomSheetCallback.callbackMethod(selectedView, chosenEmojiCode)
    }

    private fun createEmojiViews() {
        EmojiCodes.values.forEach { emojiCode ->
            val emojiView = LayoutInflater.from(context).inflate(
                R.layout.layout_bottom_sheet_emoji, null
            ) as TextView
            emojiView.text = emojiCode
            emojiView.setOnClickListener {
                chosenEmojiCode = emojiView.text.toString()
                dismiss()
            }
            (bottomSheet.getChildAt(1) as FlexBoxLayout).addView(emojiView)
        }
    }
}
