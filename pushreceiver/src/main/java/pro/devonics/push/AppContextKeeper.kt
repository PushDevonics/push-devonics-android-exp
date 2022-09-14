package pro.devonics.push

import android.annotation.SuppressLint
import android.content.Context

class AppContextKeeper {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private lateinit var mContext: Context

        fun setContext(appContext: Context) {
            mContext = appContext
        }

        fun getContext(): Context {
            return mContext
        }
    }

}