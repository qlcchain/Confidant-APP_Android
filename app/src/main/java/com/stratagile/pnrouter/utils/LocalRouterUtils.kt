package com.stratagile.pnrouter.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.db.RouterEntityDao
import com.stratagile.pnrouter.entity.MyRouter
import java.util.*

/**
 * Created by zl on 2018/9/12.
 */

object LocalRouterUtils {

    val vpnList: ArrayList<Map<*, *>> = ArrayList()
    /**
     * 获取全部本地路由器
     * @return
     */
    //开始读取sd卡的路由器数据
    //根据传入的集合(旧集合)获取迭代器
    //遍历老集合
    //记录每一个元素
    val localAssetsList: ArrayList<MyRouter>
        get() {
            var userId = FileUtil.getLocalUserId()
            var localAssetArrayList: ArrayList<MyRouter> = ArrayList()
            if(userId.equals(""))
            {
                return localAssetArrayList
            }
            val gson = Gson()

            try {
                var assetStr = FileUtil.readRoutersData(userId)
                if (assetStr != "") {
                    localAssetArrayList = gson.fromJson<ArrayList<MyRouter>>(assetStr, object : TypeToken<ArrayList<MyRouter>>() {

                    }.type)
                }

            } catch (e: Exception) {

            } finally {

            }
            return localAssetArrayList
        }

    /**
     * 同步sd上的路由器数据到greenDao
     */
    fun updateGreanDaoFromLocal() {
            //wallet = walletList.get(SpUtil.getInt(AppConfig.instance, ConstantValue.currentWallet, 0));
        var userId = FileUtil.getLocalUserId()
        if(userId.equals(""))
        {
            return
        }
        val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().list()
            if(routerEntityList.size == 0)
            {
                val gson = Gson()
                val localRouterArrayList: ArrayList<MyRouter>
                try {
                    //开始读取sd卡的路由器数据
                    var assetStr = ""
                    assetStr = FileUtil.readRoutersData(userId)
                    if (assetStr != "") {
                        localRouterArrayList = gson.fromJson<ArrayList<MyRouter>>(assetStr, object : TypeToken<ArrayList<MyRouter>>() {

                        }.type)
                        for (myAsset in localRouterArrayList) {

                            if (myAsset.getType() === 0)
                            {
                                val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.RouterId.eq(myAsset.getRouterEntity().routerId)).list()
                                if (routerEntityList != null && routerEntityList!!.size == 0) {
                                    AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(myAsset.getRouterEntity())
                                }
                            }
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {

                }
            }else{
                    val gson = Gson()
                    val localRouterArrayList: ArrayList<MyRouter>
                    try {
                        //开始读取sd卡的路由器数据
                        var assetStr = FileUtil.readRoutersData(userId)
                        if (assetStr != "") {
                            localRouterArrayList = gson.fromJson<ArrayList<MyRouter>>(assetStr, object : TypeToken<ArrayList<MyRouter>>() {

                            }.type)
                            for (myRouter in localRouterArrayList) {

                                if (myRouter.getType() === 0)
                                 {
                                    for (routerEntity in routerEntityList) {

                                        if (myRouter.getRouterEntity().routerId.equals(routerEntity.routerId)) {
                                            myRouter.getRouterEntity().setId(routerEntity.getId())//这个很重要，要不没法更新greenDao
                                            myRouter.getRouterEntity().setRouterId(routerEntity.routerId)
                                            myRouter.getRouterEntity().setUsername(routerEntity.username)
                                            myRouter.getRouterEntity().setUserId(routerEntity.userId)
                                            myRouter.getRouterEntity().setRouterName(routerEntity.routerName)
                                            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(myRouter.getRouterEntity())
                                        }
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
     * 新增本地路由器数据
     *
     * @param myRouter
     */
    fun insertLocalAssets(myRouter: MyRouter?) {
        if (myRouter == null) {
            return
        }
        var userId = FileUtil.getLocalUserId()
        if(userId.equals(""))
        {
            return
        }
        val gson = Gson()
        val localAssetArrayList: ArrayList<MyRouter>
        try {
            //开始读取sd卡的路由器数据
            var routerStr = FileUtil.readRoutersData(userId)
            if (routerStr != "") {
                localAssetArrayList = gson.fromJson<ArrayList<MyRouter>>(routerStr, object : TypeToken<ArrayList<MyRouter>>() {

                }.type)
                var isHad = false
                for (myRouterItem in localAssetArrayList) {

                    if (myRouter!!.getType() === 0)
                    {
                        if (myRouterItem.getRouterEntity() != null && myRouter!!.getRouterEntity().routerId.equals(myRouterItem.getRouterEntity().routerId)) {
                            isHad = true
                            break
                        }
                    }
                }
                if (isHad) {
                    LocalRouterUtils.updateLocalAssets(myRouter)
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

    /**
     * 批量更新
     *
     * @param myAssets
     */
    fun updateList(myAssets: ArrayList<MyRouter>?) {
        var userId = FileUtil.getLocalUserId()
        if(userId.equals(""))
        {
            return
        }
        var myAssets: ArrayList<MyRouter>? = myAssets ?: return
        val newList = ArrayList<MyRouter>()     //创建新集合
        val it = myAssets!!.iterator()        //根据传入的集合(旧集合)获取迭代器
        while (it.hasNext()) {          //遍历老集合
            val obj = it.next() as MyRouter       //记录每一个元素
            if (!newList.contains(obj)) {      //如果新集合中不包含旧集合中的元素
                newList.add(obj)       //将元素添加
            }
        }
        myAssets = newList
        val gson = Gson()
        try {
            val itadd = myAssets!!.iterator()        //根据传入的集合(旧集合)获取迭代器
            while (itadd.hasNext()) {          //遍历老集合
                val obj = itadd.next() as MyRouter
                insertLocalAssets(obj)
            }
            //FileUtil.saveRouterData(wallet.getAddress(), gson.toJson(myAssets));
        } catch (e: Exception) {

        } finally {

        }
    }

    /**
     * 更新某个本地路由器
     *
     * @param router
     */
    fun updateLocalAssets(router: MyRouter?) {
        var userId = FileUtil.getLocalUserId()
        if(userId.equals(""))
        {
            return
        }
        if (router == null) {
            return
        }
        val gson = Gson()
        val localRouterArrayList: ArrayList<MyRouter>
        val newRouterArrayList = ArrayList<MyRouter>()
        try {
            //开始读取sd卡的路由器数据
            var assetStr = FileUtil.readRoutersData(userId)
            if (assetStr != "") {
                localRouterArrayList = gson.fromJson<ArrayList<MyRouter>>(assetStr, object : TypeToken<ArrayList<MyRouter>>() {

                }.type)
                for (myRouter in localRouterArrayList) {

                    if (myRouter.getType() === 0)
                    {
                        if (router!!.getRouterEntity() != null && myRouter.getRouterEntity().routerId.equals(router!!.getRouterEntity().routerId)) {
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
