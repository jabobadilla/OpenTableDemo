package com.bobadilla.opentabledemo.connection.controller

import android.os.Handler
import android.os.Looper
import com.bobadilla.opentabledemo.common.Singleton
import com.bobadilla.opentabledemo.common.Singleton.getFragmentManager
import com.bobadilla.opentabledemo.connection.OkHttpRequest
import com.bobadilla.opentabledemo.common.CommonFunctions
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object ConnectionController {

    private var coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var JSONResponse: JSONObject? = null

    fun callOpenTableAsync(url: String): JSONObject {

            Singleton.showLoadDialog(getFragmentManager())

            var mHandler = Handler(Looper.getMainLooper());

            var client = OkHttpClient()
            var request = OkHttpRequest(client)

            request.GET(url, object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    println("Request Failure.")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    mHandler.post {
                        try {
                            JSONResponse = JSONObject(responseData)
                            println(JSONResponse)
                            println("Request Successful!!")
                        } catch (e: JSONException) {
                            Singleton.dissmissLoad()
                            e.printStackTrace()
                            CommonFunctions.displayConnectionProblemMessage()
                        }
                    }
                }

            })

            return JSONResponse ?: JSONObject()
        }

    suspend fun callOpenTableSync(fragmentLoad: Boolean, url: String): Deferred<JSONObject> =
        coroutineScope.async(start = CoroutineStart.LAZY) {

            if (fragmentLoad) {
                Singleton.showLoadDialog(getFragmentManager())
            }

            var client = OkHttpClient()

            var request: Request = Request.Builder().url(url).build()
            var call: Call = client.newCall(request)

            try {
                var response: Response = call.execute()
                val responseData = response.body?.string()
                JSONResponse = JSONObject(responseData)
                println(JSONResponse)
                println("Request Successful!!")
            } catch (e: JSONException) {
                Singleton.dissmissLoad()
                e.printStackTrace()
                CommonFunctions.displayConnectionProblemMessage()
            }

            return@async JSONResponse ?: JSONObject()
        }

}