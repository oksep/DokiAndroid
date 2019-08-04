package com.dokiwa.dokidoki.relationship.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dokiwa.dokidoki.relationship.R
import com.dokiwa.dokidoki.ui.view.RefreshRecyclerView
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_relationship.*

class RelationshipActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "RelationshipActivity"
        fun launch(context: Context) {
            context.startActivity(Intent(context, RelationshipActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relationship)
        viewPager.adapter = adapter
    }

    private val adapter by lazy {
        object : BaseQuickAdapter<Unit, BaseViewHolder>(R.layout.fragment_relationship_list, listOf(Unit, Unit)) {
            override fun convert(helper: BaseViewHolder, item: Unit) {
                (helper.itemView as? RefreshRecyclerView)?.setAdapter(followAdapter)
            }
        }
    }
}

internal class FollowingFragment : InnerPageFragment() {
    override fun onGetApiSingle(map: Map<String, String?>): Single<TimelinePage> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

internal class FollowerFragment : InnerPageFragment() {
    override fun onGetApiSingle(map: Map<String, String?>): Single<TimelinePage> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
