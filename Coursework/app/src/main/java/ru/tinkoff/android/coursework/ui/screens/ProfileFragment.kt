package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.SELF_USER_ID
import ru.tinkoff.android.coursework.data.getUserById
import ru.tinkoff.android.coursework.databinding.FragmentProfileBinding

internal class ProfileFragment: Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.userOnlineStatus.visibility = View.GONE
        binding.toolbar.visibility = View.VISIBLE
        binding.logoutBtn.visibility = View.GONE

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
                binding.logoutBtn.visibility = View.VISIBLE
            }
        } else {
            binding.username.text = arguments?.getString("username")
            binding.userStatus.text = arguments?.getString("status")
            if (arguments?.getBoolean("onlineStatus") == true) {
                binding.userOnlineStatus.visibility = View.VISIBLE
            }
            if (arguments?.getLong("id") == SELF_USER_ID) {
                binding.toolbar.visibility = View.GONE
                binding.logoutBtn.visibility = View.VISIBLE
                binding.profileAvatarImg.setImageResource(R.drawable.self_avatar)
            } else {
                binding.profileAvatarImg.setImageResource(R.drawable.avatar)
            }
        }
    }
}
