package com.remote.firebaseconfigs

import androidx.annotation.XmlRes
import com.remote.firebaseconfigs.listeners.SdkConfigListener


fun SdkRemoteConfigController.fetch(
    @XmlRes xml: Int,
    onGo: () -> Unit,
    timeOutInSec: Int = 8,
    onUpdate: () -> Unit
) {
    fetchRemoteConfig(
        defaultXml = xml,
        callback = object : SdkConfigListener {
            override fun onSuccess() {}
            override fun onFailure(error: String) {}
            override fun onDismiss() {
                onGo.invoke()
            }
        },
        fetchOutTimeInSeconds = timeOutInSec.toLong(),
        handlerDelayInSeconds = timeOutInSec.toLong(),
        onUpdate = {
            onUpdate.invoke()
        }
    )
}