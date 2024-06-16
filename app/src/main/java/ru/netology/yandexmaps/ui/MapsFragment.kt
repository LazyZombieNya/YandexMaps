package ru.netology.yandexmaps.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.connectivity.internal.ConnectivitySubscription
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.launch
import ru.netology.yandexmaps.BuildConfig
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.dao.PointDao
import ru.netology.yandexmaps.databinding.FragmentMapsBinding
import ru.netology.yandexmaps.db.AppDatabase
import ru.netology.yandexmaps.entity.Point
import ru.netology.yandexmaps.viewmodel.PointViewModel

class MapsFragment :Fragment() {
    private lateinit var binding: FragmentMapsBinding
    private lateinit var mapView: MapView
    private lateinit var database: AppDatabase
    private lateinit var pointDao: PointDao
    private val points = mutableListOf<Point>()  // Список для хранения точек

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setApiKey(savedInstanceState) // Проверяем: был ли уже ранее установлен API-ключ в приложении. Если нет - устанавливаем его.
        binding = FragmentMapsBinding.inflate(layoutInflater) // Раздуваем макет только после того, как установили API-ключ


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        MapKitFactory.initialize(requireContext())
        return inflater.inflate(R.layout.fragment_maps, container, false)
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.map)
        database = AppDatabase.getDatabase(requireContext())
        pointDao = database.pointDao()

        // Отключаем жесты поворота карты
        mapView.map.isRotateGesturesEnabled = false

        // Listener для кликов на карте
        mapView.map.addTapListener ( point, _ ->
            createMarker(point.latitude, point.longitude)
            true
        )

        // Загрузка точек из базы данных
        loadPoints()
    }


    private fun createMarker(latitude: Double, longitude: Double) {
        val placemark = mapView.map.mapObjects.addPlacemark(
            com.yandex.mapkit.geometry.Point(
                latitude,
                longitude
            )
        )
        placemark.setIcon(ImageProvider.fromResource(requireContext(), R.drawable.baseline_person_pin_circle_24))

        // Слушатель для кликов на маркере
        placemark.addTapListener(MapObjectTapListener { mapObject, _ ->
            val pointData = points.find { it.latitude == latitude && it.longitude == longitude }
            if (pointData != null) {
                editMarker(pointData, mapObject as PlacemarkMapObject)
            }
            true
        })

        // Добавление новой точки в список и базу данных
        val newPoint = Point(latitude = latitude, longitude = longitude, title = "Новая точка", description = "Описание")
        lifecycleScope.launch {
            val id = pointDao.insert(newPoint)
            newPoint.id = id
            points.add(newPoint)
        }

        Toast.makeText(requireContext(), "Маркер добавлен", Toast.LENGTH_SHORT).show()
    }

    private fun editMarker(pointData: Point, placemark: PlacemarkMapObject) {
        // Здесь можно реализовать диалог для редактирования точки
        // Для простоты используем тост для демонстрации
        Toast.makeText(requireContext(), "Редактирование точки: ${pointData.title}", Toast.LENGTH_SHORT).show()
    }

    private fun loadPoints() {
        lifecycleScope.launch {
            val loadedPoints = pointDao.getAllPoints()
            points.addAll(loadedPoints)

            for (point in points) {
                val placemark = mapView.map.mapObjects.addPlacemark(
                    com.yandex.mapkit.geometry.Point(
                        point.latitude,
                        point.longitude
                    )
                )
                placemark.setIcon(ImageProvider.fromResource(requireContext(), R.drawable.baseline_person_pin_circle_24))
                placemark.addTapListener(MapObjectTapListener { mapObject, _ ->
                    editMarker(point, mapObject as PlacemarkMapObject)
                    true
                })
            }
        }
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

