package com.dokiwa.dokidoki.profile

import com.dokiwa.dokidoki.center.sp.BaseSharedPreferences

/**
 * Created by Septenary on 2018/12/31.
 */
internal object ProfileSP : BaseSharedPreferences("profile", asUserData = true) {
    const val KEY_NOTIFY_ENABLE = "key.setting_notify_enable"
    const val KEY_SOUND_ENABLE = "key.setting_sound_enable"
    const val KEY_VIBRATE_ENABLE = "key.setting_vibrate_enable"
}