package geoanalysis.geocollect

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class conexionBD private constructor(context: Context) {

    private lateinit var mRequestQueue: RequestQueue

    val requestQueue: RequestQueue
        get() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(mCtx?.applicationContext)
            }
            return mRequestQueue
        }

    init {
        mCtx = context
        mRequestQueue = requestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    companion object {
        private var mInstance: conexionBD? = null
        private var mCtx: Context? = null

        @Synchronized
        fun getInstance(context: Context): conexionBD {
            if (mInstance == null) {
                mInstance = conexionBD(context)
            }
            return mInstance as conexionBD
        }
    }


}

