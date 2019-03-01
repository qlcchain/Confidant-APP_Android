package com.stratagile.pnrouter.data.api

import com.stratagile.pnrouter.entity.BaseBackA
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*


/**
 * Created by hu on 2017/5/16.
 */

interface HttpApi {
    @POST(API.url_post_file)
    @Multipart
    //@Part("filename") map: RequestBody,
    //@Part head : MultipartBody.Part
    fun upLoad(@Part file: MultipartBody.Part): Observable<BaseBackA>
}
