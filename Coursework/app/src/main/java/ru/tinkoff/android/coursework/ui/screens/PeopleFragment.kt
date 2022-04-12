package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.api.NetworkService
import ru.tinkoff.android.coursework.databinding.FragmentPeopleBinding
import ru.tinkoff.android.coursework.model.User
import ru.tinkoff.android.coursework.ui.screens.adapters.OnUserItemClickListener
import ru.tinkoff.android.coursework.ui.screens.adapters.PeopleListAdapter
import ru.tinkoff.android.coursework.ui.screens.utils.showSnackBarWithRetryAction

internal class PeopleFragment: CompositeDisposableFragment(), OnUserItemClickListener {

    private lateinit var binding: FragmentPeopleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        binding = FragmentPeopleBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurePeopleListRecycler()
    }

    override fun onUserItemClick(user: User) {
        val bundle = bundleOf(
            ProfileFragment.USER_ID_KEY to user.userId,
            ProfileFragment.USERNAME_KEY to user.fullName,
            ProfileFragment.EMAIL_KEY to user.email,
            ProfileFragment.AVATAR_KEY to user.avatarUrl,
            ProfileFragment.USER_PRESENCE_KEY to user.presence
        )
        NavHostFragment.findNavController(binding.root.findFragment())
            .navigate(R.id.action_nav_people_to_nav_user, bundle)
    }

    private fun configurePeopleListRecycler() {
        val adapter = PeopleListAdapter(this)

        NetworkService.getZulipJsonApi().getAllUsers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    adapter.apply {
                        showShimmer = false
                        users = it.members
                        users.forEach { user ->
                            getUserPresence(user)
                        }
                        notifyDataSetChanged()
                    }
                },
                onError = {
                    adapter.apply {
                        showShimmer = false
                        users = listOf()
                        notifyDataSetChanged()
                    }

                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.people_not_found_error_text),
                        Snackbar.LENGTH_LONG
                    ) { configurePeopleListRecycler() }
                }
            ).addTo(compositeDisposable)

        binding.peopleList.adapter = adapter
    }

    private fun getUserPresence(user: User) {
        NetworkService.getZulipJsonApi().getUserPresence(userIdOrEmail = user.userId.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    user.presence = it.presence.aggregated?.status ?: NOT_FOUND_PRESENCE_KEY
                },
                onError = {
                    user.presence = NOT_FOUND_PRESENCE_KEY
                }
            )
            .addTo(compositeDisposable)
    }

    companion object {

        const val NOT_FOUND_PRESENCE_KEY = "not found"
    }

}
