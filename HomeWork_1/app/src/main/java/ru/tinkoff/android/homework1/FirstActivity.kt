package ru.tinkoff.android.homework1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView

class FirstActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var listAdapter: ArrayAdapter<String>

    private val secondActivityLauncher = registerForActivityResult(SecondActivityContract()) { result ->
        if (result != null && result.isNotEmpty()) {
            listAdapter.clear()
            listAdapter.addAll(result)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        listView = findViewById(R.id.contact_list)
        listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        listView.adapter = listAdapter

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            secondActivityLauncher.launch("")
        }
    }

}
