package ru.tinkoff.android.homework_2.ui.customviews

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.StyleRes
import androidx.core.view.children
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.tinkoff.android.homework_2.model.EmojiWithCount
import ru.tinkoff.android.homework_2.ui.BottomSheetCallback

class EmojiBottomSheetDialog(
    context: Context,
    @StyleRes theme: Int,
    private val bottomSheetCallback: BottomSheetCallback
): BottomSheetDialog(context, theme) {

    private var selectedView: View? = null

    fun show(view: View) {
        selectedView = view
        this.show()
    }

    override fun dismiss() {
        super.dismiss()
        val emojiCode = bottomSheetCallback.callbackMethod()
        val emojiBox = when(selectedView) {
            is MessageViewGroup -> {
                (selectedView as MessageViewGroup).binding.emojiBox
            }
            is SelfMessageViewGroup -> {
                (selectedView as SelfMessageViewGroup).binding.emojiBox
            }
            is ImageView -> {
                 (selectedView as ImageView).parent as FlexBoxLayout
            }
            else -> null
        }
        val emoji = emojiBox?.children?.firstOrNull {
            it is EmojiWithCountView && it.emojiCode == emojiCode
        }
        if (emoji is EmojiWithCountView) emoji.emojiCount++ else {
            if (emojiBox != null) {
                val emojiView = EmojiWithCountView.createEmojiWithCountView(
                    emojiBox,
                    EmojiWithCount(emojiCode, 1)
                )
                emojiBox.addView(emojiView, emojiBox.childCount - 1)
                if (emojiBox.childCount > 1) {
                    emojiBox.getChildAt(emojiBox.childCount - 1).visibility = View.VISIBLE
                }
            }
        }

    }
}