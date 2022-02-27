package ru.tinkoff.android.homework1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView

class FirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        if (intent.hasExtra("contactList")) {
            val contactList = intent.getSerializableExtra("contactList") as ArrayList<*>?
            val listView = findViewById<ListView>(R.id.contact_list)
            if (contactList != null) {
                val listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contactList)
                listView.adapter = listAdapter
            }
        }
    }

}
