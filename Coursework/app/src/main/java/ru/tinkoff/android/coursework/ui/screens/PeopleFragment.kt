package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.usersWithTestErrorAndDelay
import ru.tinkoff.android.coursework.databinding.FragmentPeopleBinding
import ru.tinkoff.android.coursework.model.User
import ru.tinkoff.android.coursework.ui.screens.adapters.OnUserItemClickListener
import ru.tinkoff.android.coursework.ui.screens.adapters.PeopleListAdapter

internal class PeopleFragment: Fragment(), OnUserItemClickListener {

    private lateinit var binding: FragmentPeopleBinding
    private lateinit var compositeDisposable: CompositeDisposable

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
        compositeDisposable = CompositeDisposable()
        configurePeopleListRecycler()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
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
        val adapter = PeopleListAdapter(this)

        Single.fromCallable { (usersWithTestErrorAndDelay()) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (object : SingleObserver<MutableList<User>> {

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "People not found", Toast.LENGTH_SHORT).show()
                    adapter.showShimmer = false
                    adapter.users = mutableListOf()
                    adapter.notifyDataSetChanged()
                }

                override fun onSuccess(t: MutableList<User>) {
                    adapter.showShimmer = false
                    adapter.users = t
                    adapter.notifyDataSetChanged()
                }
            })

        binding.peopleList.adapter = adapter
    }
}
