package com.stratagile.pnrouter.application

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.os.Process
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.tox.toxcore.ToxCoreJni

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.lang.reflect.Field
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap

class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {
    private var mContext: Context? = null
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null
    // 用来存储设备信息和异常信息
    private val mInfo = HashMap<String, String>()
    private val mDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    fun init(context: Context) {
        mContext = context
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        // 如果用户没有处理则让系统默认的异常处理器来处理
        if (!handleException(e) && mDefaultHandler != null) {
            mDefaultHandler!!.uncaughtException(t, e)
        } else {
            try {
                Thread.sleep(3000)
            } catch (e1: InterruptedException) {
                Log.e(TAG, "error", e)
            }
            if(ConstantValue.curreantNetworkType.equals("TOX"))
            {
                ToxCoreJni.getInstance().toxKill()
            }
            AppConfig.instance.stopAllService()
            // 退出程序
            Process.killProcess(Process.myPid())
            System.exit(1)
        }
    }

    // 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成.
    private fun handleException(e: Throwable?): Boolean {
        if (e == null) {
            return false
        }
        // 使用Toast来显示异常信息
        object : Thread() {
            override fun run() {
                Looper.prepare()
                Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_SHORT).show()
                Looper.loop()
            }
        }.start()

        //收集设备参数信息
        collectErrorInfo()
        //保存日志文件
        saveErrorInfo(e)
        return true
    }

    // 收集设备参数信息
    private fun collectErrorInfo() {
        val pm = mContext!!.packageManager
        try {
            val pi = pm.getPackageInfo(mContext!!.packageName, PackageManager.GET_ACTIVITIES)
            if (pi != null) {
                val versionName = if (TextUtils.isEmpty(pi.versionName)) "未设置版本号" else pi.versionName
                val versionCode = pi.versionCode.toString() + ""
                mInfo["versionName"] = versionName
                mInfo["versionCode"] = versionCode
            }

            val fields = Build::class.java.fields
            if (fields != null && fields.size > 0) {
                for (field in fields) {
                    field.isAccessible = true
                    try {
                        mInfo[field.name] = field.get(null).toString()
                    } catch (e: IllegalAccessException) {
                        Log.e(TAG, "an error occured when collect crash info", e)
                    }

                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "an error occured when collect package info", e)
        }

    }

    // 保存错误信息到文件中
    private fun saveErrorInfo(e: Throwable) {
        val stringBuffer = StringBuffer()
        for ((keyName) in mInfo) {
            stringBuffer.append("$keyName=$keyName\n")
        }

        val writer = StringWriter()
        val printWriter = PrintWriter(writer)
        e.printStackTrace(printWriter)
        var cause: Throwable? = e.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }

        printWriter.close()

        val result = writer.toString()
        stringBuffer.append(result)

        val currentTime = System.currentTimeMillis()
        val time = mDateFormat.format(Date())
        val fileName = "crash-$time-$currentTime.txt"

        // 判断有没有SD卡
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val dir = File(Environment.getExternalStorageDirectory().absolutePath + ConstantValue.localPath+"/ppmcrash")
            if (!dir.exists()) {
                dir.mkdirs()
            }

            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(dir.toString() + "/" + fileName)
                fos.write(stringBuffer.toString().toByteArray())
            } catch (e1: FileNotFoundException) {
                Log.e(TAG, "an error occured due to file not found", e)
            } catch (e2: IOException) {
                Log.e(TAG, "an error occured while writing file...", e)
            } finally {
                try {
                    fos!!.close()
                } catch (e1: IOException) {
                    Log.e(TAG, "an error occured when close file", e)
                }

            }
        }
    }

    companion object {
        private val TAG = "CrashHandler"
        private var mInstance: CrashHandler? = null

        val instance: CrashHandler
            get() {
                if (mInstance == null) {
                    synchronized(CrashHandler::class.java) {
                        if (mInstance == null) {
                            mInstance = CrashHandler()
                        }
                    }
                }
                return mInstance!!
            }
    }
}