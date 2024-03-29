package com.dokiwa.dokidoki.social.sharego.core;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.android.dingtalk.share.ddsharemodule.IDDAPIEventHandler;
import com.android.dingtalk.share.ddsharemodule.ShareConstant;
import com.android.dingtalk.share.ddsharemodule.message.BaseReq;
import com.android.dingtalk.share.ddsharemodule.message.BaseResp;
import com.android.dingtalk.share.ddsharemodule.message.DDImageMessage;
import com.android.dingtalk.share.ddsharemodule.message.DDMediaMessage;
import com.android.dingtalk.share.ddsharemodule.message.DDTextMessage;
import com.android.dingtalk.share.ddsharemodule.message.DDWebpageMessage;
import com.android.dingtalk.share.ddsharemodule.message.SendMessageToDD;
import com.dokiwa.dokidoki.social.R;
import com.dokiwa.dokidoki.social.sharego.core.callback.SocialShareCallback;
import com.dokiwa.dokidoki.social.sharego.core.entities.DDShareEntity;
import com.dokiwa.dokidoki.social.sharego.core.entities.ShareEntity;
import com.dokiwa.dokidoki.social.sharego.utils.ShareUtils;
import com.dokiwa.dokidoki.social.socialgo.core.DDSocial;
import com.dokiwa.dokidoki.social.socialgo.core.callback.SocialCallback;

/**
 * Created by Septenary
 */

public class DDShare extends DDSocial implements IShare, IDDAPIEventHandler {

    DDShare(Activity activity, String appId, String secretId, SocialCallback callback) {
        super(activity, appId, secretId, callback);
    }

    @Override
    public void share(ShareEntity shareInfo) {
        if (unInitInterrupt()) {
            return;
        }

        if (!iddShareApi.isDDSupportAPI()) {
            if (socialCallback != null) {
                socialCallback.fail(ErrCode.ERR_LOW_VERSION, getString(R.string.share_dd_version_low_error));
            }
            return;
        }

        SendMessageToDD.Req req = new SendMessageToDD.Req();
        req.mMediaMessage = createMessage(req, shareInfo.getParams());
        if (req.mMediaMessage == null) {
            return;
        }
        iddShareApi.sendReq(req);
    }

    private DDMediaMessage createMessage(SendMessageToDD.Req req, Bundle params) {
        DDMediaMessage msg = new DDMediaMessage();
        int type = params.getInt(DDShareEntity.SHARE_TYPE);
        boolean success = false;
        switch (type) {
            case DDShareEntity.SHARE_TYPE_TEXT:
                success = addText(req, msg, params);
                break;
            case DDShareEntity.SHARE_TYPE_IMG:
                success = addImage(req, msg, params);
                break;
            case DDShareEntity.SHARE_TYPE_WEB:
                success = addWeb(req, msg, params);
                break;
        }
        if (!success) {
            return null;
        }
        return msg;
    }

    private boolean addText(SendMessageToDD.Req req, DDMediaMessage msg, Bundle params) {
        DDTextMessage textObj = new DDTextMessage();
        textObj.mText = params.getString(DDShareEntity.SHARE_TITLE);

        msg.mMediaObject = textObj;
        return true;
    }

    private boolean addImage(SendMessageToDD.Req req, DDMediaMessage msg, Bundle params) {
        if (params.containsKey(DDShareEntity.SHARE_IMAGE_BITMAP)) {
            Bitmap bitmap = params.getParcelable(DDShareEntity.SHARE_IMAGE_BITMAP);
            msg.mMediaObject = new DDImageMessage(bitmap);
            msg.mThumbData = ShareUtils.smallBmpToByteArray(bitmap);
            return true;
        }
        return false;
    }

    private boolean addWeb(SendMessageToDD.Req req, DDMediaMessage msg, Bundle params) {
        DDWebpageMessage webObj = new DDWebpageMessage();
        webObj.mUrl = params.getString(DDShareEntity.SHARE_LINK);

        msg.mMediaObject = webObj;
        if (addTitleSummaryAndThumb(msg, params)) return false;

        return true;
    }

    private boolean addTitleSummaryAndThumb(DDMediaMessage msg, Bundle params) {
        if (params.containsKey(DDShareEntity.SHARE_TITLE)) {
            msg.mTitle = params.getString(DDShareEntity.SHARE_TITLE);
        }

        if (params.containsKey(DDShareEntity.SHARE_DESC)) {
            msg.mContent = params.getString(DDShareEntity.SHARE_DESC);
        }

        if (params.containsKey(DDShareEntity.SHARE_IMAGE_BITMAP)) {
            Bitmap bitmap = params.getParcelable(DDShareEntity.SHARE_IMAGE_BITMAP);
            msg.mThumbData = ShareUtils.smallBmpToByteArray(bitmap);
        }
        return false;
    }

    @Override
    public void onReq(BaseReq baseReq) {
        //nothing
    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.getType() == ShareConstant.COMMAND_SENDAUTH) {
            if (baseResp.mErrCode == BaseResp.ErrCode.ERR_OK) {
                if (socialCallback instanceof SocialShareCallback) {
                    ((SocialShareCallback) socialCallback).success();
                }
            } else {
                if (socialCallback != null) {
                    socialCallback.cancel();
                }
            }
        }
    }

}
