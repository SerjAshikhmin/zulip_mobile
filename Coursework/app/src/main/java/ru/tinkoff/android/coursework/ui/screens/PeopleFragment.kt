package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.tinkoff.android.coursework.data.users
import ru.tinkoff.android.coursework.databinding.FragmentPeopleBinding
import ru.tinkoff.android.coursework.ui.screens.adapters.PeopleListAdapter

internal class PeopleFragment: Fragment() {

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

    private fun configurePeopleListRecycler() {
        val peopleListRecycle = binding.peopleList
        val layoutManager = LinearLayoutManager(context)
        peopleListRecycle.layoutManager = layoutManager
        val adapter = PeopleListAdapter()
        adapter.users = users
        peopleListRecycle.adapter = adapter
    }
}
