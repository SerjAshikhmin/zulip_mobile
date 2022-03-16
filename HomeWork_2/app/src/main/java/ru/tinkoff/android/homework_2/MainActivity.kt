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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.tinkoff.android.homework_2.data.EmojiCodes
import ru.tinkoff.android.homework_2.data.Messages
import ru.tinkoff.android.homework_2.databinding.ActivityMainBinding
import ru.tinkoff.android.homework_2.ui.ChatMessagesAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createAndConfigureBottomSheet()
        configureEnterMessageSection()
        configureChatRecycler()
    }

    private fun configureChatRecycler() {
        val chatRecycler = binding.chat
        chatRecycler.layoutManager = LinearLayoutManager(this)
        chatRecycler.adapter = ChatMessagesAdapter(Messages.messages, dialog)
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

        dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        dialog.setContentView(bottomSheet)

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
}
