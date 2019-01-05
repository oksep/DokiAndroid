### :exclamation: Tips
>
> * 微信分享回调 不再返回用户是否分享完成事件，cancel事件和success事件统一为success事件。[微信官方分享功能调整公告](https://mp.weixin.qq.com/cgi-bin/announce?action=getannouncement&announce_id=11526372695t90Dn&version=&lang=zh_CN&scene=21#wechat_redirect)
> * QQ原生SDK不支持纯文本分享，本库通过指定Activity跳转实现，故无分享回调。

### 回调配置

#### 微信的回调在packageName.wxapi.WXEntryActivity

```java
@Override
public void onReq(BaseReq baseReq) {
    SocialGo.onWXAPIHandlerReq(baseReq);
}

@Override
public void onResp(BaseResp baseResp) {
    SocialGo.onWXAPIHandlerResp(baseResp);
    finish();
}
```

#### QQ回调在调用的Activity中重写onActivityResult()

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    SocialGo.onActivityResult(requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);
}
```

#### 微博回调在调用的Activity中重写onActivityResult()和onNewIntent()

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    SocialGo.onActivityResult(requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);
}

@Override
protected void onNewIntent(Intent intent) {
    SocialGo.onNewIntent(intent);
    super.onNewIntent(intent);
}
```

## 调用

### 分享

```java
ShareGo.getInstance().share();
ShareGo.getInstance().shareWX()；
ShareGo.getInstance().shareQQ();
ShareGo.getInstance().shareWB();
ShareGo.getInstance().shareDD();

ShareGo.getInstance().launchWX();
```
### 授权

```java
AuthGo.getInstance().auth();
AuthGo.getInstance().authWX();
AuthGo.getInstance().authQQ();
AuthGo.getInstance().authWB();
AuthGo.getInstance().authDD();
```

### 登录
```java
AuthGo.getInstance().login();
AuthGo.getInstance().loginWX();
AuthGo.getInstance().loginQQ();
AuthGo.getInstance().loginWB();
AuthGo.getInstance().loginDD();
```