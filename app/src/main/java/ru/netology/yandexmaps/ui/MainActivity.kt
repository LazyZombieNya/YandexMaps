package ru.netology.yandexmaps.ui

import AllPointsFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.entity.Point

class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    if (savedInstanceState == null) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mapFragment, MapsFragment())
            .commit()
    }
}

    fun navigateToAllPoints() {
        replaceFragment(AllPointsFragment())
    }

    fun navigateToMapWithPoint(point: Point) {
        val fragment = MapsFragment()
        val bundle = Bundle()
        bundle.putParcelable("point", point)
        fragment.arguments = bundle
        replaceFragment(fragment)
    }

            private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mapFragment, fragment)
            .addToBackStack(null)
            .commit()
    }
}