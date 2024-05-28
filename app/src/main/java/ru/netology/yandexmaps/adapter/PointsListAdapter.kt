package ru.netology.yandexmaps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.entity.Point

class PointsListAdapter(private val onClick: (Point) -> Unit) :
    ListAdapter<Point, PointsListAdapter.PointViewHolder>(PointsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.point_item, parent, false)
        return PointViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        val point = getItem(position)
        holder.bind(point)
    }

    class PointViewHolder(itemView: View, val onClick: (Point) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val descriptionView: TextView = itemView.findViewById(R.id.point_description)
        private var currentPoint: Point? = null

        init {
            itemView.setOnClickListener {
                currentPoint?.let {
                    onClick(it)
                }
            }
        }

        fun bind(point: Point) {
            currentPoint = point
            descriptionView.text = point.description
        }
    }

    class PointsComparator : DiffUtil.ItemCallback<Point>() {
        override fun areItemsTheSame(oldItem: Point, newItem: Point): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Point, newItem: Point): Boolean {
            return oldItem == newItem
        }
    }
}
