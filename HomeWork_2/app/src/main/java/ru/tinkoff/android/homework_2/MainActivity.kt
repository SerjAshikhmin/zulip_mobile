package ru.tinkoff.android.homework_2

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.tinkoff.android.homework_2.data.EmojiCodes
import ru.tinkoff.android.homework_2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createAndConfigureBottomSheet()
        configureEnterMessageSection()
    }

    private fun configureEnterMessageSection() {
        val enterMessage = binding.enterMessage
        val sendButton = binding.sendButton

        enterMessage.doAfterTextChanged {
            if (enterMessage.text.isEmpty()) {
                sendButton.setImageResource(R.drawable.baseline_add_20)
                //sendButton.backgroundTintList =
                //    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.darkGreyVariant))
            }
            if (enterMessage.text.length == 1) {
                sendButton.setImageResource(R.drawable.baseline_send_20)
                //sendButton.backgroundTintList =
                //    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.teal_500))
            }
        }

        sendButton.setOnClickListener {
            if (enterMessage.text.isNotEmpty()) {
                println(enterMessage.text)
                enterMessage.text.clear()
                //enterMessage.onEditorAction(EditorInfo.IME_ACTION_DONE);
                val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(enterMessage.windowToken, 0)
            }
        }
    }

    private fun createAndConfigureBottomSheet() {
        val bottomSheet = layoutInflater.inflate(R.layout.layout_bottom_sheet, null) as LinearLayout

        EmojiCodes.values.forEach { emojiCode ->
            val emojiView = LayoutInflater.from(this).inflate(
                R.layout.layout_bottom_sheet_emoji, null
            ) as TextView
            emojiView.text = emojiCode
            (bottomSheet.getChildAt(1) as FlexboxLayout).addView(emojiView)
        }

        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        dialog.setContentView(bottomSheet)

        val messageGroup = binding.messageGroup
        messageGroup.setOnLongClickListener {
            return@setOnLongClickListener messageOnClickFunc(dialog)
        }

        val selfMessageGroup = binding.selfMessageGroup
        selfMessageGroup.setOnLongClickListener {
            return@setOnLongClickListener messageOnClickFunc(dialog)
        }

        bottomSheet.children
            .filter { child -> child is TextView }
            .forEach { child ->
                child.setOnClickListener {
                    println((child as TextView).text)
                    dialog.dismiss()
                }
            }

        bottomSheet.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun messageOnClickFunc(dialog: BottomSheetDialog): Boolean {
        dialog.show()
        return true
    }
}
