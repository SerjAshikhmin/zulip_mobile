package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
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
import ru.tinkoff.android.coursework.api.NetworkService
import ru.tinkoff.android.coursework.databinding.FragmentProfileBinding
import ru.tinkoff.android.coursework.api.model.User
import ru.tinkoff.android.coursework.ui.screens.utils.showSnackBarWithRetryAction

internal class ProfileFragment: CompositeDisposableFragment() {

    private lateinit var binding: FragmentProfileBinding

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

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val user: User
        if (arguments == null) {
            getOwnUser()
        } else {
            user = User(
                userId = requireArguments().getLong(USER_ID_KEY),
                fullName = requireArguments().getString(USERNAME_KEY),
                email = requireArguments().getString(EMAIL_KEY),
                avatarUrl = requireArguments().getString(AVATAR_KEY),
                presence = requireArguments().getString(USER_PRESENCE_KEY)
            )
            fillViewsWithUserData(user)
        }
    }

    private fun fillViewsWithUserData(user: User) {
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

    private fun getOwnUser() {
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
                    ) { getOwnUser() }
                }
            )
            .addTo(compositeDisposable)
    }

    private fun getUserPresence(user: User) {
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
            .addTo(compositeDisposable)
    }

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
    }

}
