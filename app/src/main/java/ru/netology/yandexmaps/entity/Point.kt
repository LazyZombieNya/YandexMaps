package ru.netology.yandexmaps.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "points")
//data class Point(
//    @PrimaryKey(autoGenerate = true)
//    var id: Long = 0,
//    val latitude: Double,
//    val longitude: Double,
//    var title: String,
//    var description: String
//)

@Entity(tableName = "points")
data class Point(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var latitude: Double,
    var longitude: Double,
    var title: String,
    var description: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(title)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Point> {
        override fun createFromParcel(parcel: Parcel): Point {
            return Point(parcel)
        }

        override fun newArray(size: Int): Array<Point?> {
            return arrayOfNulls(size)
        }
    }
}
