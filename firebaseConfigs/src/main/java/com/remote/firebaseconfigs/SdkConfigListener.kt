package com.remote.firebaseconfigs

interface SdkConfigListener {
    fun onSuccess()
    fun onUpdate()
    fun onFailure(error: String)
    fun onDismiss()

}