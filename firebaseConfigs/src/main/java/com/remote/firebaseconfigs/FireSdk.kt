package com.remote.firebaseconfigs

import android.content.Context
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics

object FireSdk {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firebaseCrashlytics: FirebaseCrashlytics

    fun initialize(context: Context) {
        FirebaseApp.initializeApp(context)
    }

    fun sendEvent(context: Context, message: String, bundle: Bundle = Bundle()) {
        initFirebaseAnalytics(context)
        firebaseAnalytics.logEvent(message, bundle)
    }

    fun setUserId(context: Context, userId: String) {
        initFirebaseAnalytics(context)
        firebaseAnalytics.setUserId(userId);
    }

    fun setScreenName(screenName: String, screenClass: String, context: Context) {
        initFirebaseAnalytics(context)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
    }

    fun sendException(exception: Exception) {
        if (FireSdk::firebaseCrashlytics.isInitialized.not()) {
            firebaseCrashlytics = FirebaseCrashlytics.getInstance()
        }
        firebaseCrashlytics.recordException(exception)
    }

    private fun initFirebaseAnalytics(context: Context) {
        if (FireSdk::firebaseAnalytics.isInitialized.not()) {
            firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        }
    }
}