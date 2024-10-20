package com.example.zerosecondmemo

// メモのタイトル、内容、作成日時を持つデータクラス
data class Memo(
    val title: String,
    val content: String,
    val dateTime: String
) {
    // メモを指定された形式で文字列として表示
    override fun toString(): String {
        return "$dateTime  $title\n$content"
    }
}
