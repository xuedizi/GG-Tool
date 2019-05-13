package com.yyhd.tools.views

import android.content.Context
import android.os.Environment
import android.text.format.Formatter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import com.yyhd.tools.R
import java.io.File

/**
 * TODO
 *
 * @author xichao
 * @version v1.0.0
 * @created 2018/12/7
 */
class CleanGameDetailCacheView : FrameLayout {

  val packageName = "com.iplay.assistant"

  constructor(context: Context) : super(context)
  constructor(
    context: Context,
    attributeSet: AttributeSet
  ) : this(context, attributeSet, 0)

  constructor(
    context: Context,
    attributeSet: AttributeSet,
    defStyle: Int
  ) : super(context, attributeSet, defStyle)

  init {

    LayoutInflater.from(context).inflate(R.layout.view_clear_game_cache, this);

    findViewById<Button>(R.id.clean_game_detail_cache).setOnClickListener {
      val files = File(
          Environment.getExternalStorageDirectory(), "sandbox/$packageName/gamedetails/"
      )
      if (null == files.listFiles()) return@setOnClickListener

      var length = 0L
      for (file in files.listFiles()) {
        length += file.length()
        file.delete()
      }

      if (length <= 0) {
        Toast.makeText(
            context, "你丫都没进过游戏详情页，清理个毛线啊...", Toast.LENGTH_LONG
        ).show()
      } else {
        Toast.makeText(
            context, "清理" + Formatter.formatFileSize(context, length), Toast.LENGTH_LONG
        ).show()
      }

    }
  }
}