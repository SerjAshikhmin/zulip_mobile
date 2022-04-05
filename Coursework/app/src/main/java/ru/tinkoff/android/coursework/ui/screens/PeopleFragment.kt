package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.api.NetworkService
import ru.tinkoff.android.coursework.databinding.FragmentPeopleBinding
import ru.tinkoff.android.coursework.model.User
import ru.tinkoff.android.coursework.model.response.AllUsersListResponse
import ru.tinkoff.android.coursework.ui.screens.adapters.OnUserItemClickListener
import ru.tinkoff.android.coursework.ui.screens.adapters.PeopleListAdapter
import ru.tinkoff.android.coursework.ui.screens.utils.showSnackBarWithRetryAction

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

    override fun onUserItemClick(
        topicItemView: View?,
        user: User
    ) {
        topicItemView?.setOnClickListener {
            val bundle = bundleOf(
                ProfileFragment.USER_ID_KEY to user.id,
                ProfileFragment.USERNAME_KEY to user.name,
                ProfileFragment.EMAIL_KEY to user.email,
                ProfileFragment.AVATAR_KEY to user.avatarUrl,
                ProfileFragment.USER_PRESENCE_KEY to user.presence
            )
            NavHostFragment.findNavController(binding.root.findFragment())
                .navigate(R.id.action_nav_people_to_nav_profile, bundle)
        }
    }

    private fun configurePeopleListRecycler() {
        val adapter = PeopleListAdapter(this)

        NetworkService.getZulipJsonApi().getAllUsers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (object : SingleObserver<AllUsersListResponse> {

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onError(e: Throwable) {
                    adapter.showShimmer = false
                    adapter.users = mutableListOf()
                    adapter.notifyDataSetChanged()

                    showSnackBarWithRetryAction(
                        binding.root,
                        "People not found",
                        Snackbar.LENGTH_LONG
                    ) { configurePeopleListRecycler() }
                }

                override fun onSuccess(t: AllUsersListResponse) {
                    adapter.showShimmer = false
                    adapter.users = t.members

                    adapter.users.forEach { user ->
                        getUserPresence(user)
                    }
                    adapter.notifyDataSetChanged()
                }
            })

        binding.peopleList.adapter = adapter
    }

    private fun getUserPresence(user: User) {
        NetworkService.getZulipJsonApi().getUserPresence(userIdOrEmail = user.id.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    user.presence = it.presence.aggregated?.status ?: "not found"
                },
                onError = {
                    user.presence = "not found"
                }
            )
            .addTo(compositeDisposable)
    }

}
