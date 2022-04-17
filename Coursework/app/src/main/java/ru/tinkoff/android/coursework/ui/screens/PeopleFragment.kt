package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.util.Log
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
import ru.tinkoff.android.coursework.api.model.UserDto
import ru.tinkoff.android.coursework.db.AppDatabase
import ru.tinkoff.android.coursework.db.model.User
import ru.tinkoff.android.coursework.db.model.toUsersDtoList
import ru.tinkoff.android.coursework.ui.screens.adapters.OnUserItemClickListener
import ru.tinkoff.android.coursework.ui.screens.adapters.PeopleListAdapter
import ru.tinkoff.android.coursework.utils.showSnackBarWithRetryAction

internal class PeopleFragment: CompositeDisposableFragment(), OnUserItemClickListener {

    private lateinit var binding: FragmentPeopleBinding
    private var db: AppDatabase? = null

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
        db = AppDatabase.getAppDatabase(requireContext())
        configurePeopleListRecycler()
    }

    override fun onUserItemClick(user: UserDto) {
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

        loadUsersFromDb(adapter)
        loadUsersFromApi(adapter)

        binding.peopleList.adapter = adapter
    }

    private fun loadUsersFromApi(adapter: PeopleListAdapter) {
        NetworkService.getZulipJsonApi().getAllUsers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    with(adapter) {
                        showShimmer = false
                        users = it.members
                        users.forEach { user ->
                            getUserPresence(user)
                        }
                        notifyDataSetChanged()
                    }
                },
                onError = {
                    with(adapter) {
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
    }

    private fun loadUsersFromDb(adapter: PeopleListAdapter) {
        db?.userDao()?.getAll()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeBy(
                onSuccess = {
                    if (it.isNotEmpty()) {
                        with(adapter) {
                            showShimmer = false
                            users = it.toUsersDtoList()
                            notifyDataSetChanged()
                        }
                    }
                },
                onError = {
                    Log.e(TAG, resources.getString(R.string.loading_users_from_db_error_text), it)
                }
            )
            ?.addTo(compositeDisposable)
    }

    private fun saveUserToDb(user: User) {
        db?.userDao()?.save(user)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeBy(
                onError = {
                    Log.e(TAG, resources.getString(R.string.saving_user_to_db_error_text), it)
                }
            )
            ?.addTo(compositeDisposable)
    }

    private fun getUserPresence(user: UserDto) {
        NetworkService.getZulipJsonApi().getUserPresence(userIdOrEmail = user.userId.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    user.presence = it.presence.aggregated?.status ?: NOT_FOUND_PRESENCE_KEY
                    saveUserToDb(user.toUserDb())
                },
                onError = {
                    user.presence = NOT_FOUND_PRESENCE_KEY
                }
            )
            .addTo(compositeDisposable)
    }

    companion object {

        const val NOT_FOUND_PRESENCE_KEY = "not found"
        private const val TAG = "PeopleFragment"
    }

}
