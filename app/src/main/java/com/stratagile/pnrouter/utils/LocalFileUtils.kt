package com.stratagile.pnrouter.utils

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

    val vpnList: ArrayList<Map<*, *>> = ArrayList()


    //检查本地数据合法性
    fun inspectionLocalData()
    {
        var userId = "fileData"
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
            FileUtil.deleteFile(ConstantValue.localPath + "/RouterList/" + userId + ".json")
            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.deleteAll()
        } finally {

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
            var userId = "fileData"
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

            } finally {

            }
            return localAssetArrayList
        }
    /**
     * 同步sd上的文件数据到greenDao
     */
    fun updateGreanDaoFromLocal() {
            //wallet = walletList.get(SpUtil.getInt(AppConfig.instance, ConstantValue.currentWallet, 0));
        var userId = "fileData"
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

                            if (myRouter.getType() == 0)
                            {
                               /* val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.UserSn.eq(myRouter.getUpLoadFile().userSn)).list()
                                if (routerEntityList != null && routerEntityList!!.size == 0) {
                                    AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(myRouter.getUpLoadFile())
                                }*/
                            }
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
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

                                if (myRouter.getType() ==0)
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
        if (myRouter == null) {
            return
        }
        var userId = "fileData"
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

                    if (myRouter!!.getType() == 0)
                    {
                        if (myRouterItem.getUpLoadFile() != null && myRouter!!.getUpLoadFile().path.equals(myRouterItem.getUpLoadFile().path)) {
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

        } finally {

        }
    }
    fun deleteLocalAssets(deletePath: String): UpLoadFile? {
        var userId = "fileData"
        if(deletePath == null && deletePath.equals(""))
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

                    if (myRouter.getType() == 0)
                    {
                        if (!myRouter.getUpLoadFile().path.equals(deletePath)) {
                            newRouterArrayList.add(myRouter)
                        } else{
                            deleteRouterEntity = myRouter.getUpLoadFile()
                        }
                    }
                }
                FileUtil.saveRouterData(userId, gson.toJson(newRouterArrayList))
            }

        } catch (e: Exception) {

        } finally {

        }
        return deleteRouterEntity
    }
    /**
     * 批量更新
     *
     * @param myAssets
     */
    fun updateList(myAssets: ArrayList<MyFile>?) {
        var userId = "fileData"
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

        } finally {

        }
    }

    /**
     * 更新某个本地文件
     *
     * @param router
     */
    fun updateLocalAssets(router: MyFile?) {
        var userId = "fileData"
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

                    if (myRouter.getType() == 0)
                    {
                        if (router!!.getUpLoadFile() != null && myRouter.getUpLoadFile().path.equals(router!!.getUpLoadFile().path)) {
                            newRouterArrayList.add(router)
                        } else {
                            newRouterArrayList.add(myRouter)
                        }
                    }
                }
                FileUtil.saveRouterData(userId, gson.toJson(newRouterArrayList))
            }

        } catch (e: Exception) {

        } finally {

        }
    }
}
