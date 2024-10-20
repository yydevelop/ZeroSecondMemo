package com.example.zerosecondmemo

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale

class FullListActivity : AppCompatActivity() {

    private lateinit var memoListView: ListView
    private lateinit var backToInputButton: Button
    private lateinit var memoListAdapter: ArrayAdapter<String>
    private val memoList = ArrayList<Memo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_list)

        memoListView = findViewById(R.id.fullMemoListView)
        backToInputButton = findViewById(R.id.backToInputButton)

        // SharedPreferencesからメモリストを取得
        val sharedPreferences = getSharedPreferences("ZeroSecondMemo", MODE_PRIVATE)
        val savedMemos = sharedPreferences.getStringSet("memos", null)

        savedMemos?.let {
            for (memo in it) {
                val parts = memo.split("|")
                if (parts.size == 3) {
                    memoList.add(Memo(parts[0], parts[1], parts[2]))  // タイトル, 本文, 日付
                }
            }
        }

        // メモを日付の降順でソート
        memoList.sortByDescending { SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).parse(it.dateTime) }

        // リストに表示するために文字列のリストに変換
        val memoStrings = memoList.map { "${it.dateTime} - ${it.title}" }

        memoListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, memoStrings)
        memoListView.adapter = memoListAdapter

        // メモをクリックしたときにメイン画面にメモを返す処理
        memoListView.setOnItemClickListener { _, _, position, _ ->
            val selectedMemo = memoList[position]
            val resultIntent = Intent().apply {
                putExtra("title", selectedMemo.title)
                putExtra("content", selectedMemo.content)
                putExtra("index", position)
            }
            setResult(RESULT_OK, resultIntent)
            finish()  // メイン画面に戻る
        }

        // 入力画面に戻るボタン
        backToInputButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
