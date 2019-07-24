package com.dokiwa.dokidoki.message.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.dokiwa.dokidoki.message.R
import kotlinx.android.synthetic.main.merge_input_panel_emoticon.view.*

/**
 * Created by Septenary on 2019-07-24.
 */
class InputEmoticonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.merge_input_panel_emoticon, this)
        orientation = VERTICAL
        setUp()
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (isShown && viewPager.adapter == null) {
            setUp()
        }
    }

    private fun setUp() {
        val list = TabData.getData()
        val group = list.groupBy { it.count / 10 }
        viewPager.adapter = EmoticonAdapter().apply {
            group.forEach {
                // addData(StickerEntity(it.first * 10, ))
            }
            registerAdapterDataObserver(indicator.adapterDataObserver)
        }
        indicator.setViewPager(viewPager)
    }
}

internal data class TabData(
    val normalIcon: String,
    val highlightIcon: String,
    val count: Int,
    val pathGen: (Int) -> Pair<Int, String>
) {
    companion object {
        fun getData(): List<TabData> {
            return listOf(
                TabData(
                    "guaiqiaogif/icon/guaiqiao_s_normal@2x.png",
                    "guaiqiaogif/icon/guaiqiao_s_highlighted@2x.png",
                    18
                ) { index ->
                    Pair(index, "guaiqiaogif/content/guaiqiaogif0${String.format("%02d", index)}@2x.gif")
                },
                TabData(
                    "monigif/icon/moni_s_normal@2x.png",
                    "monigif/icon/moni_s_highlighted@2x.png",
                    18
                ) { index ->
                    Pair(index, "monigif/content/monigif0${String.format("%02d", index)}@2x.gif")
                }
            )
        }
    }
}

internal class EmoticonAdapter : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(null) {

    companion object {
        const val TYPE_EMOJI = 0
        const val TYPE_STICKER = 1
    }

    init {
        addItemType(TYPE_EMOJI, R.layout.view_item_emoticon_emoji)
        addItemType(TYPE_STICKER, R.layout.view_item_emoticon_sticker)
    }

    override fun convert(helper: BaseViewHolder, item: MultiItemEntity) {
        (helper.itemView as? GridDrawableView)?.setUp(listOf())
    }
}

private class EmojiEntity(list: List<String>) : MultiItemEntity {
    override fun getItemType() = EmoticonAdapter.TYPE_EMOJI
}

private class StickerEntity(list: List<String>) : MultiItemEntity {
    override fun getItemType() = EmoticonAdapter.TYPE_STICKER
}