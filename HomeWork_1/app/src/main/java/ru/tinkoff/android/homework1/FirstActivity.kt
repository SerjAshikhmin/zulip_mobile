package ru.tinkoff.android.homework1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView

class FirstActivity : AppCompatActivity() {

    private val secondActivityLauncher = registerForActivityResult(SecondActivityContract()) { result ->
        if (result != null && result.isNotEmpty()) {
            val listView = findViewById<ListView>(R.id.contact_list)
            val listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, result)
            listView.adapter = listAdapter
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            secondActivityLauncher.launch("")
        }
    }

}
