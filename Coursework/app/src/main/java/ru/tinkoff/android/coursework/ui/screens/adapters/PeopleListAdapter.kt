package ru.tinkoff.android.coursework.ui.screens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.data.SELF_USER_ID
import ru.tinkoff.android.coursework.model.User

internal class PeopleListAdapter: RecyclerView.Adapter<PeopleListAdapter.PeopleListViewHolder>() {

    internal var users: List<User>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    private val differ = AsyncListDiffer(this, DiffCallback())

    internal class DiffCallback: DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    internal class PeopleListViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        private val username = view.findViewById<TextView>(R.id.username)
        private val email = view.findViewById<TextView>(R.id.email)
        private val avatar = view.findViewById<ImageView>(R.id.profile_avatar)

        fun bind(user: User) {
            username.text = user.name
            email.text = user.email
            if (user.id == SELF_USER_ID) {
                avatar.setImageResource(R.drawable.self_avatar)
            } else {
                avatar.setImageResource(R.drawable.avatar)
            }
            initListener(user)
        }

        private fun initListener(user: User) {
            view.setOnClickListener {
                val bundle = bundleOf(
                    "id" to user.id,
                    "username" to user.name,
                    "status" to user.status,
                    "onlineStatus" to user.isOnline
                )
                view.findNavController().navigate(R.id.nav_profile, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleListViewHolder {
        val userItemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_in_people_list, parent, false)
        return PeopleListViewHolder(userItemView)
    }

    override fun onBindViewHolder(holder: PeopleListViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

}
