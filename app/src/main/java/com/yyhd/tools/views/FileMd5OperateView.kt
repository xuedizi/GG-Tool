package com.yyhd.tools.views

import android.app.AlertDialog
import android.content.Context
import android.os.Environment
import android.text.format.Formatter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.yyhd.tools.R
import com.yyhd.tools.utils.MessageDigestUtil
import java.io.File
import java.io.FileInputStream

/**
 * TODO
 *
 * @author xichao
 * @version v1.0.0
 * @created 2019/4/12
 */

class FileMd5OperateView(
  context: Context,
  attributes: AttributeSet?,
  defStyleAttr: Int
) : FrameLayout(context) {

  constructor(context: Context) : this(context, null)

  constructor(
    context: Context,
    attributes: AttributeSet?
  ) : this(context, attributes, 0)

  private val etIdPath: EditText
  private var mRomInfos = ArrayList<RomInfo>()

  init {
    val view: View = LayoutInflater.from(context).inflate(R.layout.view_file_md5_opt, this)
    etIdPath = view.findViewById(R.id.etId_path)
    val btnOpt: Button = view.findViewById(R.id.btnId_operate)
    btnOpt.setOnClickListener {
      startOpt()
    }
  }

  private fun startOpt() {

    val listView = ListView(context)
    mRomInfos.clear()
    val adapter = RomAdapter(mRomInfos, context)
    listView.adapter = adapter
    AlertDialog.Builder(context).setView(listView).show()

    var file = File(Environment.getExternalStorageDirectory(), etIdPath.text.toString().trim())
    if (!file.exists() || !file.isDirectory) {
      Toast.makeText(context, "需要一个文件夹目录", Toast.LENGTH_LONG).show()
      return
    }
    val roms = file.listFiles()
    if (null != roms && roms.isNotEmpty()) {
      Thread {
        run {
          for (romFile: File in roms) {
            val romInfo = RomInfo(romFile, System.currentTimeMillis())
            val fileMD5String = calculateRomHash(romFile)
            romInfo.endTime = System.currentTimeMillis()
            romInfo.MD5 = fileMD5String
            mRomInfos.add(romInfo)
          }
          handler.post {
            adapter.notifyDataSetChanged()
          }
        }
      }.start()
    }
  }

  class RomInfo(
    var file: File,
    var startTime: Long
  ) {
    var endTime: Long = 0
    var MD5: String? = null
  }

  class RomAdapter(
    private var romInfos: ArrayList<RomInfo>,
    var context: Context
  ) : BaseAdapter() {
    override fun getView(
      position: Int,
      convertView: View?,
      parent: ViewGroup?
    ): View {
      var holderView: View
      var holder: ViewHolder
      if (null == convertView) {
        holderView = LayoutInflater.from(context).inflate(R.layout.item_rom, null)
        holder = ViewHolder(holderView)
        holderView.tag = holder
      } else {
        holderView = convertView
        holder = holderView.tag as ViewHolder
      }
      val item = getItem(position)
      holder.txtIdName.text = "文件名:" + item.file.name
      holder.txtIdMd5.text = "MD5:" + item.MD5
      holder.txtIdTime.text = "耗时:" + (item.endTime - item.startTime).toString()
      holder.txtIdSize.text = "文件大小:" + Formatter.formatFileSize(
          context, item.file.length()
      ) + "(" + item.file.length() + ")"
      return holderView
    }

    override fun getItem(position: Int): RomInfo {
      return romInfos[position]
    }

    override fun getItemId(position: Int): Long {
      return position.toLong()
    }

    override fun getCount(): Int {
      return romInfos.size
    }
  }

  class ViewHolder(var item: View) {
    val txtIdName: TextView = item.findViewById(R.id.txtId_name)
    val txtIdMd5: TextView = item.findViewById(R.id.txtId_md5)
    val txtIdTime: TextView = item.findViewById(R.id.txtId_time)
    val txtIdSize: TextView = item.findViewById(R.id.txtId_size)

  }

  internal var MAXBUFFER = 1024 * 1024 * 2

  fun calculateRomHash(romFile: File?): String? {
    var fileHash: String? = null

    if (romFile != null && romFile.isFile) {
      val fileLength = romFile.length()
      if (fileLength > 0) {
        val buffer = ByteArray(MAXBUFFER)
        try {
          val inputStream = FileInputStream(romFile)
          if (fileLength > MAXBUFFER) {
            inputStream.skip(fileLength / 2)
            val length = inputStream.read(buffer)
            inputStream.close()
            val temp = ByteArray(length)
            System.arraycopy(buffer, 0, temp, 0, length)
            val md5 = MessageDigestUtil.getMD5String(temp)
            fileHash = md5 + String.format("%X", fileLength)
          } else {
            val length = inputStream.read(buffer)
            val temp = ByteArray(length)
            System.arraycopy(buffer, 0, temp, 0, length)
            val md5 = MessageDigestUtil.getMD5String(temp)
            fileHash = md5 + String.format("%X", fileLength)
          }
        } catch (e: Exception) {
          e.printStackTrace()
        }

      }
    }
    return fileHash
  }
}
