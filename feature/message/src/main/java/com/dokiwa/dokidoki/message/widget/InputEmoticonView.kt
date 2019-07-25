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
        viewPager.adapter = EmoticonAdapter().apply {
            addData(emoji.items.map { EmojiEntity(it.value) })
            addData(guaiqiao.items.map { StickerEntity(it.value) })
            addData(moni.items.map { StickerEntity(it.value) })
            registerAdapterDataObserver(indicator.adapterDataObserver)
        }
        indicator.setViewPager(viewPager)
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
        when (item) {
            is EmojiEntity -> {
                (helper.itemView as? GridDrawableView)?.setUp(item.list)
            }
            is StickerEntity -> {
                (helper.itemView as? GridDrawableView)?.setUp(item.list)
            }
        }
    }
}

private class EmojiEntity(val list: List<String>) : MultiItemEntity {
    override fun getItemType() = EmoticonAdapter.TYPE_EMOJI
}

private class StickerEntity(val list: List<String>) : MultiItemEntity {
    override fun getItemType() = EmoticonAdapter.TYPE_STICKER
}