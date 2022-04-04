package ru.tinkoff.android.coursework.ui.screens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.testdata.SELF_USER_ID
import ru.tinkoff.android.coursework.databinding.ItemUserInPeopleListBinding
import ru.tinkoff.android.coursework.model.User

internal class PeopleListAdapter(private val userItemClickListener: OnUserItemClickListener)
    : RecyclerView.Adapter<PeopleListAdapter.PeopleListViewHolder>() {

    var showShimmer = true

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
        if (showShimmer) {
            holder.shimmerFrameLayout.startShimmer()
        } else {
            holder.shimmerFrameLayout.stopShimmer()
            holder.shimmerFrameLayout.setShimmer(null)
            holder.avatar.foreground = null
            holder.username.foreground = null
            holder.email.foreground = null
            holder.onlineStatusCard.foreground = null
            holder.bind(users[position])
        }
    }

    override fun getItemCount(): Int {
        return if (showShimmer) SHIMMER_ITEM_COUNT else users.size
    }

    inner class PeopleListViewHolder(private val binding: ItemUserInPeopleListBinding)
        : RecyclerView.ViewHolder(binding.root) {

        internal val username = binding.username
        internal val email = binding.email
        internal val avatar = binding.profileAvatar
        internal val onlineStatusCard = binding.onlineStatusCard
        internal val shimmerFrameLayout = binding.shimmerLayout

        fun bind(user: User) {
            username.text = user.name
            email.text = user.email
            if (user.id == SELF_USER_ID) {
                avatar.setImageResource(R.drawable.self_avatar)
            } else {
                avatar.setImageResource(R.drawable.avatar)
            }
            onlineStatusCard.visibility = if (!user.isOnline) View.GONE else View.VISIBLE
            this@PeopleListAdapter.userItemClickListener.onTopicItemClickListener(binding.root, user)
        }
    }

    companion object {

        const val SHIMMER_ITEM_COUNT = 2
    }
}
