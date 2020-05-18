package com.stratagile.pnrouter.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.socks.library.KLog
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

object FileShareUtil {
    fun getFilePathFromContentUri(selectedVideoUri: Uri?, context: Activity): String {
        var filePath = ""
        val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor: Cursor = context.managedQuery(selectedVideoUri, filePathColumn, null, null, null)
        cursor.moveToFirst()
        val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
        filePath = cursor.getString(columnIndex)
        return filePath
    }

    fun getFPUriToPath(context: Context, uri: Uri): String {
        try {
            val packs: List<PackageInfo> = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS)
            if (packs != null) {
                val fileProviderClassName: String = FileProvider::class.java.getName()
                KLog.i(uri.authority)
                for (pack in packs) {
                    if (pack.providers != null) {
                        val providers: Array<ProviderInfo> = pack.providers
                        for (provider in providers) {
                            KLog.i(provider.authority)
                            if (uri.authority == provider.authority) {
//                                KLog.i(provider.name)
//                                if (provider.name.equals(fileProviderClassName, true)) {
                                    val fileProviderClass: Class<FileProvider> = FileProvider::class.java
                                    try {
                                        val getPathStrategy: Method = fileProviderClass.getDeclaredMethod("getPathStrategy", Context::class.java, String::class.java)
                                        getPathStrategy.setAccessible(true)
                                        val invoke: Any = getPathStrategy.invoke(null, context, uri.authority)
                                        if (invoke != null) {
                                            val PathStrategyStringClass: String = FileProvider::class.java.getName() + "\$PathStrategy"
                                            val PathStrategy = Class.forName(PathStrategyStringClass)
                                            val getFileForUri: Method = PathStrategy.getDeclaredMethod("getFileForUri", Uri::class.java)
                                            getFileForUri.setAccessible(true)
                                            val invoke1: Any = getFileForUri.invoke(invoke, uri)
                                            if (invoke1 is File) {
                                                return (invoke1 as File).getAbsolutePath()
                                            }
                                        }
                                    } catch (e: NoSuchMethodException) {
                                        e.printStackTrace()
                                    } catch (e: InvocationTargetException) {
                                        e.printStackTrace()
                                    } catch (e: IllegalAccessException) {
                                        e.printStackTrace()
                                    } catch (e: ClassNotFoundException) {
                                        e.printStackTrace()
                                    }
                                    break
//                                }
//                                break
                            }
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ""
    }
}