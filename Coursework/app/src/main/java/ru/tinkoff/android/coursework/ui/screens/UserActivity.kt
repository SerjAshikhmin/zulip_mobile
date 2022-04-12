package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profileFragment = ProfileFragment()
        profileFragment.arguments = intent.extras
        supportFragmentManager.beginTransaction()
            .replace(R.id.profile_container, profileFragment)
            .commit()
    }

}
