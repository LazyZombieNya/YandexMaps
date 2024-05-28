package ru.netology.yandexmaps.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.map.CameraPosition
import ru.netology.yandexmaps.BuildConfig
import ru.netology.yandexmaps.databinding.FragmentMapsBinding

class MapsFragment :Fragment() {
    private lateinit var binding: FragmentMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setApiKey(savedInstanceState) // Проверяем: был ли уже ранее установлен API-ключ в приложении. Если нет - устанавливаем его.
        MapKitFactory.initialize(requireContext()) // Инициализация библиотеки для загрузки необходимых нативных библиотек.
        binding = FragmentMapsBinding.inflate(layoutInflater) // Раздуваем макет только после того, как установили API-ключ

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding =  FragmentMapsBinding.inflate(inflater, container, false)

        binding.plus.setOnClickListener {
            binding.map.mapWindow.map.move(
                CameraPosition(
                    binding.map.mapWindow.map.cameraPosition.target,
                    binding.map.mapWindow.map.cameraPosition.zoom + 1,
                    0.0f,
                    0.0f
                ),
                Animation(Animation.Type.SMOOTH, 0.3F),
                null
            )
        }
        binding.minus.setOnClickListener {
            binding.map.mapWindow.map.move(
                CameraPosition(
                    binding.map.mapWindow.map.cameraPosition.target,
                    binding.map.mapWindow.map.cameraPosition.zoom - 1,
                    0.0f,
                    0.0f
                ),
                Animation(Animation.Type.SMOOTH, 0.3F),
                null
            )
        }
        return binding.root
    }


    private fun setApiKey(savedInstanceState: Bundle?) {
        val haveApiKey = savedInstanceState?.getBoolean("haveApiKey") ?: false // При первом запуске приложения всегда false
        if (!haveApiKey) {
            MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY) // API-ключ должен быть задан единожды перед инициализацией MapKitFactory
        }
    }

    // Если Activity уничтожается (например, при нехватке памяти или при повороте экрана) - сохраняем информацию, что API-ключ уже был получен ранее
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("haveApiKey", true)
    }

    // Отображаем карты перед моментом, когда активити с картой станет видимой пользователю:
    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.map.onStart()
    }

    // Останавливаем обработку карты, когда активити с картой становится невидимым для пользователя:
    override fun onStop() {
        binding.map.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

}

