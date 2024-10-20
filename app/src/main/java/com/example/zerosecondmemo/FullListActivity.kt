package com.example.zerosecondmemo

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class FullListActivity : AppCompatActivity() {

    private lateinit var memoListView: ListView
    private lateinit var memoListAdapter: ArrayAdapter<String>
    private val memoList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_list)

        memoListView = findViewById(R.id.fullMemoListView)

        // SharedPreferencesから全てのメモを取得
        val sharedPreferences = getSharedPreferences("ZeroSecondMemo", Context.MODE_PRIVATE)
        val savedMemos = sharedPreferences.getStringSet("memos", null)

        // 以前のメモを読み込み
        savedMemos?.let {
            memoList.addAll(it)
        }

        // リストアダプターの設定
        memoListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, memoList)
        memoListView.adapter = memoListAdapter
    }
}
