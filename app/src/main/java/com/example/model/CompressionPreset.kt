package com.example.model

enum class CompressionPreset(
    val titleEn: String,
    val titleUr: String,
    val descriptionEn: String,
    val descriptionUr: String,
    val defaultQuality: Int,
    val maxDimension: Int,
    val estimatedReduction: String
) {
    LOW(
        titleEn = "Low Compression",
        titleUr = "لو کمپریشن (کم دباؤ)",
        descriptionEn = "Best visual quality with minimal compression (~20-40% savings)",
        descriptionUr = "بہترین کوالٹی اور تفصیلات محفوظ، ہلکی فائل بچت",
        defaultQuality = 92,
        maxDimension = 3840,
        estimatedReduction = "20% - 40%"
    ),
    STANDARD(
        titleEn = "Standard",
        titleUr = "سٹینڈرڈ کمپریشن (معیاری)",
        descriptionEn = "Optimal balance of clarity and file size reduction (~60-75% savings)",
        descriptionUr = "کوالٹی اور سائز میں متوازن، روزمرہ شیئرنگ کے لیے بہترین",
        defaultQuality = 75,
        maxDimension = 2048,
        estimatedReduction = "60% - 75%"
    ),
    HIGH(
        titleEn = "High Compression",
        titleUr = "ہائی کمپریشن (زیادہ دباؤ)",
        descriptionEn = "Maximum storage reduction, smallest file size (~80-92% savings)",
        descriptionUr = "انتہائی چھوٹا سائز، تیز رفتار انٹرنیٹ اور واٹس ایپ شیئرنگ کے لیے",
        defaultQuality = 45,
        maxDimension = 1280,
        estimatedReduction = "80% - 92%"
    ),
    CUSTOM(
        titleEn = "Custom",
        titleUr = "کسٹم (اپنی مرضی)",
        descriptionEn = "Manually adjust quality percentage and image dimension scaling",
        descriptionUr = "اپنی مرضی سے کوالٹی اور ڈائمینشنز کا سائز منتخب کریں",
        defaultQuality = 80,
        maxDimension = 1920,
        estimatedReduction = "Custom"
    )
}

enum class CompressionFormat(
    val extension: String,
    val mimeType: String,
    val displayName: String
) {
    JPEG("jpg", "image/jpeg", "JPEG (Photos)"),
    WEBP("webp", "image/webp", "WebP (Modern & Small)"),
    PNG("png", "image/png", "PNG (Lossless)")
}
