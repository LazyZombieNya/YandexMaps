package ru.netology.yandexmaps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import ru.netology.yandexmaps.databinding.FragmentMapsBinding

class MapsFragment :Fragment(){
//    private lateinit var mapView: MapView
//    private lateinit var userLocation: UserLocationLayer
//
//    //@SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        MapKitFactory.initialize(requireContext())
//        setContentView(R.layout.activity_main)
//        mapView = findViewById(R.id.map)
//    }
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val binding = FragmentMapsBinding.inflate(inflater, container, false)
//
//        mapView = binding.map.apply {
//            userLocation = MapKitFactory.getInstance().createUserLocationLayer(mapWindow)
//            userLocation.isVisible = true
//            userLocation.isHeadingEnabled = false
//            mapWindow.map.addInputListener(listener)
//        }
//        return binding.root
//    }
//    override fun onStart() {
//        super.onStart()
//        MapKitFactory.getInstance().onStart()
//        mapView.onStart()
//    }
//
//    override fun onStop() {
//        mapView.onStop()
//        MapKitFactory.getInstance().onStop()
//        super.onStop()
//    }
}

