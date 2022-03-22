package ru.tinkoff.android.homework_2.ui.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ru.tinkoff.android.homework_2.R
import ru.tinkoff.android.homework_2.databinding.FragmentChannelsBinding
import ru.tinkoff.android.homework_2.ui.screens.adapters.ChannelsListPagerAdapter
import java.lang.Exception

class ChannelsFragment: Fragment() {

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

    /*private fun changeTabsFont(tabLayout: TabLayout) {
        val vg = tabLayout.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount
        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup
            val tabChildCount = vgTab.childCount
            for (i in 0 until tabChildCount) {
                val tabViewChild = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {
                    try {
                        tabViewChild.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter)
                    } catch (e: Exception) {
                        Log.e(TAG, "Font resource didn't loaded")
                    }
                    tabViewChild.isAllCaps = false
                }
            }
        }
    }*/

    /*companion object {

        private const val TAG = "ChannelsFragment"
    }*/
}
