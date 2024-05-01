package ru.netology.yandexmaps.application

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import ru.netology.yandexmaps.BuildConfig

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }
}