package com.example.zerosecondmemo

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class FullListActivity : AppCompatActivity() {

    private lateinit var memoListView: ListView
    private lateinit var memoListAdapter: ArrayAdapter<Memo>
    private val memoList = ArrayList<Memo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_list)

        memoListView = findViewById(R.id.fullMemoListView)

        // メモリストを取得 (例: インテントから受け取るか、SharedPreferencesから取得)
        val sharedPreferences = getSharedPreferences("ZeroSecondMemo", MODE_PRIVATE)
        val savedMemos = sharedPreferences.getStringSet("memos", null)

        savedMemos?.let {
            for (memo in it) {
                val parts = memo.split("|")
                if (parts.size == 3) {
                    memoList.add(Memo(parts[0], parts[1], parts[2]))
                }
            }
        }

        memoListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, memoList)
        memoListView.adapter = memoListAdapter

        // メモをクリックしたときにメイン画面にメモを返す処理
        memoListView.setOnItemClickListener { _, _, position, _ ->
            val selectedMemo = memoList[position]
            val resultIntent = Intent().apply {
                putExtra("title", selectedMemo.title)
                putExtra("content", selectedMemo.content)
                putExtra("index", position)  // インデックスも返す
            }
            setResult(RESULT_OK, resultIntent)
            finish()  // メイン画面に戻る
        }
    }
}
