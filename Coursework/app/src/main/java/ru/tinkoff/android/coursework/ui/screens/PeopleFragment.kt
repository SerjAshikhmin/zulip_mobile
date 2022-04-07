package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.usersTestData
import ru.tinkoff.android.coursework.databinding.FragmentPeopleBinding
import ru.tinkoff.android.coursework.model.User
import ru.tinkoff.android.coursework.ui.screens.adapters.OnUserItemClickListener
import ru.tinkoff.android.coursework.ui.screens.adapters.PeopleListAdapter

internal class PeopleFragment: Fragment(), OnUserItemClickListener {

    private lateinit var binding: FragmentPeopleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPeopleBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurePeopleListRecycler()
    }

    override fun onTopicItemClickListener(topicItemView: View?, user: User) {
        topicItemView?.setOnClickListener {
            val bundle = bundleOf(
                ProfileFragment.USER_ID_KEY to user.id,
                ProfileFragment.USERNAME_KEY to user.name,
                ProfileFragment.USER_STATUS_KEY to user.status,
                ProfileFragment.USER_ONLINE_STATUS_KEY to user.isOnline
            )
            NavHostFragment.findNavController(binding.root.findFragment())
                .navigate(R.id.action_nav_people_to_nav_profile, bundle)
        }
    }

    private fun configurePeopleListRecycler() {
        binding.peopleList.adapter = PeopleListAdapter(this).apply { users = usersTestData }
    }

}
