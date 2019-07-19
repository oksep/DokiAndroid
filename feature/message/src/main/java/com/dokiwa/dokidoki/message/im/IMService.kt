package com.dokiwa.dokidoki.message.im

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.dokiwa.dokidoki.center.util.toUploadFileObservable
import com.dokiwa.dokidoki.message.Log
import com.netease.nimlib.sdk.*
import com.netease.nimlib.sdk.auth.ClientType
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.auth.OnlineClient
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum
import com.netease.nimlib.sdk.msg.model.RecentContact
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import com.netease.nimlib.sdk.util.NIMUtil
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import java.io.File

/**
 * Created by Septenary on 2019-07-10.
 */
object IMService {

    const val TAG = "IMService"

    //region 初始化 NIM
    fun init(context: Context) {
        NIMClient.init(
            context,
            NIMSP.getNIMLoginInfo(),
            SDKOptions.DEFAULT.apply {
                asyncInitSDK = true
                reducedIM = true
            }
        )

        if (NIMUtil.isMainProcess(context)) {
            registerObservers()
            loginStatusSubject.onNext(IMLoginStatus.UNLOGIN)
        }
    }
    //endregion

    //region 向外提供的状态发射源
    private val loginStatusSubject = BehaviorSubject.create<IMLoginStatus>()

    private val onlineClientSubject = BehaviorSubject.create<IMOnlineClient>()

    fun subscribeLoginStatus(): Observable<IMLoginStatus> = loginStatusSubject

    fun subscribeOnlineClients(): Observable<IMOnlineClient> = onlineClientSubject
    //endregion

    //region 注销、注册监听器
    private fun registerObservers() {
        unRegisterObservers()
        NIMSDK.getAuthServiceObserve().observeOtherClients(clientsObserver, true)
        NIMSDK.getAuthServiceObserve().observeOnlineStatus(loginStatusObserver, true)
    }

    private fun unRegisterObservers() {
        NIMSDK.getAuthServiceObserve().observeOtherClients(clientsObserver, false)
        NIMSDK.getAuthServiceObserve().observeOnlineStatus(loginStatusObserver, false)
    }
    //endregion

    //region 其它客户端在线状态
    private val clientsObserver = Observer<List<OnlineClient>> { onlineClients ->
        val onlineClient = when (onlineClients?.getOrNull(0)?.clientType) {
            ClientType.Windows, ClientType.MAC -> IMOnlineClient.Desktop
            ClientType.Web -> IMOnlineClient.Web
            ClientType.iOS -> IMOnlineClient.iOS
            ClientType.Android -> IMOnlineClient.Android
            else -> IMOnlineClient.None
        }
        onlineClientSubject.onNext(onlineClient)
        Log.d(TAG, "[im] online clients changed -> $onlineClient")
    }
    //endregion

    //region 当前用户登录状态
    private val loginStatusObserver = Observer<StatusCode> { code ->
        val loginStatus = if (code.wontAutoLogin()) {
            IMLoginStatus.KICK_OUT
        } else {
            when (code) {
                StatusCode.NET_BROKEN -> IMLoginStatus.NET_BROKEN
                StatusCode.UNLOGIN -> IMLoginStatus.UNLOGIN
                StatusCode.CONNECTING -> IMLoginStatus.CONNECTING
                StatusCode.LOGINING -> IMLoginStatus.LOGINING
                StatusCode.LOGINED -> IMLoginStatus.LOGINED
                else -> IMLoginStatus.UNKNOW
            }
        }
        loginStatusSubject.onNext(loginStatus)
        Log.d(TAG, "[im] login status changed -> $loginStatus")
    }
    //endregion

    //region 登入、登出 NIM
    fun loginIM() {
        Log.d(TAG, "[im] logining")
        NIMSDK.getAuthService().login(NIMSP.getNIMLoginInfo()).setCallback(object : RequestCallback<LoginInfo> {
            override fun onSuccess(loginInfo: LoginInfo) {
                Log.d(TAG, "[im] login success -> $loginInfo")
                NIMSP.saveNIMLoginInfo(loginInfo)
                registerObservers()
            }

            override fun onFailed(code: Int) {
                Log.w(TAG, "[im] login failed -> $code")
            }

            override fun onException(exception: Throwable?) {
                Log.w(TAG, "[im] login exception", exception)
            }
        })
    }

    fun logoutIM() {
        Log.d(TAG, "[im] logout")
        NIMSP.clearNIMLoginInfo()
        NIMSDK.getAuthService().logout()
        unRegisterObservers()
    }
    //endregion

    //region 最近联系人消息
    fun getRecentMessages(): Single<List<IMRecentMessage>> {
        return Single.create { emitter ->
            val callback = EmitterAdaptRequestCallback<List<RecentContact>, List<IMRecentMessage>>(
                "get recent message",
                emitter
            ) { list ->
                list?.map { it.toRecentMessage() } ?: listOf()
            }
            NIMSDK.getMsgService().queryRecentContacts().setCallback(callback)
        }
    }

