package com.stratagile.pnrouter.utils

import android.os.Environment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.MyFile
import com.stratagile.pnrouter.entity.file.UpLoadFile
import java.util.*

/**
 * Created by zl on 2018/9/12.
 */

object LocalFileUtils {

    var fileLock = Object()
    val vpnList: ArrayList<Map<*, *>> = ArrayList()


    //检查本地数据合法性
    fun inspectionLocalData()
    {
        synchronized(fileLock){
            var userId = "fileData5"
            if(userId.equals(""))
            {
                return
            }
            val gson = Gson()
            val localAssetArrayList: ArrayList<MyFile>
            try {
                //开始读取sd卡的文件数据
                var routerStr = FileUtil.readRoutersData(userId)
                if (routerStr != "") {
                    localAssetArrayList = gson.fromJson<ArrayList<MyFile>>(routerStr, object : TypeToken<ArrayList<MyFile>>() {

                    }.type)
                }

            } catch (e: Exception) {
                FileUtil.deleteFile(Environment.getExternalStorageDirectory().getPath()+ConstantValue.localPath + "/RouterList/" + userId + ".json")
                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.deleteAll()
            } finally {

            }
        }

    }
    /**
     * 获取全部本地文件
     * @return
     */
    //开始读取sd卡的文件数据
    //根据传入的集合(旧集合)获取迭代器
    //遍历老集合
    //记录每一个元素
    val localFilesList: ArrayList<MyFile>
        get() {
            synchronized(fileLock){
                var userId = "fileData5"
                var localAssetArrayList: ArrayList<MyFile> = ArrayList()
                if(userId.equals(""))
                {
                    return localAssetArrayList
                }
                val gson = Gson()

                try {
                    var assetStr = FileUtil.readRoutersData(userId)
                    if (assetStr != "") {
                        localAssetArrayList = gson.fromJson<ArrayList<MyFile>>(assetStr, object : TypeToken<ArrayList<MyFile>>() {

                        }.type)
                    }

                } catch (e: Exception) {
                    System.out.println("出错啦_localFilesList")
                    //e.printStackTrace()
                } finally {

                }
                var localNeedAssetArrayList: ArrayList<MyFile> = ArrayList()
                for (myFile in localAssetArrayList)
                {
                    if(myFile.type == 0 && myFile.userSn.equals(ConstantValue.currentRouterSN))
                    {
                        localNeedAssetArrayList.add(myFile)
                    }
                }
                return localNeedAssetArrayList
            }
        }

    /**
     * 同步sd上的文件数据到greenDao
     */
    fun updateGreanDaoFromLocal() {
        //wallet = walletList.get(SpUtil.getInt(AppConfig.instance, ConstantValue.currentWallet, 0));
        var userId = "fileData5"
        if(userId.equals(""))
        {
            return
        }
        val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().list()
        if(routerEntityList.size == 0)
        {
            val gson = Gson()
            val localRouterArrayList: ArrayList<MyFile>
            try {
                //开始读取sd卡的文件数据
                var assetStr = ""
                assetStr = FileUtil.readRoutersData(userId)
                if (assetStr != "") {
                    localRouterArrayList = gson.fromJson<ArrayList<MyFile>>(assetStr, object : TypeToken<ArrayList<MyFile>>() {

                    }.type)
                    for (myRouter in localRouterArrayList) {

                        if (myRouter.getType() == 0 && myRouter.getUserSn().equals(ConstantValue.currentRouterSN))
                        {
                            /* val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.UserSn.eq(myRouter.getUpLoadFile().userSn)).list()
                             if (routerEntityList != null && routerEntityList!!.size == 0) {
                                 AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(myRouter.getUpLoadFile())
                             }*/
                        }
                    }
                }

            } catch (e: Exception) {
                System.out.println("出错啦_localFilesList")
                //e.printStackTrace()
            } finally {

            }
        }else{
            val gson = Gson()
            val localRouterArrayList: ArrayList<MyFile>
            try {
                //开始读取sd卡的文件数据
                var assetStr = FileUtil.readRoutersData(userId)
                if (assetStr != "") {
                    localRouterArrayList = gson.fromJson<ArrayList<MyFile>>(assetStr, object : TypeToken<ArrayList<MyFile>>() {

                    }.type)
                    for (myRouter in localRouterArrayList) {

                        if (myRouter.getType() ==0 && myRouter.getUserSn().equals(ConstantValue.currentRouterSN))
                        {
                            for (routerEntity in routerEntityList) {

                            }
                        }
                    }
                }

            } catch (e: Exception) {

            } finally {

            }
        }


    }

