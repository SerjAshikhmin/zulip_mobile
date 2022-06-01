package ru.tinkoff.android.coursework.presentation.screens.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.databinding.ItemUserInPeopleListBinding
import ru.tinkoff.android.coursework.domain.model.User
import ru.tinkoff.android.coursework.presentation.screens.ProfileFragment.Companion.ACTIVE_PRESENCE_COLOR
import ru.tinkoff.android.coursework.presentation.screens.ProfileFragment.Companion.ACTIVE_PRESENCE_KEY
import ru.tinkoff.android.coursework.presentation.screens.ProfileFragment.Companion.IDLE_PRESENCE_COLOR
import ru.tinkoff.android.coursework.presentation.screens.ProfileFragment.Companion.IDLE_PRESENCE_KEY
import ru.tinkoff.android.coursework.presentation.screens.ProfileFragment.Companion.OFFLINE_PRESENCE_COLOR
import ru.tinkoff.android.coursework.presentation.screens.listeners.OnUserItemClickListener

internal class PeopleListAdapter(private val userItemClickListener: OnUserItemClickListener)
    : RecyclerView.Adapter<PeopleListAdapter.PeopleListViewHolder>() {

    var showShimmer = true

    var users: List<User>
        set(value) = differ.submitList(value.sortedBy { it.fullName })
        get() = differ.currentList

    private val differ = AsyncListDiffer(this, DiffCallback())

    class DiffCallback: DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.userId == newItem.userId
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
            with (holder) {
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.setShimmer(null)
                avatar.foreground = null
                username.foreground = null
                email.foreground = null
                onlineStatusCard.foreground = null
                bind(users[position])
            }
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
            username.text = user.fullName
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
                ACTIVE_PRESENCE_KEY -> ColorStateList.valueOf(binding.root.context.getColor(ACTIVE_PRESENCE_COLOR))
                IDLE_PRESENCE_KEY -> ColorStateList.valueOf(binding.root.context.getColor(IDLE_PRESENCE_COLOR))
                else -> ColorStateList.valueOf(binding.root.context.getColor(OFFLINE_PRESENCE_COLOR))
            }

            binding.root.setOnClickListener {
                this@PeopleListAdapter.userItemClickListener.onUserItemClick(user)
            }
        }
    }

    companion object {

        const val SHIMMER_ITEM_COUNT = 5
    }

}
