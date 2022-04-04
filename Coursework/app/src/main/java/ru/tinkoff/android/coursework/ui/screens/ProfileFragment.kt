package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.testdata.SELF_USER_ID
import ru.tinkoff.android.coursework.testdata.getUserById
import ru.tinkoff.android.coursework.databinding.FragmentProfileBinding

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
        binding.userOnlineStatus.visibility = View.GONE
        binding.toolbar.visibility = View.VISIBLE

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        if (arguments == null) {
            val user = getUserById(SELF_USER_ID)
            if (user != null) {
                binding.username.text = user.name
                binding.userStatus.text = user.status
                binding.profileAvatarImg.setImageResource(R.drawable.self_avatar)
                if (user.isOnline) {
                    binding.userOnlineStatus.visibility = View.VISIBLE
                }
                binding.toolbar.visibility = View.GONE
            }
        } else {
            binding.username.text = arguments?.getString(USERNAME_KEY)
            binding.userStatus.text = arguments?.getString(USER_STATUS_KEY)
            if (arguments?.getBoolean(USER_ONLINE_STATUS_KEY) == true) {
                binding.userOnlineStatus.visibility = View.VISIBLE
            }
            if (arguments?.getLong(USER_ID_KEY) == SELF_USER_ID) {
                binding.toolbar.visibility = View.GONE
                binding.profileAvatarImg.setImageResource(R.drawable.self_avatar)
            } else {
                binding.profileAvatarImg.setImageResource(R.drawable.avatar)
            }
        }
    }

    companion object {

        const val USER_ID_KEY = "id"
        const val USERNAME_KEY = "username"
        const val USER_STATUS_KEY = "status"
        const val USER_ONLINE_STATUS_KEY = "onlineStatus"
    }
}
