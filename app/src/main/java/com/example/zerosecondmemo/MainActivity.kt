package com.example.zerosecondmemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var viewAllButton: Button
    private lateinit var countdownTextView: TextView
    private lateinit var memoCountTextView: TextView  // 今日のメモ数表示用
    private lateinit var memoListAdapter: ArrayAdapter<Memo>
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
        countdownTextView = findViewById(R.id.countdownTextView)
        memoCountTextView = findViewById(R.id.memoCountTextView)

        // カウントダウンタイマーを開始
        startCountdownTimer()

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

        // 今日のメモ数を表示
        updateMemoCount()

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
            memoLauncher.launch(intent)  // FullListActivityを起動
        }

        // メモを保存または編集
        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()
            val currentDateTime = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(Date())

            if (title.isNotEmpty() && content.isNotEmpty()) {
                if (editingIndex != null) {
                    memoList[editingIndex!!] = Memo(title, content, memoList[editingIndex!!].dateTime)
                    editingIndex = null  // 編集終了
                } else {
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

                // 保存後、リスト画面に遷移
                val intent = Intent(this, FullListActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // カウントダウンタイマーの実装
    private fun startCountdownTimer() {
        object : CountDownTimer(60000, 1000) {  // 1分間（60,000ms）、1秒ごとに更新
            override fun onTick(millisUntilFinished: Long) {
                countdownTextView.text = "残り時間: ${millisUntilFinished / 1000}秒"  // 秒単位で表示
            }

            override fun onFinish() {
                countdownTextView.text = "時間切れ！"
            }
        }.start()
    }

    // 今日のメモ数をカウントして更新
    private fun updateMemoCount() {
        val today = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
        val todayMemos = memoList.filter { it.dateTime.startsWith(today) }
        val memoCount = todayMemos.size
        memoCountTextView.text = "今日のメモ数: $memoCount (目標: 10ページ)"
    }
}
