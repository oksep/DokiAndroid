package com.dokiwa.dokidoki.message.im

import android.util.LruCache
import com.dokiwa.dokidoki.message.Log
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import io.reactivex.subjects.BehaviorSubject

private const val TAG = "IMUserInfoProvider"

private val cache = LruCache<String, NimUserInfo>(200)

internal val nimUserInfoSubject = BehaviorSubject.create<List<NimUserInfo>>()

internal fun getCacheNimUser(uuid: String): NimUserInfo? {
    val userInfo = cache.get(uuid)
    if (userInfo == null) {
        fetchRemoteNimUserInfo(uuid)
    }
    return userInfo
}

private fun addNimUserToCache(list: List<NimUserInfo>) {
    list.forEach {
        cache.put(it.account, it)
    }
}

private fun fetchRemoteNimUserInfo(uuid: String) {
    NIMClient.getService(UserService::class.java).fetchUserInfo(listOf(uuid))
        .setCallback(object : RequestCallback<List<NimUserInfo>> {
            override fun onSuccess(users: List<NimUserInfo>) {
                Log.e(TAG, "fetchRemoteNimUserInfo success -> $users")
                addNimUserToCache(users)
                nimUserInfoSubject.onNext(users)
            }

            override fun onFailed(code: Int) {
                Log.e(TAG, "fetchRemoteNimUserInfo failed $code")
            }

            override fun onException(exception: Throwable) {
                Log.e(TAG, "fetchRemoteNimUserInfo error $exception")
            }
        })
}