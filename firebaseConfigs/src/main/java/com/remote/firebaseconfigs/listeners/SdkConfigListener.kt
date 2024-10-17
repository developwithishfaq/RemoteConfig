package com.remote.firebaseconfigs.listeners

interface SdkConfigListener {
    fun onSuccess()
    fun onFailure(error: String)
    fun onDismiss()
}