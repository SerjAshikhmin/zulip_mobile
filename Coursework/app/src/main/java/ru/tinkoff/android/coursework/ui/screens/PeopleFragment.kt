package ru.tinkoff.android.coursework.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.tinkoff.android.coursework.data.users
import ru.tinkoff.android.coursework.data.usersWithTestError
import ru.tinkoff.android.coursework.databinding.FragmentPeopleBinding
import ru.tinkoff.android.coursework.model.User
import ru.tinkoff.android.coursework.ui.screens.adapters.PeopleListAdapter

internal class PeopleFragment: Fragment() {

    private lateinit var binding: FragmentPeopleBinding
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPeopleBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        compositeDisposable = CompositeDisposable()
        configurePeopleListRecycler()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }

    private fun configurePeopleListRecycler() {
        val peopleListRecycle = binding.peopleList
        val layoutManager = LinearLayoutManager(context)
        peopleListRecycle.layoutManager = layoutManager
        val adapter = PeopleListAdapter()

        Single.fromCallable { (usersWithTestError()) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (object : SingleObserver<MutableList<User>> {

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "People not found", Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(t: MutableList<User>) {
                    adapter.users = t
                }
            })

        peopleListRecycle.adapter = adapter
    }
}
