package ru.netology.yandexmaps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.yandexmaps.db.AppDatabase
import ru.netology.yandexmaps.entity.Point

class PointViewModel(application: Application) : AndroidViewModel(application) {
    private val pointDao = AppDatabase.getDatabase(application).pointDao()
    val allPoints: LiveData<List<Point>> = pointDao.getAllPoints()

    fun insertPoint(point: Point) = viewModelScope.launch {
        pointDao.insertPoint(point)
    }

    fun updatePoint(point: Point) = viewModelScope.launch {
        pointDao.updatePoint(point)
    }

    fun deletePoint(point: Point) = viewModelScope.launch {
        pointDao.deletePoint(point)
    }
}
