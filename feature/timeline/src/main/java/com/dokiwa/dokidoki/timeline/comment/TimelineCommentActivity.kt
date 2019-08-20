package com.dokiwa.dokidoki.timeline.comment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.rx.subscribeApiWithDialog
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.ext.toastApiException
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.timeline.Log
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.timeline.api.Timeline
import com.dokiwa.dokidoki.timeline.api.TimelineApi
import com.dokiwa.dokidoki.timeline.api.TimelineComment
import com.dokiwa.dokidoki.timeline.api.TimelineUser
import com.dokiwa.dokidoki.ui.ext.setRefreshListenerHaptic
import com.dokiwa.dokidoki.ui.ext.showKeyboard
import com.dokiwa.dokidoki.ui.util.KeyboardHeightObserver
import com.dokiwa.dokidoki.ui.util.KeyboardHeightProvider
import com.dokiwa.dokidoki.ui.util.SimpleTextWatcher
import com.dokiwa.dokidoki.ui.view.LoadMoreView
import kotlinx.android.synthetic.main.activity_timeline_comment.*
import kotlinx.android.synthetic.main.view_timeline_comment_edit.*

private const val TAG = "TimelineCommentActivity"

class TimelineCommentActivity : TranslucentActivity(), KeyboardHeightObserver {

    companion object {
        private const val EXTRA_TIMELINE = "extra.timeline"
        private const val EXTRA_ID = "extra.id"

        fun launch(context: Context, data: Timeline) {
            context.startActivity(
                Intent(context, TimelineCommentActivity::class.java).putExtra(EXTRA_TIMELINE, data)
            )
        }

        fun launch(context: Context, id: String) {
            context.startActivity(
                Intent(context, TimelineCommentActivity::class.java).putExtra(EXTRA_ID, id)
            )
        }
    }

    private val adapter by lazy { TimelineCommentAdapter(this, ::onMoreClick) }

    private lateinit var timeline: Timeline

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        changeStatusBarDark()

        setContentView(R.layout.activity_timeline_comment)

        KeyboardHeightProvider(this).attach(this)

        loadTimelineData()
    }

    private fun loadTimelineData() {
        val timeline: Timeline? = intent.getParcelableExtra(EXTRA_TIMELINE)
        val id: String? = intent.getStringExtra(EXTRA_ID) ?: intent.data?.getQueryParameter("ufeed_id")

        if (timeline != null) {
            this.timeline = timeline
            initView()
            refresh()
        } else if (!id.isNullOrEmpty()) {
            Api.get(TimelineApi::class.java).getTimeline(id).subscribeApi(this, {
                this.timeline = it.ufeed
                initView()
                refresh()
            }, {
                toastApiException(it, R.string.load_failed)
            })
        } else {
            return finish()
        }
    }

    private fun initView() {
        hideEdit()

        toolBar.rightIconView.setOnClickListener {
            toast("举报、删除动态")
        }

        val refreshLayout = refreshRecyclerView.getRefreshLayout()
        refreshLayout.setColorSchemeResources(R.color.dd_red)
        refreshLayout.setRefreshListenerHaptic {
            refresh()
        }

        val recyclerView = refreshRecyclerView.getRecyclerView()
        recyclerView.setBackgroundColor(Color.WHITE)
        recyclerView.addItemDecoration(LineDivider(this))
        adapter.setLoadMoreView(LoadMoreView())
        adapter.setEnableLoadMore(false)
        adapter.setOnItemClickListener { adapter, _, position ->
            when (val entity = adapter.getItem(position)) {
                is TimelineCommentAdapter.HeadEntity -> {
                    recyclerView.postDelayed({
                        recyclerView.smoothScrollToPosition(position)
                    }, 150)
                    setEditReplyTo(null)
                }
                is TimelineCommentAdapter.CommentEntity -> {
                    recyclerView.postDelayed({
                        recyclerView.smoothScrollToPosition(position)
                    }, 150)
                    setEditReplyTo(entity.comment.user)
                }
            }
        }
        recyclerView.adapter = adapter
    }

    private fun ensureEditAble(userId: Int): Boolean {
        return ILoginPlugin.get().getLoginUserId() == userId
    }

    private fun refresh() {
        loadCommentData()
    }

    private fun loadCommentData() {

        refreshRecyclerView.showLoading()

        Api.get(TimelineApi::class.java)
            .getTimelineComment(timeline.id, adapter.sortBy)
            .subscribeApi(this, {
                Log.d(TAG, "timeline comments: $it")
                setData(timeline, it.commentList)
            }, {
                Log.e(TAG, "get timeline comment failed", it)
                setData(timeline, null)
            })
    }

    private fun submitReply(content: String) {
        val replyToUser = editText.tag as? TimelineUser
        Api.get(TimelineApi::class.java)
            .createComment(timeline.id, replyToUser?.id, content)
            .subscribeApiWithDialog(this, this,
                {
                    editText.text = null
                    setEditReplyTo(null)
                    adapter.addComment(it.comment)
                },
                {
                    toastApiException(it, R.string.timeline_comment_reply_failed)
                }
            )
    }

    private fun setData(timeline: Timeline, list: List<TimelineComment>?) {
        showEdit()
        refreshRecyclerView.showSuccess(true)
        adapter.setData(timeline, list)
    }

    private fun onMoreClick(comment: TimelineComment) {
        toast("回复、举报、删除评论")
    }

    private fun showEdit() {
        editContainer.visibility = View.VISIBLE
        editText.removeTextChangedListener(textChangeListener)
        editText.addTextChangedListener(textChangeListener)
        sendBtn.setOnClickListener {
            submitReply(editText.text.toString())
        }
    }

    private fun hideEdit() {
        editContainer.visibility = View.GONE
    }

    private val textChangeListener = object : SimpleTextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            sendBtn.isEnabled = s?.length ?: 0 > 0
        }
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        val params = editBottomSpace.layoutParams
        if (params.height != height) {
            params.height = height
            TransitionManager.beginDelayedTransition(root, ChangeBounds().setStartDelay(0).setDuration(80))
            editBottomSpace.layoutParams = params
        }
    }

    private fun setEditReplyTo(user: TimelineUser?) {
        editText.tag = user
        editText.hint = if (user != null) {
            getString(R.string.timeline_comment_hint_reply, user.nickname)
        } else {
            getString(R.string.timeline_comment_say_something)
        }
        editText.showKeyboard()
    }
}
