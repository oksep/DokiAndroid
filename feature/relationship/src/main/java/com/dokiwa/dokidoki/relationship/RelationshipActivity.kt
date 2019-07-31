package com.dokiwa.dokidoki.relationship

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

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
    }
}
