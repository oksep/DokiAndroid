package com.dokiwa.dokidoki.profile.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.children
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.api.LocalAssetApi
import com.dokiwa.dokidoki.profile.api.TagsGroupModel
import kotlinx.android.synthetic.main.activity_profile_edit_tags.*
import kotlinx.android.synthetic.main.view_profile_edit_tags_head.*
import kotlinx.android.synthetic.main.view_profile_tags_group.view.*

private const val TAG = "TagsEditActivity"

class TagsEditActivity : TranslucentActivity() {

    companion object {

        const val EXTRA_TAGS = "extra.user.tags"

        const val REQUEST_CODE = 0x0002

        fun launch(context: Activity, tags: List<String>?) {
            context.startActivityForResult(
                Intent(context, TagsEditActivity::class.java).putExtra(EXTRA_TAGS, tags?.toTypedArray()), REQUEST_CODE
            )
        }
    }

    private val selectedTags by lazy {
        mutableListOf<String>().also { list ->
            intent.getStringArrayExtra(EXTRA_TAGS)?.let {
                list.addAll(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_tags)

        toolBar.rightTextView.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_TAGS, selectedTags.toTypedArray()))
            finish()
        }

        resetSelectTagView()

        loadData()
    }

    private fun loadData() {
        Api.getLocalAsset(this, LocalAssetApi::class.java).getTagsConfig().subscribeApi(this, {
            Log.d(TAG, "result $it")
            initTagsList(it)
        }, {
            Log.e(TAG, "failed", it)
        })
    }

    private fun initTagsList(model: TagsGroupModel) {
        model.list?.forEach {
            if (it.list?.isNotEmpty() == true) {
                val view =
                    LayoutInflater.from(this).inflate(R.layout.view_profile_tags_group, configTagsListView, false)
                view.tagsTitle.text = it.name
                view.tagsView.setTags(it.list.map { l -> l.name }) { tag, view ->
                    val isAdd = !view.isSelected
                    if (isAdd) {
                        val added = addTag(tag)
                        view.isSelected = added
                    } else {
                        removeTag(tag)
                        view.isSelected = false
                    }
                }
                configTagsListView.addView(view)
            }
        }
    }

    private fun addTag(tag: String): Boolean {
        if (selectedTags.size >= 6) {
            toast(R.string.profile_edit_tags_max_tip)
            return false
        }
        if (!selectedTags.contains(tag)) {
            selectedTags.add(tag)
            resetSelectTagView()
        }
        return true
    }

    private fun removeTag(tag: String) {
        if (selectedTags.contains(tag)) {
            selectedTags.remove(tag)
            resetSelectTagView()
        }
    }

    private fun resetSelectTagView() {
        selectTagsView.setTags(selectedTags)
        selectTagsView.onTagsRemoveCallback = {
            selectedTags.remove(it)
            unSelectConfigTagsListViewChild(it)
            resetTagsCount()
        }
        resetTagsCount()
    }

    private fun unSelectConfigTagsListViewChild(tag: String) {
        configTagsListView.children.forEach { child ->
            child.tagsView.children.forEach { tagView ->
                if (tagView.tag == tag) {
                    tagView.isSelected = false
                }
            }
        }
    }

    private fun resetTagsCount() {
        tagsCount.text = getString(R.string.profile_edit_tags_count, selectedTags.size)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }
}