    fun subscribeRecentMessages(): Observable<List<IMRecentMessage>> {
        var tmp: Observer<List<RecentContact>>? = null
        return Observable.create<List<IMRecentMessage>> { emitter ->
            tmp = Observer { list ->
                val l = list?.map { it.toRecentMessage() } ?: listOf()
                emitter.onNext(l)
            }
            NIMSDK.getMsgServiceObserve().observeRecentContact(tmp, true)
        }.doOnDispose {
            tmp?.let {
                NIMSDK.getMsgServiceObserve().observeRecentContact(tmp, false)
            }
        }
    }
    //endregion

    //region 联系人信息
    fun subscribeNimUserInfoList(): Observable<List<NimUserInfo>> = nimUserInfoSubject
    //endregion

    //region 聊天对话
    fun attachChatRoomSession(lifecycle: Lifecycle, uuid: String) {
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(value = Lifecycle.Event.ON_RESUME)
            fun onResume() {
                NIMSDK.getMsgService().setChattingAccount(uuid, SessionTypeEnum.P2P)
            }

            @OnLifecycleEvent(value = Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                NIMSDK.getMsgService().setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None)
            }
        })
    }

    fun getChatRoomLocalSessionMessages(
        anchor: IMMessage? = null,
        sessionId: String,
        count: Int = 20
    ): Single<List<IMSessionMessage>> {
        val queryAnchor = anchor ?: MessageBuilder.createEmptyMessage(sessionId, SessionTypeEnum.P2P, 0)
        return Single.create { emitter ->
            val callback = EmitterAdaptRequestCallback<List<IMMessage>, List<IMSessionMessage>>(
                "get local session message",
                emitter
            ) { list ->
                list?.map { it.toSessionMessage() } ?: listOf()
            }
            NIMSDK.getMsgService()
                .queryMessageListEx(queryAnchor, QueryDirectionEnum.QUERY_OLD, count, true)
                .setCallback(callback)
        }
    }

    fun subscribeChatRoomIncomingMessage(): Observable<List<IMSessionMessage>> {
        var tmp: Observer<List<IMMessage>>? = null
        return Observable.create<List<IMSessionMessage>> { emitter ->
            tmp = Observer { list ->
                val l = list?.map { it.toSessionMessage() } ?: listOf()
                emitter.onNext(l)
            }
            NIMSDK.getMsgServiceObserve().observeReceiveMessage(tmp, true)
        }.doOnDispose {
            tmp?.let {
                NIMSDK.getMsgServiceObserve().observeReceiveMessage(tmp, false)
            }
        }
    }

    fun sendMessageTxt(account: String, message: String): Single<IMSessionMessage> {
        return Single.create { emitter ->
            val payload = MessageBuilder.createTextMessage(account, SessionTypeEnum.P2P, message)
            emitter.onSuccess(payload.toSessionMessage())
            NIMSDK.getMsgService()
                .sendMessage(payload, false)
                .setCallback(DummyAdaptRequestCallback("send text message"))
        }
    }

    fun sendMessageImg(context: Context, account: String, list: List<Uri>): Observable<IMSessionMessage> {
        val copyFileTasks = list.map { it.toUploadFileObservable(context) }
        return Observable.merge<String>(copyFileTasks)
            .filter { it.isNotEmpty() }
            .map { Uri.parse(it) }
            .flatMap { uri: Uri ->
                Observable.create<IMSessionMessage> { emitter ->
                    val payload = MessageBuilder.createImageMessage(account, SessionTypeEnum.P2P, uri.toFile())
                    emitter.onNext(payload.toSessionMessage())
                    NIMSDK.getMsgService().sendMessage(payload, false)
                        .setCallback(DummyAdaptRequestCallback("send img message"))
                }
            }
    }

    fun sendMessageAudio(account: String, audioFile: File, audioLength: Long): Single<IMSessionMessage> {
        return Single.create { emitter ->
            val payload = MessageBuilder.createAudioMessage(account, SessionTypeEnum.P2P, audioFile, audioLength)
            emitter.onSuccess(payload.toSessionMessage())
            NIMSDK.getMsgService()
                .sendMessage(payload, false)
                .setCallback(DummyAdaptRequestCallback("send audio message"))
        }
    }

    fun resendTextMessage(message: IMSessionMessage): Single<IMSessionMessage> {
        return Single.create { emitter ->
            val payload = message.rawMsg
            payload.status = MsgStatusEnum.sending
            emitter.onSuccess(message)
            NIMSDK.getMsgService()
                .sendMessage(payload, true)
                .setCallback(DummyAdaptRequestCallback("send text message"))
        }
    }

    fun subscribeMsgStatus(): Observable<IMSessionMessage> {
        var tmp: Observer<IMMessage>? = null
        return Observable.create<IMSessionMessage> { emitter ->
            tmp = Observer { rawMsg ->
                emitter.onNext(rawMsg.toSessionMessage())
            }
            NIMSDK.getMsgServiceObserve().observeMsgStatus(tmp, true)
        }.doOnDispose {
            tmp?.let {
                NIMSDK.getMsgServiceObserve().observeMsgStatus(tmp, false)
            }
        }
    }
    //endregion
}