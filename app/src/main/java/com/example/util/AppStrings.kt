package com.example.util

import com.example.model.AppLanguage

object AppStrings {

    fun appTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "امیج کمپریسر"
        AppLanguage.ENGLISH -> "Image Compressor"
    }

    fun selectImagePrompt(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "تصویر منتخب کریں"
        AppLanguage.ENGLISH -> "Select Image"
    }

    fun selectAnotherImage(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "نئی تصویر منتخب کریں"
        AppLanguage.ENGLISH -> "Choose Another"
    }

    fun compressionModesTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "کمپریشن لیول (3 ورژن)"
        AppLanguage.ENGLISH -> "Compression Preset (3 Modes)"
    }

    fun standard(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "سٹینڈرڈ"
        AppLanguage.ENGLISH -> "Standard"
    }

    fun high(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "ہائی کمپریشن"
        AppLanguage.ENGLISH -> "High Compression"
    }

    fun low(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "لو کمپریشن"
        AppLanguage.ENGLISH -> "Low Compression"
    }

    fun custom(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "کسٹم"
        AppLanguage.ENGLISH -> "Custom"
    }

    fun compressButton(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "ابھی کمپریس کریں"
        AppLanguage.ENGLISH -> "Compress Now"
    }

    fun compressing(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "کمپریس ہو رہا ہے..."
        AppLanguage.ENGLISH -> "Compressing..."
    }

    fun originalSize(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "اصل سائز"
        AppLanguage.ENGLISH -> "Original Size"
    }

    fun compressedSize(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "نیا کمپریسڈ سائز"
        AppLanguage.ENGLISH -> "Compressed Size"
    }

    fun savedSize(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "بچت ہوئی"
        AppLanguage.ENGLISH -> "Space Saved"
    }

    fun saveToGallery(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "گیلری میں محفوظ کریں"
        AppLanguage.ENGLISH -> "Save to Gallery"
    }

    fun shareImage(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "شیئر کریں"
        AppLanguage.ENGLISH -> "Share Image"
    }

    fun compareSlider(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "فرق دیکھیں (پہلے / بعد)"
        AppLanguage.ENGLISH -> "Before / After Slider"
    }

    fun historyTab(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "ہسٹری"
        AppLanguage.ENGLISH -> "History"
    }

    fun compressorTab(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "کمپریسر"
        AppLanguage.ENGLISH -> "Compressor"
    }

    fun settingsTab(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "سیٹنگز و اشتہارات"
        AppLanguage.ENGLISH -> "Settings & Ads"
    }

    fun adsConfigTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "ایڈز کنفیگریشن (AdMob)"
        AppLanguage.ENGLISH -> "Ad Settings & IDs"
    }

    fun adsBannerTest(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "اسپانسرڈ اشتہار (Ad ID: Active)"
        AppLanguage.ENGLISH -> "Sponsored Ad (Active Banner)"
    }

    fun savedSuccess(lang: AppLanguage) = when (lang) {
        AppLanguage.URDU -> "تصویر کامیابی سے گیلری میں محفوظ ہو گئی!"
        AppLanguage.ENGLISH -> "Image saved successfully to Pictures/ImageCompressor!"
    }
}
