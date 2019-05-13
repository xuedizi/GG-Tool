package com.yyhd.tools.utils

import java.io.File
import java.io.InputStream
import java.net.URL

/**
 * TODO
 *
 * @author xichao
 * @version v1.0.0
 * @created 2018/10/30
 */

class FileUtils {
  companion object {
    fun get(): FileUtils {
      return FileUtils()
    }
  }

  fun inputstreamtofile(
    file: File,
    inputStream: InputStream
  ) {
    file.writeBytes(inputStream.readBytes())
  }

  fun urltofile(
    url: String,
    file: File
  ) {
    file.writeBytes(URL(url).readBytes())
  }
}