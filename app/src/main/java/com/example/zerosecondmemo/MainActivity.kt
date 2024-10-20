package com.example.zerosecondmemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var viewAllButton: Button
    private lateinit var memoListAdapter: ArrayAdapter<Memo>
    private val memoList = ArrayList<Memo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 各ビューの参照を取得
        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        saveButton = findViewById(R.id.saveButton)
        viewAllButton = findViewById(R.id.viewAllButton)

        // SharedPreferencesのインスタンスを取得
        val sharedPreferences = getSharedPreferences("ZeroSecondMemo", Context.MODE_PRIVATE)
        val savedMemos = sharedPreferences.getStringSet("memos", null)

        // 以前のメモを読み込み
        savedMemos?.let {
            for (memo in it) {
                val parts = memo.split("|")
                if (parts.size == 3) {
                    memoList.add(Memo(parts[0], parts[1], parts[2]))
                }
            }
        }

        // リストアダプターの設定
        memoListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, memoList)

        // 保存ボタンが押されたときの処理
        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()

            // 現在の日時を取得
            val currentDateTime = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(Date())

            // タイトル、本文、日時をリストに登録
            if (title.isNotEmpty() && content.isNotEmpty()) {
                val memo = Memo(title, content, currentDateTime)
                memoList.add(memo)

                // SharedPreferencesに保存
                val editor = sharedPreferences.edit()
                val memoStrings = memoList.map { "${it.title}|${it.content}|${it.dateTime}" }.toSet()
                editor.putStringSet("memos", memoStrings)
                editor.apply()

                // 入力フィールドをクリア
                titleEditText.text.clear()
                contentEditText.text.clear()

                // リストを更新
                memoListAdapter.notifyDataSetChanged()
            }
        }

        // 全メモを表示する画面に遷移
        viewAllButton.setOnClickListener {
            val intent = Intent(this, FullListActivity::class.java)
            startActivity(intent)
        }
    }
}
