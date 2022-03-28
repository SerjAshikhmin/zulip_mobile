package ru.tinkoff.android.homework1

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class SecondActivityContract : ActivityResultContract<String, List<String>?>() {

    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(context, SecondActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<String>? = when {
        resultCode != Activity.RESULT_OK -> null
        else -> {
            var result: List<String>? = emptyList()
            if (intent != null && intent.hasExtra(GetContactListService.CONTRACT_LIST_EXTRA)) {
                result = intent.getSerializableExtra(GetContactListService.CONTRACT_LIST_EXTRA) as ArrayList<String>
            }
            result
        }
    }
}
