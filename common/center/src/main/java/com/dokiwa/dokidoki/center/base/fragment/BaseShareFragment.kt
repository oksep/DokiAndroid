package com.dokiwa.dokidoki.center.base.fragment

import android.util.SparseArray
import androidx.annotation.LayoutRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Septenary on 2019-06-23.
 */
abstract class BaseShareFragment(@LayoutRes contentLayoutId: Int) : BaseFragment(contentLayoutId) {

    protected val sharedViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(SharedViewModel::class.java)
    }

    protected inline fun <reified T> getModel(key: Int): T? {
        return sharedViewModel.getModel<T>(key)
    }

    protected fun putModel(key: Int, model: Any) {
        sharedViewModel.putModel(key, model)
    }

    protected class SharedViewModel : ViewModel(), CompositeDisposableContext {

        val holder: SparseArray<Any> = SparseArray()

        private val disposableContainer by lazy { CompositeDisposable() }

        override fun addDispose(dispose: Disposable) {
            disposableContainer.add(dispose)
        }

        override fun onCleared() {
            disposableContainer.dispose()
        }

        inline fun <reified T> getModel(key: Int): T? {
            val t = holder.get(key)
            return if (t is T) t else null
        }

        fun putModel(key: Int, model: Any) {
            holder.put(key, model)
        }
    }
}