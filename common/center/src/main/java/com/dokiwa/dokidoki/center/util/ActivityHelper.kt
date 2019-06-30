package com.dokiwa.dokidoki.center.util

import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.dokiwa.dokidoki.center.util.ActivityHelper.ActivityResult
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject

class ActivityHelper(activity: FragmentActivity) {

    data class ActivityResult(val requestCode: Int, val resultCode: Int, val data: Intent?)

    private val activityResultFragment: OnActivityResultFragment

    init {
        activityResultFragment = getResultFragment(activity)
    }

    private fun getResultFragment(activity: FragmentActivity): OnActivityResultFragment {
        return findResultFragment(activity) ?: OnActivityResultFragment().also {
            val fragmentManager = activity.supportFragmentManager
            fragmentManager
                .beginTransaction()
                .add(it, TAG)
                .commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()
        }
    }

    private fun findResultFragment(activity: FragmentActivity): OnActivityResultFragment? {
        return activity.supportFragmentManager.findFragmentByTag(TAG) as? OnActivityResultFragment
    }

    fun startForResult(launch: (Fragment, Int) -> Unit, requestCode: Int): Single<ActivityResult> {
        return activityResultFragment.startForResult(launch, requestCode)
    }

    fun startForResult(intent: Intent, requestCode: Int): Single<ActivityResult> {
        return activityResultFragment.startForResult(intent, requestCode)
    }

    companion object {
        private const val TAG = "OnActivityResultHelper"
    }
}

internal class OnActivityResultFragment : Fragment() {
    private val holder = SparseArray<SingleSubject<ActivityResult>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun startForResult(launch: (Fragment, Int) -> Unit, requestCode: Int): Single<ActivityResult> {
        val subject = SingleSubject.create<ActivityResult>()
        return subject.doOnSubscribe {
            holder.put(requestCode, subject)
            if (isAdded && activity != null) {
                launch.invoke(this, requestCode)
            }
        }
    }

    fun startForResult(intent: Intent, requestCode: Int): Single<ActivityResult> {
        val subject = SingleSubject.create<ActivityResult>()
        return subject.doOnSubscribe {
            holder.put(requestCode, subject)
            if (isAdded && activity != null) {
                startActivityForResult(intent, requestCode)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        holder.get(requestCode)?.onSuccess(ActivityResult(requestCode, resultCode, data))
        holder.remove(requestCode)
    }
}