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
                    listener?.onSuccess()
                    responseCallBack()
                }?.addOnCanceledListener {
                    listener?.onFailure("addOnCanceledListener called")
                    responseCallBack()
                }?.addOnFailureListener {
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
        } catch (_: Exception) {
            logConfigs("Firebase is not initialized", true)
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

fun  main(){
    val results = calculatePayments(19221.0,6990.0,28250.0)
    println(results)
}
fun calculatePayments(x: Double, y: Double, z: Double): Map<String, Map<String, Double>> {
    // Calculate the amount each person spent on others
    val aliSpentOnOthers = x - (x / 3)
    val basitSpentOnOthers = y - (y / 3)
    val ishfaqSpentOnOthers = z - (z / 3)

    // Calculate total amount spent on others and the share each person should pay
    val totalSpentOnOthers = aliSpentOnOthers + basitSpentOnOthers + ishfaqSpentOnOthers
    val eachPersonShare = totalSpentOnOthers / 3

    // Calculate how much each person owes or is owed
    val aliOwes = eachPersonShare - aliSpentOnOthers
    val basitOwes = eachPersonShare - basitSpentOnOthers
    val ishfaqOwes = eachPersonShare - ishfaqSpentOnOthers

    // Return the amount each person owes the others
    return mapOf(
        "Ali" to mapOf("Basit" to -aliOwes / 2, "Ishfaq" to -aliOwes / 2),
        "Basit" to mapOf("Ali" to -basitOwes / 2, "Ishfaq" to -basitOwes / 2),
        "Ishfaq" to mapOf("Ali" to -ishfaqOwes / 2, "Basit" to -ishfaqOwes / 2)
    )
}
