package ru.tinkoff.android.coursework.ui.screens.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.tinkoff.android.coursework.ui.screens.AllChannelsFragment
import ru.tinkoff.android.coursework.ui.screens.SubscribedFragment

internal class ChannelsListPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    lateinit var subscribedFragment: SubscribedFragment
    lateinit var allChannelsFragment: AllChannelsFragment

    fun isSubscribedFragment() = ::subscribedFragment.isInitialized
    fun isAllChannelsFragment() = ::allChannelsFragment.isInitialized

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                subscribedFragment = SubscribedFragment()
                subscribedFragment
            }
            else -> {
                allChannelsFragment = AllChannelsFragment()
                allChannelsFragment
            }
        }
    }

    override fun getItemCount(): Int = 2

}
