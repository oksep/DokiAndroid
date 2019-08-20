package com.dokiwa.dokidoki.timeline.comment

import android.view.View
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import com.dokiwa.dokidoki.center.ext.glideAvatar
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.center.util.toReadable
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.timeline.api.Timeline
import com.dokiwa.dokidoki.timeline.api.TimelineComment
import com.dokiwa.dokidoki.timeline.home.TimelineAdapter
import com.dokiwa.dokidoki.ui.span.HtmlSpan
import kotlinx.android.synthetic.main.view_item_timeline_comment.view.*
import kotlinx.android.synthetic.main.view_item_timeline_comment_sort.view.*

internal class TimelineCommentAdapter(
    cdc: CompositeDisposableContext,
    private val onMoreBtnClick: (TimelineComment) -> Unit
) : TimelineAdapter(cdc) {

    companion object {
        private const val TYPE_COMMENT_SORT = 10
        private const val TYPE_COMMENT_EMPTY = 11
        private const val TYPE_COMMENT = 12
    }

    init {
        addItemType(TYPE_COMMENT_SORT, R.layout.view_item_timeline_comment_sort)
        addItemType(TYPE_COMMENT_EMPTY, R.layout.view_item_timeline_comment_empty)
        addItemType(TYPE_COMMENT, R.layout.view_item_timeline_comment)
    }

    override fun convert(helper: BaseViewHolder, item: MultiItemEntity) {
        when (item) {
            is HeadEntity -> {
                super.convert(helper, item)
                helper.itemView.moreBtn.visibility = View.GONE
                helper.itemView.background = null
            }
            is SortEntity -> {
                if (item.commentCount == 0) {
                    helper.itemView.commentCount.setText(R.string.timeline_comment_all_comment)
                    helper.itemView.sortByBtn.visibility = View.GONE
                } else {
                    helper.itemView.commentCount.text = helper.itemView.resources.getString(
                        R.string.timeline_comment_all_comment_with_count, item.commentCount
                    )
                    helper.itemView.sortByBtn.visibility = View.VISIBLE
                }

                val isDesc = item.sortBy == "desc"
                helper.itemView.sortByBtn.isSelected = isDesc
                helper.itemView.sortByBtn.setOnClickListener {
                    this.sortBy = if (isDesc) "asc" else "desc"
                    helper.itemView.sortByBtn.isSelected = !isDesc
                    resortComments()
                }
            }
            is CommentEntity -> {
                val user = item.comment.user
                helper.itemView.avatar.glideAvatar(user.avatar)
                helper.itemView.name.text = user.nickname
                helper.itemView.time.text = item.comment.createTime.toReadable()
                if (item.comment.replyToUser != null) {
                    helper.itemView.content.text = HtmlSpan.fromHtml(
                        helper.itemView.resources.getString(
                            R.string.timeline_comment_content_reply,
                            item.comment.replyToUser.nickname,
                            item.comment.content
                        )
                    )
                } else {
                    helper.itemView.content.text = item.comment.content
                }
                helper.itemView.name.setOnClickListener {
                    IProfilePlugin.get().launchProfileActivity(helper.itemView.context, user.uuid)
                }
                helper.itemView.avatar.setOnClickListener {
                    IProfilePlugin.get().launchProfileActivity(helper.itemView.context, user.uuid)
                }
                helper.itemView.moreBtn.setOnClickListener {
                    onMoreBtnClick(item.comment)
                }
            }
            is EmptyEntity -> {
                // noop
            }
        }
        super.convert(helper, item)
    }

    private fun resortComments() {
        if (timeline != null) {
            setData(timeline!!, commentList?.reversed())
        }
    }

    private var timeline: Timeline? = null

    private var commentList: List<TimelineComment>? = null

    var sortBy = "desc" //  "desc" | "asc"

    fun setData(timeline: Timeline, list: List<TimelineComment>?) {
        this.timeline = timeline
        this.commentList = list
        mutableListOf<MultiItemEntity>().apply {
            add(HeadEntity(timeline))
            add(SortEntity(list?.size ?: 0, sortBy))
            if (list.isNullOrEmpty()) {
                add(EmptyEntity())
            } else {
                addAll(list.map { CommentEntity(it) })
            }
        }.also {
            setNewData(it)
        }
    }

    fun addComment(comment: TimelineComment) {
        if (timeline != null) {
            val commentList = commentList?.toMutableList() ?: mutableListOf()
            if (sortBy == "desc") {
                commentList.add(0, comment)
            } else {
                commentList.add(comment)
            }
            setData(timeline!!, commentList)
        }
    }

    class HeadEntity(timeline: Timeline) : TimelineAdapter.TimelineEntity(timeline)

    class SortEntity(val commentCount: Int, val sortBy: String) : MultiItemEntity {
        override fun getItemType() = TYPE_COMMENT_SORT
    }

    class EmptyEntity : MultiItemEntity {
        override fun getItemType() = TYPE_COMMENT_EMPTY
    }

    class CommentEntity(val comment: TimelineComment) : MultiItemEntity {
        override fun getItemType() = TYPE_COMMENT
    }
}