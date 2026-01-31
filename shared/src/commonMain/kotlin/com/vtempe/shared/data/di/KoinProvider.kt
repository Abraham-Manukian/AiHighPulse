package com.vtempe.shared.data.di

import org.koin.core.Koin

/**
 * Р”Р»СЏ iOS: РІ Kotlin/Native РЅРµ РІСЃРµРіРґР° СѓРґРѕР±РЅРѕ/РґРѕСЃС‚СѓРїРЅРѕ РґРѕСЃС‚Р°РІР°С‚СЊ С‚РµРєСѓС‰РёР№ Koin РёР· GlobalContext.
 * РџРѕСЌС‚РѕРјСѓ СЃРѕС…СЂР°РЅСЏРµРј СЃСЃС‹Р»РєСѓ РЅР° Koin РїСЂРё РёРЅРёС†РёР°Р»РёР·Р°С†РёРё (СЃРј. app-ios).
 */
object KoinProvider {
    var koin: Koin? = null
}


