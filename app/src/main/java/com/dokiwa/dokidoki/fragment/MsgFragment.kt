package com.dokiwa.dokidoki.fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dokiwa.dokidoki.R

class MsgFragment : Fragment() {

    companion object {
        fun newInstance() = MsgFragment()
    }

    private lateinit var viewModel: MsgViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_msg, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MsgViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
