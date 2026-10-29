package com.example.core.config

import com.example.BuildConfig

object AppEnvironment {
    val isDemo: Boolean
        get() = BuildConfig.IS_DEMO && BuildConfig.FLAVOR_MODE == "demo"

    val isProd: Boolean
        get() = BuildConfig.IS_PROD || BuildConfig.FLAVOR_MODE == "prod"
}
