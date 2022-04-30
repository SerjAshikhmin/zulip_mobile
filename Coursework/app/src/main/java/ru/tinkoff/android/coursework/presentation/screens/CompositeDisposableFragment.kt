package ru.tinkoff.android.coursework.presentation.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable

abstract class CompositeDisposableFragment : Fragment() {

    lateinit var disposables: CompositeDisposable

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disposables = CompositeDisposable()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.dispose()
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

}
