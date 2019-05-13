package com.yyhd.tools

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.yyhd.tools.utils.FileUtils
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit.SECONDS

/**
 * YYHD 辅助工具
 *
 * @author xichao
 * @version v1.0.0
 * @created 2018/10/30
 */

class HomeActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)
  }

  override fun onResume() {
    super.onResume()
//    Thread(Runnable { upgrade() }).start()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menu?.add("ReadMe")
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    android.app.AlertDialog.Builder(this@HomeActivity).setMessage("此应用是YYHD工具,功能包括环境切换等").setPositiveButton(
        "我知道了"
    ) { dialog, _ ->
      dialog.dismiss()
    }.show()
    return true
  }

  @Suppress("DEPRECATION") fun upgrade() {
    val url = "http://docs.yyhudong.com/nas/客户端/yyhdtool/tools.apk"
    val file = File(Environment.getExternalStorageDirectory(), "yyhd/tools/tools.apk")
    file.delete()
    file.parentFile.mkdirs()
//    FileUtils.get().urltofile(url, file)

    val builder = OkHttpClient.Builder().hostnameVerifier { _, _ -> true }.readTimeout(
        100000, SECONDS
    ).connectTimeout(100000, SECONDS)
    val okHttpClient = builder.build()
    val build = Request.Builder().url(url).build()
    val response = okHttpClient.newCall(build).execute()
    if (response.isSuccessful) {
      val body = response.body()
      FileUtils.get().inputstreamtofile(file, body!!.byteStream())
      val packageInfo = this.packageManager.getPackageArchiveInfo(file.absolutePath, 0)
      val currPackageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
      val versionCode = packageInfo.versionCode
      val currVercode = currPackageInfo.versionCode
      if (versionCode > currVercode) {
        val install = Intent()
        install.action = Intent.ACTION_VIEW
        install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        startActivity(install)
      }
    }
  }
}