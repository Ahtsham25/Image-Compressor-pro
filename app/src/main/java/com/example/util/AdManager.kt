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
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdManager {
    private const val TAG = "AdManager"
    private const val PREFS_NAME = "admob_settings"
    private const val KEY_CUSTOM_BANNER = "key_custom_banner"
    private const val KEY_CUSTOM_INTERSTITIAL = "key_custom_interstitial"
    private const val KEY_CUSTOM_REWARDED = "key_custom_rewarded"
    private const val KEY_CUSTOM_APP_ID = "key_custom_app_id"
    private const val KEY_ADS_ENABLED = "key_ads_enabled"

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false

    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading = false

    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            MobileAds.initialize(context) {
                isInitialized = true
                Log.d(TAG, "Google Mobile Ads SDK Initialized with user Google IDs")
                preloadInterstitial(context)
                preloadRewarded(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing MobileAds", e)
        }
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isAdsEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_ADS_ENABLED, true)
    }

    fun setAdsEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_ADS_ENABLED, enabled).apply()
    }

    // Google Ad IDs getters and setters
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

    fun getCustomRewardedId(context: Context): String {
        return getPrefs(context).getString(KEY_CUSTOM_REWARDED, "") ?: ""
    }

    fun setCustomRewardedId(context: Context, id: String) {
        getPrefs(context).edit().putString(KEY_CUSTOM_REWARDED, id.trim()).apply()
        rewardedAd = null
        preloadRewarded(context)
    }

    fun getCustomAppId(context: Context): String {
        return getPrefs(context).getString(KEY_CUSTOM_APP_ID, "") ?: ""
    }

    fun setCustomAppId(context: Context, id: String) {
        getPrefs(context).edit().putString(KEY_CUSTOM_APP_ID, id.trim()).apply()
    }

    // Directly use user's Google Ad unit IDs - permanently no test IDs
    fun getEffectiveBannerId(context: Context): String {
        return getCustomBannerId(context)
    }

    fun getEffectiveInterstitialId(context: Context): String {
        return getCustomInterstitialId(context)
    }

    fun getEffectiveRewardedId(context: Context): String {
        return getCustomRewardedId(context)
    }

    // Interstitial Preloading & Showing
    fun preloadInterstitial(context: Context) {
        val adUnitId = getEffectiveInterstitialId(context)
        if (adUnitId.isBlank()) {
            Log.d(TAG, "No custom Google Interstitial Ad ID configured; skipping load")
            return
        }
        if (interstitialAd != null || isInterstitialLoading) return

        isInterstitialLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    Log.d(TAG, "User Google Interstitial Ad successfully loaded ($adUnitId)")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
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
            preloadInterstitial(activity)
            onAdDismissed()
        }
    }

    fun isInterstitialAdReady(): Boolean {
        return interstitialAd != null
    }

    // Rewarded Ad Preloading & Showing
    fun preloadRewarded(context: Context) {
        val adUnitId = getEffectiveRewardedId(context)
        if (adUnitId.isBlank()) {
            Log.d(TAG, "No custom Google Rewarded Ad ID configured; skipping load")
            return
        }
        if (rewardedAd != null || isRewardedLoading) return

        isRewardedLoading = true
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            context,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isRewardedLoading = false
                    Log.d(TAG, "User Google Rewarded Ad successfully loaded ($adUnitId)")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    rewardedAd = null
                    isRewardedLoading = false
                    Log.w(TAG, "Rewarded Ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    fun isRewardedAdReady(): Boolean {
        return rewardedAd != null
    }

    fun showRewarded(
        activity: Activity,
        onUserEarnedReward: () -> Unit,
        onAdDismissed: () -> Unit
    ) {
        val ad = rewardedAd
        if (ad != null) {
            var rewardEarned = false

            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    preloadRewarded(activity)
                    if (rewardEarned) {
                        onUserEarnedReward()
                    }
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    rewardedAd = null
                    preloadRewarded(activity)
                    onUserEarnedReward()
                    onAdDismissed()
                }
            }

            ad.show(activity, OnUserEarnedRewardListener { _ ->
                rewardEarned = true
                Log.d(TAG, "User earned reward for watching ad")
            })
        } else {
            preloadRewarded(activity)
            onUserEarnedReward()
            onAdDismissed()
        }
    }
}
