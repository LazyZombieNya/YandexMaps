import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.adapter.PointsListAdapter
import ru.netology.yandexmaps.dao.PointDao
import ru.netology.yandexmaps.db.AppDatabase
import ru.netology.yandexmaps.entity.Point
import ru.netology.yandexmaps.ui.MainActivity

class AllPointsFragment : Fragment() {

    private lateinit var pointDao: PointDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PointsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_points_list , container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PointsListAdapter { point -> onPointClick(point) }
        recyclerView.adapter = adapter

        pointDao = AppDatabase.getDatabase(requireContext()).pointDao()

        loadPoints()
    }

    private fun loadPoints() {
        lifecycleScope.launch {
            val points = pointDao.getAllPoints()
            adapter.submitList(points)
        }
    }

    private fun onPointClick(point: Point) {
        (activity as MainActivity).navigateToMapWithPoint(point)
    }
}
