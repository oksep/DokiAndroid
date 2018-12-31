package com.dokiwa.dokidoki.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.dokiwa.dokidoki.home.OnPageSelectedListener
import com.dokiwa.dokidoki.home.R

class MeFragment : Fragment(), OnPageSelectedListener {

    companion object {
        fun newInstance() = MeFragment()
    }

    private val sharedModel by lazy {
        ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun onPageSelected() {
        context?.let {
            // FeaturePlugin.get(ILoginPlugin::class.java).launchLoginActivity(it)
        }
    }
}
