package ru.netology.yandexmaps.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.adapter.PointsListAdapter
import ru.netology.yandexmaps.viewmodel.PointViewModel

class PointsListActivity : AppCompatActivity() {

    private val pointViewModel: PointViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_points_list)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val adapter = PointsListAdapter { point ->
            val intent = Intent().apply {
                putExtra("latitude", point.latitude)
                putExtra("longitude", point.longitude)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        pointViewModel.allPoints.observe(this, { points ->
            points?.let { adapter.submitList(it) }
        })
    }
}
