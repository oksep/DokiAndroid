package com.dokiwa.dokidoki.profile.create.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.dokiwa.dokidoki.center.base.fragment.BaseFragment
import com.dokiwa.dokidoki.profile.create.model.SharedViewModel

/**
 * Created by Septenary on 2019/1/2.
 */
internal abstract class BaseStepFragment : BaseFragment() {

    private val sharedViewModel by lazy {
        ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateContent(sharedViewModel)
    }

    abstract fun updateContent(viewModel: SharedViewModel)

    abstract val layoutId: Int

    abstract fun reset()

    fun requestNextStep() {
        (activity as? IStepFragmentInteract)?.requestNextStep()
    }

    fun requestPreStep() {
        (activity as? IStepFragmentInteract)?.requestPreStep()
    }

    abstract val skipable: Boolean
}

internal interface IStepFragmentInteract {
    fun requestNextStep()
    fun requestPreStep()
}