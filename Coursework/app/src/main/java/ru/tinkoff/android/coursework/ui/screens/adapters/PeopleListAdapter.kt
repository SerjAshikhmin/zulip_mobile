package ru.tinkoff.android.coursework.ui.screens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.SELF_USER_ID
import ru.tinkoff.android.coursework.databinding.ItemUserInPeopleListBinding
import ru.tinkoff.android.coursework.model.User
import ru.tinkoff.android.coursework.ui.screens.ProfileFragment.Companion.USERNAME_KEY
import ru.tinkoff.android.coursework.ui.screens.ProfileFragment.Companion.USER_ID_KEY
import ru.tinkoff.android.coursework.ui.screens.ProfileFragment.Companion.USER_ONLINE_STATUS_KEY
import ru.tinkoff.android.coursework.ui.screens.ProfileFragment.Companion.USER_STATUS_KEY

internal class PeopleListAdapter: RecyclerView.Adapter<PeopleListAdapter.PeopleListViewHolder>() {

    var users: List<User>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    private val differ = AsyncListDiffer(this, DiffCallback())

    class DiffCallback: DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleListViewHolder {
        val userItemBinding = ItemUserInPeopleListBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PeopleListViewHolder(userItemBinding)
    }

    override fun onBindViewHolder(holder: PeopleListViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    class PeopleListViewHolder(private val binding: ItemUserInPeopleListBinding): RecyclerView.ViewHolder(binding.root) {

        private val username = binding.username
        private val email = binding.email
        private val avatar = binding.profileAvatar
        private val onlineStatusCard = binding.onlineStatusCard

        fun bind(user: User) {
            username.text = user.name
            email.text = user.email
            if (user.id == SELF_USER_ID) {
                avatar.setImageResource(R.drawable.self_avatar)
            } else {
                avatar.setImageResource(R.drawable.avatar)
            }
            onlineStatusCard.visibility = if (!user.isOnline) View.GONE else View.VISIBLE
            initListener(user)
        }

        private fun initListener(user: User) {
            binding.root.setOnClickListener {
                val bundle = bundleOf(
                    USER_ID_KEY to user.id,
                    USERNAME_KEY to user.name,
                    USER_STATUS_KEY to user.status,
                    USER_ONLINE_STATUS_KEY to user.isOnline
                )
                binding.root.findNavController().navigate(R.id.action_nav_people_to_nav_profile, bundle)
            }
        }
    }

}
