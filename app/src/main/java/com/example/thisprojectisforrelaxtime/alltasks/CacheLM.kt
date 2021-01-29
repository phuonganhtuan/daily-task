package com.example.thisprojectisforrelaxtime.alltasks

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CacheLM(context: Context?) : LinearLayoutManager(context) {
    override fun getExtraLayoutSpace(state: RecyclerView.State?) = 5000
}
