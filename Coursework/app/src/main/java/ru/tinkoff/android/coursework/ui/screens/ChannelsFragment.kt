package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import ru.tinkoff.android.coursework.databinding.FragmentChannelsBinding
import ru.tinkoff.android.coursework.ui.screens.adapters.ChannelsListPagerAdapter

internal class ChannelsFragment: Fragment() {

    private lateinit var binding: FragmentChannelsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChannelsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureViewPager()
    }

    private fun configureViewPager() {
        val viewPager = binding.pager
        val tabLayout = binding.tabLayout

        val pagerAdapter = ChannelsListPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabNames = listOf("Subscribed", "All streams")
            tab.text = tabNames[position]
        }.attach()
    }
}
