package com.dokiwa.dokidoki.center.base.fragment

import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Septenary on 2019/1/2.
 */
open class BaseFragment : Fragment(), CompositeDisposableContext {

    private val disposableContainer by lazy { CompositeDisposable() }

    override fun addDispose(dispose: Disposable) {
        disposableContainer.add(dispose)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposableContainer.dispose()
    }
}