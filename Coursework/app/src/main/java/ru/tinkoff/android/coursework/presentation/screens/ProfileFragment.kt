package ru.tinkoff.android.coursework.presentation.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import ru.tinkoff.android.coursework.App
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.databinding.FragmentProfileBinding
import ru.tinkoff.android.coursework.di.ActivityScope
import ru.tinkoff.android.coursework.di.profile.DaggerProfileComponent
import ru.tinkoff.android.coursework.domain.model.User
import ru.tinkoff.android.coursework.presentation.elm.profile.ProfileElmStoreFactory
import ru.tinkoff.android.coursework.presentation.elm.profile.models.ProfileEffect
import ru.tinkoff.android.coursework.presentation.elm.profile.models.ProfileEvent
import ru.tinkoff.android.coursework.presentation.elm.profile.models.ProfileState
import ru.tinkoff.android.coursework.utils.showSnackBarWithRetryAction
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

@ActivityScope
internal class ProfileFragment : ElmFragment<ProfileEvent, ProfileEffect, ProfileState>() {

    @Inject
    internal lateinit var profileElmStoreFactory: ProfileElmStoreFactory

    override var initEvent: ProfileEvent = ProfileEvent.Ui.InitEvent
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

        if (arguments == null) {
            store.accept(ProfileEvent.Ui.LoadOwnProfile)
        } else {
            store.accept(ProfileEvent.Ui.LoadProfile(requireArguments()))
        }
    }

    override fun createStore(): Store<ProfileEvent, ProfileEffect, ProfileState> {
        val profileComponent = DaggerProfileComponent.factory().create(
            (activity?.application as App).applicationComponent
        )
        profileComponent.inject(this)
        return profileElmStoreFactory.provide()
    }

    override fun render(state: ProfileState) {
        if (state.items.isNotEmpty()) fillViewsWithUserData(state.items[0])
    }

    override fun handleEffect(effect: ProfileEffect) {
        when(effect) {
            is ProfileEffect.ProfileLoadError -> {
                Log.e(TAG, resources.getString(R.string.user_not_found_error_text), effect.error)
                binding.root.showSnackBarWithRetryAction(
                    resources.getString(R.string.user_not_found_error_text),
                    Snackbar.LENGTH_LONG
                ) { store.accept(ProfileEvent.Ui.LoadOwnProfile) }
            }
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
