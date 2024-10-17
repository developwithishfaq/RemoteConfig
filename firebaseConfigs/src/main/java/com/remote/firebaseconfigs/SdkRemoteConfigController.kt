package com.remote.firebaseconfigs

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.XmlRes
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.remote.firebaseconfigs.RemoteCommons.logConfigs
import com.remote.firebaseconfigs.listeners.SdkConfigListener

object SdkRemoteConfigController {

    private var remoteConfig: FirebaseRemoteConfig? = null
    private var listener: SdkConfigListener? = null

    private var handler: Handler = Handler(Looper.getMainLooper())
    private var isHandlerRunning = false


    private var runnable = {
        if (isHandlerRunning) {
            logConfigs("")
            responseCallBack()
        }
    }


    fun getRemoteConfigString(key: String, def: String = ""): String {
        val values = remoteConfig?.getString(key) ?: def
        logConfig(key, values)
        return values
    }

    fun getRemoteConfigLong(key: String, def: Long = -1): Long {
        val values = remoteConfig?.getLong(key) ?: def
        logConfig(key, values.toString())
        return values
    }

    fun logConfig(key: String, value: String) {
        Log.d("configss", "Key=$key, value=$value ")
        Log.d("configss", "-----------------------------------")
    }

    fun getRemoteConfigBoolean(key: String, def: Boolean = false): Boolean {
        when (key) {
            "SDK_FALSE" -> {
                return false
            }

            "SDK_TRUE" -> {
                return true
            }

            else -> {
                val values = remoteConfig?.getBoolean(key) ?: def
                logConfig(key, values.toString())
                return values
            }
        }
    }

    fun fetchRemoteConfig(
        @XmlRes defaultXml: Int,
        callback: SdkConfigListener?,
        fetchOutTimeInSeconds: Long = 8,
        handlerDelayInSeconds: Long = 8,
        onUpdate: () -> Unit,
    ) {
        try {
            if (callback != null) {
                startHandler(handlerDelayInSeconds)
            }
            listener = callback
            remoteConfig = FirebaseRemoteConfig.getInstance()
            logConfigs(
                "Remote Config Is Ok ${remoteConfig != null}",
                error = remoteConfig == null
            )
            val remoteConfigBuilder = FirebaseRemoteConfigSettings.Builder()
            remoteConfigBuilder.fetchTimeoutInSeconds = fetchOutTimeInSeconds
            if (com.google.firebase.remoteconfig.BuildConfig.DEBUG) {
                remoteConfigBuilder.setMinimumFetchIntervalInSeconds(0)
            }
            remoteConfig?.setConfigSettingsAsync(
                remoteConfigBuilder.build()
            )
            remoteConfig?.setDefaultsAsync(defaultXml)
            remoteConfig?.fetchAndActivate()
                ?.addOnCompleteListener {
                    logConfigs("addOnCompleteListener called")
                    listener?.onSuccess()
                    responseCallBack()
                }?.addOnCanceledListener {
                    logConfigs("addOnCanceledListener called")
                    listener?.onFailure("addOnCanceledListener called")
                    responseCallBack()
                }?.addOnFailureListener {
                    logConfigs("addOnFailureListener called")
                    listener?.onFailure(it.message ?: "addOnFailureListener called")
                    responseCallBack()
                }
            remoteConfig?.addOnConfigUpdateListener(object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) {
                    onUpdate.invoke()
                }

                override fun onError(error: FirebaseRemoteConfigException) {

                }
            })
        } catch (e: Exception) {
            logConfigs("Firebase is not initialized: ${e.message}", true)
            listener?.onFailure("Firebase is not initialized")
            responseCallBack()
        }
    }

    private fun startHandler(time: Long) {
        if (isHandlerRunning.not()) {
            isHandlerRunning = true
            val actualTime = time * 1000
            logConfigs("Remote Config Time Started Of Millies=$actualTime")
            handler.postDelayed(runnable, actualTime)
        }
    }

    private fun responseCallBack() {
        listener?.onDismiss()
        listener = null
        stopHandler()
    }

    private fun stopHandler() {
        if (isHandlerRunning) {
            handler.removeCallbacks(runnable)
            handler.removeCallbacksAndMessages(null)
        }
    }
}

