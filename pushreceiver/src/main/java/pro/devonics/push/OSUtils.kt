package pro.devonics.push

import android.content.pm.PackageManager

class OSUtils {

    companion object {
        fun sleep(ms: Int) {
            try {
                Thread.sleep(ms.toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }

    val UNINITIALIZABLE_STATUS = -999

    var MAX_NETWORK_REQUEST_ATTEMPT_COUNT = 3
    val NO_RETRY_NETWROK_REQUEST_STATUS_CODES = intArrayOf(401, 402, 403, 404, 410)

    private val appContext = AppContextKeeper.getContext()

    private fun packageInstalledAndEnabled(packageName: String): Boolean {
        return try {
            val pm = appContext.packageManager
            val info = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA)
            info.applicationInfo.enabled
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }


}