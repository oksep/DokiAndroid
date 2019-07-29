package com.dokiwa.dokidoki.message.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import androidx.viewpager2.widget.ViewPager2
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.message.widget.emoction.GridDrawableView
import com.dokiwa.dokidoki.message.widget.emoction.emoji
import com.dokiwa.dokidoki.message.widget.emoction.guaiqiao
import com.dokiwa.dokidoki.message.widget.emoction.moni
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
    }

    fun setUp(emojiClick: (String) -> Unit, stickerClick: (String) -> Unit) {
        if (viewPager.adapter != null) return

        emoticonTabContainer.children.forEachIndexed { index, view ->
            view.setOnClickListener {
                when (index) {
                    0 -> viewPager.setCurrentItem(0, false)
                    1 -> viewPager.setCurrentItem(emoji.items.size, false)
                    2 -> viewPager.setCurrentItem(emoji.items.size + guaiqiao.items.size, false)
                }
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when {
                    position < emoji.items.size -> highlightTab(0)
                    position < emoji.items.size + guaiqiao.items.size -> highlightTab(1)
                    else -> highlightTab(2)
                }
            }
        })

        viewPager.adapter = EmoticonAdapter({
            emojiClick.invoke(it.tag)
        }, {
            stickerClick.invoke(it.path)
        }).apply {
            addData(emoji.items.map { EmojiEntity(it.value) })
            addData(guaiqiao.items.map { StickerEntity(it.value) })
            addData(moni.items.map { StickerEntity(it.value) })
            registerAdapterDataObserver(indicator.adapterDataObserver)
        }
        indicator.setViewPager(viewPager)
    }

    private fun highlightTab(position: Int) {
        emoticonTabContainer.forEachIndexed { index, view ->
            view.isSelected = index == position
        }
    }
}

internal class EmoticonAdapter(
    private val emojiClick: (GridDrawableView.GridDrawableItemData) -> Unit,
    private val stickerClick: (GridDrawableView.GridDrawableItemData) -> Unit
) : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(null) {

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
                (helper.itemView as? GridDrawableView)?.setUp(item.list, emojiClick)
            }
            is StickerEntity -> {
                (helper.itemView as? GridDrawableView)?.setUp(item.list, stickerClick)
            }
        }
    }
}

private class EmojiEntity(val list: List<GridDrawableView.GridDrawableItemData>) : MultiItemEntity {
    override fun getItemType() = EmoticonAdapter.TYPE_EMOJI
}

private class StickerEntity(val list: List<GridDrawableView.GridDrawableItemData>) : MultiItemEntity {
    override fun getItemType() = EmoticonAdapter.TYPE_STICKER
}