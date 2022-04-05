package ru.tinkoff.android.coursework.ui.screens.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.databinding.ItemUserInPeopleListBinding
import ru.tinkoff.android.coursework.model.User
import ru.tinkoff.android.coursework.ui.screens.ProfileFragment.Companion.ACTIVE_PRESENCE_KEY
import ru.tinkoff.android.coursework.ui.screens.ProfileFragment.Companion.IDLE_PRESENCE_KEY

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

            if (user.avatarUrl != null) {
                Glide.with(binding.root)
                    .asBitmap()
                    .load(user.avatarUrl)
                    .error(R.drawable.default_avatar)
                    .into(avatar)
            } else {
                avatar.setImageResource(R.drawable.default_avatar)
            }

            onlineStatusCard.backgroundTintList = when (user.presence) {
                ACTIVE_PRESENCE_KEY -> ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.green_500))
                IDLE_PRESENCE_KEY -> ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.orange_500))
                else -> ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.red_500))
            }

            this@PeopleListAdapter.userItemClickListener.onTopicItemClickListener(binding.root, user)
        }
    }

    companion object {

        const val SHIMMER_ITEM_COUNT = 2
    }
}
