package com.dokiwa.dokidoki.message.widget

import androidx.annotation.Keep
import com.dokiwa.dokidoki.center.AppCenter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by Septenary on 2019-07-25.
 */
data class EmoticonData(
    val normalIcon: String,
    val highlightIcon: String,
    val items: Map<Int, List<String>>
)

@Keep
data class EmojiMeta(val id: String, val file: String, val tag: String)

val emojiMetaList by lazy {
    Gson().fromJson<List<EmojiMeta>>(
        AppCenter.get().context.assets.open("emoji/emoji.json").bufferedReader(),
        object : TypeToken<List<EmojiMeta>>() {}.type
    ).map {
        "emoji/content/${it.file}"
    }
}

val emoji = EmoticonData(
    "emoji/icon/emoji_normal.png",
    "emoji/icon/emoji_highlight.png",
    emojiMetaList.run {
        var i = 0
        groupBy { i++ / 23 }
    }
)

val guaiqiao = EmoticonData(
    "guaiqiao/icon/normal.png",
    "guaiqiao/icon/highlight.png",
    Array(18) {
        "guaiqiao/content/guaiqiao_${String.format("%02d", it + 1)}.gif"
    }.run {
        var i = 0
        groupBy { i++ / 10 }
    }
)

val moni = EmoticonData(
    "moni/icon/normal.png",
    "moni/icon/highlight.png",
    Array(18) {
        "moni/content/moni_${String.format("%02d", it + 1)}.gif"
    }.run {
        var i = 0
        groupBy { i++ / 10 }
    }
)
