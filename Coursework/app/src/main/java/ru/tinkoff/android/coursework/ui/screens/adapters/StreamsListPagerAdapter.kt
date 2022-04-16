package ru.tinkoff.android.coursework.ui.screens.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.tinkoff.android.coursework.ui.screens.AllStreamsListFragment
import ru.tinkoff.android.coursework.ui.screens.SubscribedStreamsListFragment

internal class StreamsListPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    lateinit var subscribedStreamsListFragment: SubscribedStreamsListFragment
    lateinit var allStreamsListFragment: AllStreamsListFragment

    fun isSubscribedStreamsListFragment() = ::subscribedStreamsListFragment.isInitialized
    fun isAllStreamsListFragment() = ::allStreamsListFragment.isInitialized

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                subscribedStreamsListFragment = SubscribedStreamsListFragment()
                subscribedStreamsListFragment
            }
            else -> {
                allStreamsListFragment = AllStreamsListFragment()
                allStreamsListFragment
            }
        }
    }

    override fun getItemCount(): Int = 2

}
