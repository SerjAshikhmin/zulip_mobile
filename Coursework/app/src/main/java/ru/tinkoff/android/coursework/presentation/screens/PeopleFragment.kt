package ru.tinkoff.android.coursework.presentation.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import ru.tinkoff.android.coursework.App
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.databinding.FragmentPeopleBinding
import ru.tinkoff.android.coursework.di.ActivityScope
import ru.tinkoff.android.coursework.di.people.DaggerPeopleComponent
import ru.tinkoff.android.coursework.domain.model.User
import ru.tinkoff.android.coursework.presentation.elm.people.PeopleElmStoreFactory
import ru.tinkoff.android.coursework.presentation.elm.people.models.PeopleEffect
import ru.tinkoff.android.coursework.presentation.elm.people.models.PeopleEvent
import ru.tinkoff.android.coursework.presentation.elm.people.models.PeopleState
import ru.tinkoff.android.coursework.presentation.screens.adapters.PeopleListAdapter
import ru.tinkoff.android.coursework.presentation.screens.listeners.OnUserItemClickListener
import ru.tinkoff.android.coursework.utils.checkHttpTooManyRequestsException
import ru.tinkoff.android.coursework.utils.checkUnknownHostException
import ru.tinkoff.android.coursework.utils.showSnackBarWithRetryAction
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

@ActivityScope
internal class PeopleFragment
    : ElmFragment<PeopleEvent, PeopleEffect, PeopleState>(), OnUserItemClickListener {

    @Inject
    internal lateinit var peopleElmStoreFactory: PeopleElmStoreFactory

    override val initEvent: PeopleEvent = PeopleEvent.Ui.InitEvent
    private lateinit var adapter: PeopleListAdapter
    private lateinit var binding: FragmentPeopleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        binding = FragmentPeopleBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        store.accept(PeopleEvent.Ui.LoadPeopleList)

        binding.swipeRefreshLayout.setOnRefreshListener {
            store.accept(PeopleEvent.Ui.UpdatePeopleList)
        }

        adapter = PeopleListAdapter(this)
        binding.peopleListRecycler.adapter = adapter
    }

    override fun createStore(): Store<PeopleEvent, PeopleEffect, PeopleState> {
        val peopleComponent = DaggerPeopleComponent.factory().create(
            (activity?.application as App).applicationComponent
        )
        peopleComponent.inject(this)
        return peopleElmStoreFactory.provide()
    }

    override fun render(state: PeopleState) {
        if (!state.isLoading) {
            binding.swipeRefreshLayout.isRefreshing = false
        }
        with(adapter) {
            showShimmer = state.isLoading && !binding.swipeRefreshLayout.isRefreshing
            users = state.items
            notifyDataSetChanged()
        }
    }

    override fun handleEffect(effect: PeopleEffect) {
        when(effect) {
            is PeopleEffect.PeopleListLoadError -> {
                if (!requireContext().checkUnknownHostException(effect.error)
                    && !requireContext().checkHttpTooManyRequestsException(effect.error)
                ) {
                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.people_not_found_error_text),
                        Snackbar.LENGTH_LONG
                    ) { store.accept(PeopleEvent.Ui.LoadPeopleList) }
                }
            }
            is PeopleEffect.NavigateToProfile -> {
                NavHostFragment.findNavController(binding.root.findFragment())
                    .navigate(R.id.action_nav_people_to_nav_user, effect.bundle)
            }
        }
    }

    override fun onUserItemClick(user: User) {
        val bundle = bundleOf(
            ProfileFragment.USER_ID_KEY to user.userId,
            ProfileFragment.USERNAME_KEY to user.fullName,
            ProfileFragment.EMAIL_KEY to user.email,
            ProfileFragment.AVATAR_KEY to user.avatarUrl,
            ProfileFragment.USER_PRESENCE_KEY to user.presence
        )
        store.accept(PeopleEvent.Ui.LoadProfile(bundle))
    }

    companion object {

        const val NOT_FOUND_PRESENCE_KEY = "not found"
    }

}