    /**
     * 新增本地文件数据
     *
     * @param myRouter
     */
    fun insertLocalAssets(myRouter: MyFile?) {
        synchronized(fileLock){
            if (myRouter == null) {
                return
            }
            var userId = "fileData5"
            if(userId.equals(""))
            {
                return
            }
            val gson = Gson()
            val localAssetArrayList: ArrayList<MyFile>
            try {
                //开始读取sd卡的文件数据
                var routerStr = FileUtil.readRoutersData(userId)
                if (routerStr != "") {
                    localAssetArrayList = gson.fromJson<ArrayList<MyFile>>(routerStr, object : TypeToken<ArrayList<MyFile>>() {

                    }.type)
                    var isHad = false
                    for (myRouterItem in localAssetArrayList) {

                        if (myRouter!!.getType() == 0 && myRouter.getUserSn().equals(ConstantValue.currentRouterSN))
                        {
                            if (myRouterItem.getUpLoadFile() != null && myRouter!!.getUpLoadFile().fileKey.equals(myRouterItem.getUpLoadFile().fileKey)) {
                                isHad = true
                                break
                            }
                        }
                    }
                    if (isHad) {
                        LocalFileUtils.updateLocalAssets(myRouter)
                    } else {
                        localAssetArrayList.add(myRouter)
                        FileUtil.saveRouterData(userId, gson.toJson(localAssetArrayList))
                    }


                } else {
                    localAssetArrayList = ArrayList()
                    localAssetArrayList.add(myRouter)
                    FileUtil.saveRouterData(userId, gson.toJson(localAssetArrayList))
                }

            } catch (e: Exception) {
                System.out.println("出错啦_insertLocalAssets")
                //e.printStackTrace()
            } finally {

            }
        }
    }
    fun getLocalAssets(fileKey: String): UpLoadFile? {
        synchronized(fileLock){
            var userId = "fileData5"
            if(fileKey == null && fileKey.equals(""))
            {
                return null
            }
            val gson = Gson()
            val localRouterArrayList: ArrayList<MyFile>
            try {
                //开始读取sd卡的文件数据
                var assetStr = FileUtil.readRoutersData(userId)
                if (assetStr != "") {
                    localRouterArrayList = gson.fromJson<ArrayList<MyFile>>(assetStr, object : TypeToken<ArrayList<MyFile>>() {

                    }.type)
                    for (myRouter in localRouterArrayList) {

                        if (myRouter.getType() == 0 && myRouter.getUserSn().equals(ConstantValue.currentRouterSN))
                        {
                            if (myRouter.getUpLoadFile().fileKey.equals(fileKey)) {
                                return  myRouter.getUpLoadFile()
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                System.out.println("出错啦_localFilesList")
                //e.printStackTrace()
            } finally {

            }
            return null
        }
    }
    fun deleteLocalAssets(deleteFileKey: String): UpLoadFile? {
        synchronized(fileLock){
            var userId = "fileData5"
            if(deleteFileKey == null && deleteFileKey.equals(""))
            {
                return null
            }
            val gson = Gson()
            val localRouterArrayList: ArrayList<MyFile>
            val newRouterArrayList = ArrayList<MyFile>()
            var deleteRouterEntity:UpLoadFile? = null
            try {
                //开始读取sd卡的文件数据
                var assetStr = FileUtil.readRoutersData(userId)
                if (assetStr != "") {
                    localRouterArrayList = gson.fromJson<ArrayList<MyFile>>(assetStr, object : TypeToken<ArrayList<MyFile>>() {

                    }.type)
                    for (myRouter in localRouterArrayList) {

                        if (myRouter.getType() == 0 && myRouter.getUserSn().equals(ConstantValue.currentRouterSN))
                        {
                            if (!myRouter.getUpLoadFile().fileKey.equals(deleteFileKey)) {
                                newRouterArrayList.add(myRouter)
                            } else{
                                deleteRouterEntity = myRouter.getUpLoadFile()
                            }
                        }
                    }
                    FileUtil.saveRouterData(userId, gson.toJson(newRouterArrayList))
                }

            } catch (e: Exception) {
                System.out.println("出错啦_localFilesList")
                //e.printStackTrace()
            } finally {

            }
            return deleteRouterEntity
        }
    }
    /**
     * 批量更新
     *
     * @param myAssets
     */
    fun updateList(myAssets: ArrayList<MyFile>?) {
        synchronized(fileLock){
            var userId = "fileData5"
            if(userId.equals(""))
            {
                return
            }
            var myAssets: ArrayList<MyFile>? = myAssets ?: return
            val newList = ArrayList<MyFile>()     //创建新集合
            val it = myAssets!!.iterator()        //根据传入的集合(旧集合)获取迭代器
            while (it.hasNext()) {          //遍历老集合
                val obj = it.next() as MyFile       //记录每一个元素
                if (!newList.contains(obj)) {      //如果新集合中不包含旧集合中的元素
                    newList.add(obj)       //将元素添加
                }
            }
            myAssets = newList
            val gson = Gson()
            try {
                val itadd = myAssets!!.iterator()        //根据传入的集合(旧集合)获取迭代器
                while (itadd.hasNext()) {          //遍历老集合
                    val obj = itadd.next() as MyFile
                    insertLocalAssets(obj)
                }
                //FileUtil.saveRouterData(wallet.getAddress(), gson.toJson(myAssets));
            } catch (e: Exception) {
                System.out.println("出错啦_localFilesList")
                //e.printStackTrace()
            } finally {

            }
        }

    }

    /**
     * 更新某个本地文件
     *
     * @param router
     */
    fun updateLocalAssets(router: MyFile?) {
        synchronized(fileLock){
            var userId = "fileData5"
            if(userId.equals(""))
            {
                return
            }
            if (router == null) {
                return
            }
            val gson = Gson()
            val localRouterArrayList: ArrayList<MyFile>
            val newRouterArrayList = ArrayList<MyFile>()
            try {
                //开始读取sd卡的文件数据
                var assetStr = FileUtil.readRoutersData(userId)
                if (assetStr != "") {
                    localRouterArrayList = gson.fromJson<ArrayList<MyFile>>(assetStr, object : TypeToken<ArrayList<MyFile>>() {

                    }.type)
                    for (myRouter in localRouterArrayList) {

                        if (myRouter.getType() == 0 && myRouter.getUserSn().equals(ConstantValue.currentRouterSN))
                        {
                            if (router!!.getUpLoadFile() != null && myRouter.getUpLoadFile().fileKey.equals(router!!.getUpLoadFile().fileKey)) {
                                newRouterArrayList.add(router)
                            } else {
                                newRouterArrayList.add(myRouter)
                            }
                        }
                    }
                    FileUtil.saveRouterData(userId, gson.toJson(newRouterArrayList))
                }

            } catch (e: Exception) {
                System.out.println("出错啦_localFilesList")
                //e.printStackTrace()
            } finally {

            }
        }
    }
}
