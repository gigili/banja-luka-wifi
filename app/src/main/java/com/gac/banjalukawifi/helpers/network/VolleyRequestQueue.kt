package com.gac.banjalukawifi.helpers.network

import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import com.gac.banjalukawifi.helpers.AppInstance

class VolleyRequestQueue private constructor() {
    val requestQueue: RequestQueue = Volley.newRequestQueue(AppInstance.appContext)
    private val imageLoader: ImageLoader

    init {
        imageLoader = ImageLoader(this.requestQueue, object : ImageLoader.ImageCache {
            private val mCache = LruCache<String, Bitmap>(10)
            override fun putBitmap(url: String, bitmap: Bitmap) {
                mCache.put(url, bitmap)
            }

            override fun getBitmap(url: String): Bitmap {
                return mCache.get(url)
            }
        })
    }

    companion object {
        private var mInstance: VolleyRequestQueue? = null

        val instance: VolleyRequestQueue
            get() {
                if (mInstance == null) {
                    mInstance =
                        VolleyRequestQueue()
                }
                return mInstance!!
            }
    }

}