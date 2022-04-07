package ru.tinkoff.android.coursework.ui.screens.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.tinkoff.android.coursework.ui.screens.AllChannelsFragment
import ru.tinkoff.android.coursework.ui.screens.SubscribedFragment

internal class ChannelsListPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SubscribedFragment()
            else -> AllChannelsFragment()
        }
    }

    override fun getItemCount(): Int = 2

}
