package com.example.zerosecondmemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var memoListView: ListView
    private lateinit var viewAllButton: Button
    private lateinit var memoListAdapter: ArrayAdapter<String>
    private val memoList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 各ビューの参照を取得
        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        saveButton = findViewById(R.id.saveButton)
        memoListView = findViewById(R.id.memoListView)
        viewAllButton = findViewById(R.id.viewAllButton)

        // SharedPreferencesのインスタンスを取得
        val sharedPreferences = getSharedPreferences("ZeroSecondMemo", Context.MODE_PRIVATE)
        val savedMemos = sharedPreferences.getStringSet("memos", null)

        // 以前のメモを読み込み
        savedMemos?.let {
            memoList.addAll(it)
        }

        // 直近10件のメモのみを表示
        val recentMemos = if (memoList.size > 10) memoList.takeLast(10) else memoList

        // リストアダプターの設定
        memoListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, recentMemos)
        memoListView.adapter = memoListAdapter

        // 保存ボタンが押されたときの処理
        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()

            // タイトルと本文をリストに登録
            if (title.isNotEmpty() && content.isNotEmpty()) {
                val memo = "$title: $content"
                memoList.add(memo)
                memoListAdapter.notifyDataSetChanged()

                // SharedPreferencesに保存
                val editor = sharedPreferences.edit()
                editor.putStringSet("memos", memoList.toSet())
                editor.apply()

                // 入力フィールドをクリア
                titleEditText.text.clear()
                contentEditText.text.clear()

                // リストを更新
                memoListAdapter.clear()
                val updatedRecentMemos = if (memoList.size > 10) memoList.takeLast(10) else memoList
                memoListAdapter.addAll(updatedRecentMemos)
            }
        }

        // 全メモを表示する画面に遷移
        viewAllButton.setOnClickListener {
            val intent = Intent(this, FullListActivity::class.java)
            startActivity(intent)
        }
    }
}
