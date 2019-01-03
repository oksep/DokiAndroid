package com.dokiwa.dokidoki.social.helper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dokiwa.dokidoki.social.Social
import com.dokiwa.dokidoki.social.auth.impl.ACTION_WECHAT_AUTH
import com.dokiwa.dokidoki.social.auth.impl.EXTRA_AUTH_CODE
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler


/**
 * Created by Septenary on 2019/1/3.
 */
class WechatEventActivity : Activity(), IWXAPIEventHandler {

    private lateinit var api: IWXAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Social.getWechatApi(this).handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        Social.getWechatApi(this).handleIntent(intent, this)
    }

    override fun onResp(resp: BaseResp?) {
        when (resp?.type) {
            ConstantsAPI.COMMAND_SENDAUTH -> { // 认证
                if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                    val code = (resp as? SendAuth.Resp)?.code
                    LocalBroadcastManager.getInstance(this).sendBroadcast(
                        Intent(ACTION_WECHAT_AUTH).putExtra(EXTRA_AUTH_CODE, code)
                    )
                } else {
                    LocalBroadcastManager.getInstance(this).sendBroadcast(
                        Intent(ACTION_WECHAT_AUTH).putExtra(EXTRA_AUTH_CODE, "")
                    )
                }
            }
            ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX -> { // 分享
                resp.errCode
                // TODO: 2019/1/4 @Septenary notify result
            }

            ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM -> { // 小程序
                val extraData = (resp as? WXLaunchMiniProgram.Resp)?.extMsg
                // TODO: 2019/1/4 @Septenary notify result
            }
        }
    }

    override fun onReq(req: BaseReq?) {
    }
}