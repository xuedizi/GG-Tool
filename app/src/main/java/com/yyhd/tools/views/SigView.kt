package com.yyhd.tools.views

import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import com.yyhd.tools.R
import com.yyhd.tools.utils.MessageDigestUtil
import java.io.File

/**
 * TODO
 *
 * @author xichao
 * @version v1.0.0
 * @created 2018/10/31
 */
class SigView : FrameLayout {
  constructor(context: Context) : this(context, null, 0)
  constructor(
    context: Context,
    attributeSet: AttributeSet
  ) : this(context, attributeSet, 0)

  constructor(
    context: Context,
    attributeSet: AttributeSet?,
    defStyle: Int
  ) : super(context, attributeSet, defStyle)

  init {

    val packageManager = context.packageManager

    val inflate = LayoutInflater.from(context).inflate(R.layout.view_sig, this@SigView)
    val txtIdSig = inflate.findViewById<TextView>(R.id.txtId_sig)
    val edIdSig = inflate.findViewById<EditText>(R.id.etId_sig)
    val btnIdSig = inflate.findViewById<Button>(R.id.btnId_sig)

    btnIdSig.setOnClickListener(OnClickListener {
      val content = edIdSig.text.toString()
      if (content.trim().isEmpty()) return@OnClickListener
      try {
        //是否存在此安装包
        val packageInfo = packageManager.getPackageInfo(
            content.trim(), PackageManager.GET_SIGNATURES
        )
        txtIdSig.text = MessageDigestUtil.getMD5String(packageInfo.signatures[0].toByteArray())
      } catch (e: Exception) {
        //默认输入的文件名称
        val files = content.trim().split('-')
        var path = StringBuilder()
        path.append(Environment.getExternalStorageDirectory(),"/yyhd/apk")
        for (name: String in files) {
          path.append(File.separator).append(name)
        }
        try {
          val packageInfo = packageManager.getPackageArchiveInfo(
              path.toString(), PackageManager.GET_SIGNATURES
          )
          txtIdSig.text = MessageDigestUtil.getMD5String(packageInfo.signatures[0].toByteArray())
        } catch (e: Exception) {
        }
      }
    })
  }
}