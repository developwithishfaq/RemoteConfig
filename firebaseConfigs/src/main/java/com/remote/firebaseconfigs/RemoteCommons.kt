package com.remote.firebaseconfigs

import android.util.Log

object RemoteCommons {
    fun logConfigs(msg: String, error: Boolean = false) {
        if (error) {
            Log.e("configs", "Config:$msg")
        } else {
            Log.d("configs", "Config:$msg")
        }
    }

    fun multipleChecks(vararg key: String): String {
        var enabe = true
        key.forEach {
            if (it.isAdEnabled().not()) {
                enabe = false
            }
        }
        return enabe.toConfigString()
    }


    fun Boolean.toConfigString() = if (this) {
        "SDK_TRUE"
    } else {
        "SDK_FALSE"
    }

    fun String.isAdEnabled(default: Boolean = true) =
        SdkRemoteConfigController.getRemoteConfigBoolean(this, default)

}