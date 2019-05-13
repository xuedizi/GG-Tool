package com.yyhd.tools.views

import android.content.Context
import android.os.Environment
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import com.yyhd.tools.R
import com.yyhd.tools.views.StatusView.Status.ONLINE
import com.yyhd.tools.views.StatusView.Status.TEST
import java.io.File

/**
 * TODO
 *
 * @author xichao
 * @version v1.0.0
 * @created 2018/10/30
 */
class StatusView : FrameLayout, OnClickListener {

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

  val file: File = File(Environment.getExternalStorageDirectory(), "ggtest")

  enum class Status {
    ONLINE,
    TEST
  }

  private val txtIdStatus: TextView
  private val btnIdChange: Button

  fun changeStatus() {

    val status: Status = if (file.exists()) TEST else ONLINE

    if (status == TEST) {
      txtIdStatus.text = "当前环境:测试"
    } else if (status == ONLINE) {
      txtIdStatus.text = "当前环境:线上"
    }
//    PackageUtils.get().getRunningServiceInfo(context, "com.iplay.assistant")
  }

  init {
    val inflate = LayoutInflater.from(context).inflate(R.layout.view_env, this)
    txtIdStatus = inflate.findViewById(R.id.txtId_status)
    btnIdChange = inflate.findViewById(R.id.btnId_change)
    btnIdChange.setOnClickListener(this)

    changeStatus()
  }

  override fun onClick(v: View?) {
    if (v == btnIdChange) {
      if (file.exists()) {
        file.delete()
      } else {
        file.mkdirs()
      }
      changeStatus()
    }
  }
}