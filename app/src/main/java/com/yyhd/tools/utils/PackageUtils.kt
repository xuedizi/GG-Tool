package com.yyhd.tools.utils

import android.app.ActivityManager
import android.content.Context

/**
 * TODO
 *
 * @author xichao
 * @version v1.0.0
 * @created 2018/10/30
 */
class PackageUtils {

  companion object {
    fun get(): PackageUtils {
      return PackageUtils()
    }

  }

  fun getRunningServiceInfo(
    context: Context,
    packageName: String
  ) {
    val mActivityManager = context
        .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    mActivityManager.killBackgroundProcesses(packageName)
  }
}