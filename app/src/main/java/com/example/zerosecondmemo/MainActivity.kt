package com.example.zerosecondmemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var viewAllButton: Button
    private val memoList = ArrayList<Memo>()
    private var editingIndex: Int? = null  // 編集中のメモのインデックス

    // ActivityResultLauncherの定義
    private lateinit var memoLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        saveButton = findViewById(R.id.saveButton)
        viewAllButton = findViewById(R.id.viewAllButton)

        // SharedPreferencesからメモを読み込む
        val sharedPreferences = getSharedPreferences("ZeroSecondMemo", Context.MODE_PRIVATE)
        val savedMemos = sharedPreferences.getStringSet("memos", null)

        savedMemos?.let {
            for (memo in it) {
                val parts = memo.split("|")
                if (parts.size == 3) {
                    memoList.add(Memo(parts[0], parts[1], parts[2]))
                }
            }
        }

        // ActivityResultLauncherを登録
        memoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val title = data?.getStringExtra("title")
                val content = data?.getStringExtra("content")
                val index = data?.getIntExtra("index", -1)

                if (title != null && content != null && index != -1) {
                    titleEditText.setText(title)
                    contentEditText.setText(content)
                    editingIndex = index  // 編集モードにする
                }
            }
        }

        // 全メモ表示画面に遷移
        viewAllButton.setOnClickListener {
            val intent = Intent(this, FullListActivity::class.java)
            memoLauncher.launch(intent)  // 新しいAPIで起動
        }

        // メモを保存または編集
        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()
            val currentDateTime = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(Date())

            if (title.isNotEmpty() && content.isNotEmpty()) {
                if (editingIndex != null) {
                    // 既存メモを更新
                    memoList[editingIndex!!] = Memo(title, content, memoList[editingIndex!!].dateTime)
                    editingIndex = null  // 編集終了
                } else {
                    // 新規メモを追加
                    val memo = Memo(title, content, currentDateTime)
                    memoList.add(memo)
                }

                // SharedPreferencesに保存
                val editor = sharedPreferences.edit()
                val memoStrings = memoList.map { "${it.title}|${it.content}|${it.dateTime}" }.toSet()
                editor.putStringSet("memos", memoStrings)
                editor.apply()

                // 入力フィールドをクリア
                titleEditText.text.clear()
                contentEditText.text.clear()
            }
        }
    }
}
