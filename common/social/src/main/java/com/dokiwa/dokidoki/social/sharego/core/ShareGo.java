package com.dokiwa.dokidoki.social.sharego.core;

import android.app.Activity;
import android.content.Intent;

import com.dokiwa.dokidoki.social.sharego.core.callback.SocialLaunchCallback;
import com.dokiwa.dokidoki.social.sharego.core.callback.SocialShareCallback;
import com.dokiwa.dokidoki.social.sharego.core.entities.ShareEntity;
import com.dokiwa.dokidoki.social.sharego.core.entities.WXLaunchEntity;
import com.dokiwa.dokidoki.social.socialgo.config.SocialConfig;
import com.dokiwa.dokidoki.social.socialgo.core.ISocial;
import com.dokiwa.dokidoki.social.socialgo.core.SocialGo;
import com.dokiwa.dokidoki.social.socialgo.core.callback.SocialCallback;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;

/**
 * Create by gentriolee
 */

public class ShareGo implements ISocial {

    private static volatile ShareGo sInstance;

    private SocialConfig.Builder builder;

    private WXShare wxShare;
    private QQShare qqShare;
    private WBShare wbShare;
    private DDShare ddShare;

    //隐藏构造方法
    private ShareGo() {
        builder = SocialConfig.getInstance().getBuilder();
    }

    public static ShareGo getInstance() {
        if (sInstance == null) {
            synchronized (ShareGo.class) {
                if (sInstance == null) {
                    sInstance = new ShareGo();
                }
            }
        }
        if (SocialGo.shareGo == null) {
            SocialGo.shareGo = sInstance;
        }
        return sInstance;
    }

    //<editor-fold desc="分享方式">

    /**
     * 根据ShareEntity的target来执行不同分享
     * @param activity
     * @param shareInfo
     * @param callback
     */
    public void share(Activity activity, ShareEntity shareInfo, SocialShareCallback callback) {
        if (shareInfo == null) {
            return;
        }
        switch (shareInfo.getTarget()) {
            case ISocial.TARGET_WX:
            case ISocial.TARGET_WX_TIMELINE:
                shareWX(activity, shareInfo, callback);
                break;
            case ISocial.TARGET_QQ:
            case ISocial.TARGET_QQ_ZONE:
                shareQQ(activity, shareInfo, callback);
                break;
            case ISocial.TARGET_WB:
                shareWB(activity, shareInfo, callback);
                break;
            case ISocial.TARGET_DD:
                shareDD(activity, shareInfo, callback);
                break;
        }
    }

    /**
     * 微信分享
     * @param activity
     * @param shareInfo
     * @param callback
     */
    public void shareWX(Activity activity, ShareEntity shareInfo, SocialShareCallback callback) {
        resetCallbackTarget(shareInfo, callback);
        wxShare = new WXShare(activity, builder.getWxAppId(), builder.getWxSecretId(), callback);
        wxShare.share(shareInfo);
    }

    /**
     * 唤起微信小程序
     * @param activity
     * @param wxLaunchEntity
     * @param callback
     */
    public void launchWX(Activity activity, WXLaunchEntity wxLaunchEntity, SocialLaunchCallback callback) {
        wxShare = new WXShare(activity, builder.getWxAppId(), builder.getWxSecretId(), callback);
        wxShare.launch(wxLaunchEntity);
    }

    /**
     * QQ分享
     * @param activity
     * @param shareInfo
     * @param callback
     */
    public void shareQQ(Activity activity, ShareEntity shareInfo, SocialShareCallback callback) {
        resetCallbackTarget(shareInfo, callback);
        qqShare = new QQShare(activity, builder.getQqAppId(), callback);
        qqShare.share(shareInfo);
    }

    /**
     * 微博分享
     * @param activity
     * @param shareInfo
     * @param callback
     */
    public void shareWB(Activity activity, ShareEntity shareInfo, SocialShareCallback callback) {
        wbShare = new WBShare(activity, builder.getWbAppId(), builder.getWbRedirectUrl(), callback);
        wbShare.share(shareInfo);
    }

    /**
     * 钉钉分享
     * @param activity
     * @param shareInfo
     * @param callback
     */
    public void shareDD(Activity activity, ShareEntity shareInfo, SocialShareCallback callback) {
        ddShare = new DDShare(activity, builder.getDdAppId(), builder.getDdSecretId(), callback);
        ddShare.share(shareInfo);
    }

    /**
     * 微信和QQ存在两个Target 要根据分享类型来重设Target
     * @param shareInfo
     * @param callback
     */
    private void resetCallbackTarget(ShareEntity shareInfo, SocialCallback callback) {
        if (callback != null && shareInfo != null) {
            callback.setTarget(shareInfo.getTarget());
        }
    }

    //</editor-fold>

    //<editor-fold desc="分享需要的回调处理">
    @Override
    public void onWXAPIHandlerReq(BaseReq baseReq) {
        if (wxShare != null) {
            wxShare.onReq(baseReq);
        }
    }

    @Override
    public void onWXAPIHandlerResp(BaseResp baseResp) {
        if (wxShare != null) {
            wxShare.onResp(baseResp);
        }
    }

    @Override
    public void onDDAPIHandlerReq(com.android.dingtalk.share.ddsharemodule.message.BaseReq baseReq) {
        if (ddShare != null) {
            ddShare.onReq(baseReq);
        }
    }

    @Override
    public void onDDAPIHandlerResp(com.android.dingtalk.share.ddsharemodule.message.BaseResp baseResp) {
        if (ddShare != null) {
            ddShare.onResp(baseResp);
        }
    }

    /**
     * qq登录和分享以及微博登录都需要在其当前的activity的onActivityResult中调用该方法
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (qqShare != null) {
            qqShare.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 微博分享需要在其当前的activity中的onNewIntent中调用该方法
     */
    @Override
    public void onNewIntent(Intent intent) {
        if (wbShare != null) {
            wbShare.onNewIntent(intent);
        }
    }
    //</editor-fold>
}
