package com.example.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdManager {
    private const val TAG = "AdManager"
    private const val PREFS_NAME = "admob_settings"
    private const val KEY_TEST_MODE = "key_test_mode"
    private const val KEY_CUSTOM_BANNER = "key_custom_banner"
    private const val KEY_CUSTOM_INTERSTITIAL = "key_custom_interstitial"

    // Official Google Test Ad Unit IDs
    const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"

    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false
    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            MobileAds.initialize(context) {
                isInitialized = true
                Log.d(TAG, "Google Mobile Ads SDK Initialized")
                preloadInterstitial(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing MobileAds", e)
        }
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isTestModeEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_TEST_MODE, true)
    }

    fun setTestModeEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_TEST_MODE, enabled).apply()
        // Reload interstitial ad on mode change
        interstitialAd = null
        preloadInterstitial(context)
    }

    fun getCustomBannerId(context: Context): String {
        return getPrefs(context).getString(KEY_CUSTOM_BANNER, "") ?: ""
    }

    fun setCustomBannerId(context: Context, id: String) {
        getPrefs(context).edit().putString(KEY_CUSTOM_BANNER, id.trim()).apply()
    }

    fun getCustomInterstitialId(context: Context): String {
        return getPrefs(context).getString(KEY_CUSTOM_INTERSTITIAL, "") ?: ""
    }

    fun setCustomInterstitialId(context: Context, id: String) {
        getPrefs(context).edit().putString(KEY_CUSTOM_INTERSTITIAL, id.trim()).apply()
        interstitialAd = null
        preloadInterstitial(context)
    }

    fun getEffectiveBannerId(context: Context): String {
        val testMode = isTestModeEnabled(context)
        val customId = getCustomBannerId(context)
        return if (!testMode && customId.isNotBlank()) customId else TEST_BANNER_ID
    }

    fun getEffectiveInterstitialId(context: Context): String {
        val testMode = isTestModeEnabled(context)
        val customId = getCustomInterstitialId(context)
        return if (!testMode && customId.isNotBlank()) customId else TEST_INTERSTITIAL_ID
    }

    fun preloadInterstitial(context: Context) {
        if (interstitialAd != null || isAdLoading) return

        isAdLoading = true
        val adUnitId = getEffectiveInterstitialId(context)
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isAdLoading = false
                    Log.d(TAG, "Interstitial Ad successfully loaded ($adUnitId)")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isAdLoading = false
                    Log.w(TAG, "Interstitial Ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, onAdDismissed: () -> Unit) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    preloadInterstitial(activity)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    interstitialAd = null
                    preloadInterstitial(activity)
                    onAdDismissed()
                }
            }
            ad.show(activity)
        } else {
            // If ad not available, proceed immediately
            preloadInterstitial(activity)
            onAdDismissed()
        }
    }
}
