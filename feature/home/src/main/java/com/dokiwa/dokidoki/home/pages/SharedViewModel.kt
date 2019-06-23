//package com.dokiwa.dokidoki.home.pages
//
//import androidx.lifecycle.ViewModel
//import com.dokiwa.dokidoki.center.api.Api
//import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
//import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
//import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
//import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
//import com.dokiwa.dokidoki.home.Log
//import com.dokiwa.dokidoki.home.api.HomeApi
//
///**
// * Created by Septenary on 2018/12/31.
// */
//private const val TAG = "HomeSharedViewModel"
//
//class SharedViewModel : ViewModel(), CompositeDisposableContext {
//
//    private val disposableContainer by lazy { CompositeDisposable() }
//
//    init {
//        preloadLoginUserProfile()
//    }
//
//    override fun addDispose(dispose: Disposable) {
//        disposableContainer.add(dispose)
//    }
//
//    val mineViewModel = MineViewModel()
//
//    private fun preloadLoginUserProfile() {
//        Api.get(HomeApi::class.java)
//            .getMeProfile()
//            .subscribeApi(this, {
//                mineViewModel.profile = it
//                IProfilePlugin.get().saveLoginUserProfile(it.profile)
//            }, {
//                IProfilePlugin.get().getLoginUserProfile()?.let { profile ->
//                    mineViewModel.profile = UserProfileWrap(profile)
//                }
//                Log.e(TAG, "preloadLoginUserProfile() failed", it)
//            })
//    }
//
//    override fun onCleared() {
//        disposableContainer.dispose()
//    }
//}
//
//data class MineViewModel(var profile: UserProfileWrap? = null)