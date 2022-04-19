package ru.tinkoff.android.coursework.presentation.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.api.NetworkService
import ru.tinkoff.android.coursework.data.api.model.SELF_USER_ID
import ru.tinkoff.android.coursework.databinding.FragmentProfileBinding
import ru.tinkoff.android.coursework.data.api.model.UserDto
import ru.tinkoff.android.coursework.data.db.AppDatabase
import ru.tinkoff.android.coursework.di.GlobalDi
import ru.tinkoff.android.coursework.presentation.elm.people.models.PeopleEvent
import ru.tinkoff.android.coursework.presentation.elm.profile.models.ProfileEffect
import ru.tinkoff.android.coursework.presentation.elm.profile.models.ProfileEvent
import ru.tinkoff.android.coursework.presentation.elm.profile.models.ProfileState
import ru.tinkoff.android.coursework.utils.showSnackBarWithRetryAction
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store

internal class ProfileFragment : ElmFragment<ProfileEvent, ProfileEffect, ProfileState>() {

    override var initEvent: ProfileEvent = ProfileEvent.Ui.InitEvent
    private lateinit var binding: FragmentProfileBinding
    private var db: AppDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getAppDatabase(requireContext())
        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        if (arguments == null) store.accept(ProfileEvent.Ui.LoadOwnProfile) else store.accept(ProfileEvent.Ui.LoadProfile(requireArguments()))

        /*if (arguments != null) {
            store.stop()
            store.start()
            store.accept(ProfileEvent.Ui.LoadProfile(requireArguments()))
        }*/

        /*val user: UserDto
        if (arguments == null) {
            getUserFromDb(SELF_USER_ID)
            getOwnUserFromApi()
        } else {
            user = UserDto(
                userId = requireArguments().getLong(USER_ID_KEY),
                fullName = requireArguments().getString(USERNAME_KEY),
                email = requireArguments().getString(EMAIL_KEY),
                avatarUrl = requireArguments().getString(AVATAR_KEY),
                presence = requireArguments().getString(USER_PRESENCE_KEY)
            )
            fillViewsWithUserData(user)
        }*/
    }

    override fun createStore(): Store<ProfileEvent, ProfileEffect, ProfileState> {
        return GlobalDi.INSTANCE.profileElmStoreFactory.provide()
    }

    override fun render(state: ProfileState) {
        if (state.items.isNotEmpty()) fillViewsWithUserData(state.items[0])
    }

    override fun handleEffect(effect: ProfileEffect): Unit? {
        return super.handleEffect(effect)
    }

    private fun fillViewsWithUserData(user: UserDto) {
        binding.username.text = user.fullName
        binding.userPresence.text = user.presence
        when (user.presence) {
            ACTIVE_PRESENCE_KEY -> binding.userPresence.setTextColor(
                binding.root.context.getColor(ACTIVE_PRESENCE_COLOR)
            )
            IDLE_PRESENCE_KEY -> binding.userPresence.setTextColor(
                binding.root.context.getColor(IDLE_PRESENCE_COLOR)
            )
            else -> binding.userPresence.setTextColor(
                binding.root.context.getColor(OFFLINE_PRESENCE_COLOR)
            )
        }

        if (user.avatarUrl != null) {
            Glide.with(binding.root)
                .asBitmap()
                .load(user.avatarUrl)
                .error(R.drawable.default_avatar)
                .into(binding.profileAvatarImg)
        } else {
            binding.profileAvatarImg.setImageResource(R.drawable.default_avatar)
        }
    }

   /* private fun getUserFromDb(userId: Long) {
        db?.userDao()?.getById(userId)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeBy(
                onSuccess = {
                    fillViewsWithUserData(it.toUserDto())
                },
                onError = {
                    Log.e(TAG, resources.getString(R.string.loading_users_from_db_error_text), it)
                }
            )
            ?.addTo(disposables)
    }

    private fun getOwnUserFromApi() {
        NetworkService.getZulipJsonApi().getOwnUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onSuccess = {
                    getUserPresence(it)
                },
                onError = {
                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.user_not_found_error_text),
                        Snackbar.LENGTH_LONG
                    ) { getOwnUserFromApi() }
                }
            )
            .addTo(disposables)
    }

    private fun getUserPresence(user: UserDto) {
        NetworkService.getZulipJsonApi().getUserPresence(
            userIdOrEmail = user.userId.toString()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onSuccess = {
                    user.presence = it?.presence?.aggregated?.status
                    fillViewsWithUserData(user)
                },
                onError = {
                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.user_not_found_error_text),
                        Snackbar.LENGTH_LONG
                    ) { getUserPresence(user) }
                }
            )
            .addTo(disposables)
    }*/

    companion object {

        const val USER_ID_KEY = "id"
        const val USERNAME_KEY = "username"
        const val EMAIL_KEY = "email"
        const val USER_PRESENCE_KEY = "presence"
        const val AVATAR_KEY = "avatar"

        const val ACTIVE_PRESENCE_KEY = "active"
        const val IDLE_PRESENCE_KEY = "idle"

        const val ACTIVE_PRESENCE_COLOR = R.color.green_500
        const val IDLE_PRESENCE_COLOR = R.color.orange_500
        const val OFFLINE_PRESENCE_COLOR = R.color.red_500

        private const val TAG = "ProfileFragment"
    }

}
