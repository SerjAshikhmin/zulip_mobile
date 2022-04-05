package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.api.NetworkService
import ru.tinkoff.android.coursework.databinding.FragmentProfileBinding
import ru.tinkoff.android.coursework.model.User
import ru.tinkoff.android.coursework.model.response.UserPresenceResponse

internal class ProfileFragment: Fragment() {

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
            user = getOwnUser()
            user.presence = getUserPresence(user)?.presence?.aggregated?.status
        } else {
            user = User(
                requireArguments().getLong(USER_ID_KEY),
                requireArguments().getString(USERNAME_KEY),
                requireArguments().getString(EMAIL_KEY),
                requireArguments().getString(AVATAR_KEY),
                requireArguments().getString(USER_PRESENCE_KEY)
            )
        }

        binding.username.text = user.name
        binding.userPresence.text = user.presence
        when (user.presence) {
            ACTIVE_PRESENCE_KEY -> binding.userPresence.setTextColor(ContextCompat.getColor(binding.root.context, R.color.green_500))
            IDLE_PRESENCE_KEY -> binding.userPresence.setTextColor(ContextCompat.getColor(binding.root.context, R.color.orange_500))
            else -> binding.userPresence.setTextColor(ContextCompat.getColor(binding.root.context, R.color.red_500))
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

    private fun getOwnUser(): User {
        return NetworkService.getZulipJsonApi().getOwnUser()
            .subscribeOn(Schedulers.io())
            .blockingGet()
    }

    private fun getUserPresence(user: User): UserPresenceResponse? {
        return NetworkService.getZulipJsonApi().getUserPresence(userIdOrEmail = user.id.toString())
            .subscribeOn(Schedulers.io())
            .blockingGet()
    }

    companion object {

        const val USER_ID_KEY = "id"
        const val USERNAME_KEY = "username"
        const val EMAIL_KEY = "email"
        const val USER_PRESENCE_KEY = "presence"
        const val AVATAR_KEY = "avatar"

        const val ACTIVE_PRESENCE_KEY = "active"
        const val IDLE_PRESENCE_KEY = "idle"
    }
}
