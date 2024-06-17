package ru.netology.yandexmaps.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.launch
import ru.netology.yandexmaps.BuildConfig
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.dao.PointDao
import ru.netology.yandexmaps.databinding.FragmentMapsBinding
import ru.netology.yandexmaps.db.AppDatabase
import ru.netology.yandexmaps.entity.Point

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
        MapKitFactory.initialize(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //MapKitFactory.initialize(requireContext())
        return inflater.inflate(R.layout.fragment_maps, container, false)
        val binding =  FragmentMapsBinding.inflate(inflater, container, false)
//        val listener = object : InputListener {
//            override fun onMapTap(map: com.yandex.mapkit.map.Map, point: com.yandex.mapkit.geometry.Point) {
//                createMarker(point.latitude, point.longitude)
//                loadPoints()
//            }
//
//            override fun onMapLongTap(p0: com.yandex.mapkit.map.Map, p1: com.yandex.mapkit.geometry.Point) {
//                TODO("Not yet implemented")
//            }
//
//        }
//        binding.map.mapWindow.map.addInputListener(listener)

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

        val buttonViewAllPoints = view.findViewById<Button>(R.id.button_view_all_points)
        buttonViewAllPoints.setOnClickListener {
            (activity as MainActivity).navigateToAllPoints()
        }

        arguments?.getParcelable<Point>("point")?.let { point ->
            mapView.map.move(
                CameraPosition(com.yandex.mapkit.geometry.Point(point.latitude, point.longitude), 14.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        }

        // Listener для кликов на карте
        mapView.map.addInputListener(object : InputListener {


            override fun onMapTap(p0: Map, p1: com.yandex.mapkit.geometry.Point) {
                showEditDialog(p1.latitude, p1.longitude)
            }

            override fun onMapLongTap(p0: Map, p1: com.yandex.mapkit.geometry.Point) {
                TODO("Not yet implemented")
            }
        })

        // Загрузка точек из базы данных
        loadPoints()
    }

    private fun showEditDialog(latitude: Double, longitude: Double, pointData: Point? = null) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_point, null)
        val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val editTextDescription = dialogView.findViewById<EditText>(R.id.editTextDescription)

        if (pointData != null) {
            editTextTitle.setText(pointData.title)
            editTextDescription.setText(pointData.description)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(if (pointData == null) "Создать точку" else "Редактировать точку")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val title = editTextTitle.text.toString()
                val description = editTextDescription.text.toString()
                if (pointData == null) {
                    createMarker(latitude, longitude, title, description)
                } else {
                    updateMarker(pointData, title, description)
                }
            }
            .setNegativeButton("Отмена", null)
            .setNeutralButton("Удалить") { _, _ ->
                pointData?.let { deleteMarker(it) }
            }
            .create()

        dialog.show()
    }
    private fun createMarker(latitude: Double, longitude: Double, title: String, description: String) {
        val placemark = mapView.map.mapObjects.addPlacemark(
            com.yandex.mapkit.geometry.Point(
                latitude,
                longitude
            )
        )
        placemark.setIcon(ImageProvider.fromResource(requireContext(), R.drawable.baseline_person_pin_circle_24))

        placemark.addTapListener(MapObjectTapListener { mapObject, _ ->
            val pointData = points.find { it.latitude == latitude && it.longitude == longitude }
            if (pointData != null) {
                showEditDialog(latitude, longitude, pointData)
            }
            true
        })
    // Добавление новой точки в список и базу данных
        val newPoint = Point(latitude = latitude, longitude = longitude, title = title, description = description)
        lifecycleScope.launch {
            val id = pointDao.insert(newPoint)
            newPoint.id = id
            points.add(newPoint)
        }

        Toast.makeText(requireContext(), "Маркер добавлен", Toast.LENGTH_SHORT).show()
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
                    showEditDialog(point.latitude, point.longitude, point)
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
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putBoolean("haveApiKey", true)
//        outState.putParcelableArrayList("points", ArrayList(points))
//    }

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
    private fun updateMarker(pointData: Point, title: String, description: String) {
        pointData.title = title
        pointData.description = description
        lifecycleScope.launch {
            pointDao.update(pointData)
            Toast.makeText(requireContext(), "Точка обновлена", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteMarker(pointData: Point) {
        lifecycleScope.launch {
            pointDao.delete(pointData)
            points.remove(pointData)
            mapView.map.mapObjects.clear()
            loadPoints()
            Toast.makeText(requireContext(), "Точка удалена", Toast.LENGTH_SHORT).show()
        }
    }



}

