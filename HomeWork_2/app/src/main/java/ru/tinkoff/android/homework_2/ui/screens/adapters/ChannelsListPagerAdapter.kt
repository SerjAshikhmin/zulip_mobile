package ru.tinkoff.android.homework_2.ui.screens.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.tinkoff.android.homework_2.ui.screens.AllChannelsFragment
import ru.tinkoff.android.homework_2.ui.screens.SubscribedFragment

class ChannelsListPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SubscribedFragment()
            else -> AllChannelsFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}