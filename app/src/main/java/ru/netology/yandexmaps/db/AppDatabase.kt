package ru.netology.yandexmaps.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.yandexmaps.dao.PointDao
import ru.netology.yandexmaps.entity.Point

@Database(entities = [Point::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pointDao(): PointDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "points_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
