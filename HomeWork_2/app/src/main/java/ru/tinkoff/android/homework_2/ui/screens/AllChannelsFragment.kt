package ru.tinkoff.android.homework_2.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.tinkoff.android.homework_2.databinding.FragmentAllChannelsBinding

class AllChannelsFragment: Fragment() {

    private lateinit var binding: FragmentAllChannelsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllChannelsBinding.inflate(inflater,container,false)
        return binding.root
    }
}