package com.vtempe.ios

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * РўРѕС‡РєР° РІС…РѕРґР° РґР»СЏ iOS-РїСЂРёР»РѕР¶РµРЅРёСЏ.
 * Xcode Р±СѓРґРµС‚ РІС‹Р·С‹РІР°С‚СЊ СЌС‚Сѓ С„СѓРЅРєС†РёСЋ, С‡С‚РѕР±С‹ РїРѕР»СѓС‡РёС‚СЊ root UIViewController.
 */
fun MainViewController(): UIViewController {
    initKoinIfNeeded()
    return ComposeUIViewController(
        configure = { enforceStrictPlistSanityCheck = false }
    ) {
        AppIosRoot()
    }
}

