package ie.conor.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import ie.conor.R
import ie.conor.adapters.FitnessAdapter
import ie.conor.adapters.FitnessListener
import ie.conor.models.FitnessModel
import ie.conor.utils.*
import kotlinx.android.synthetic.main.card_fitness.*
import kotlinx.android.synthetic.main.fragment_report.view.*
import org.jetbrains.anko.info

class ReportAllFragment : ReportFragment(),
    FitnessListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_report, container, false)
        activity?.title = getString(R.string.menu_report_all)

        root.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        setSwipeRefresh()

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ReportAllFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    override fun setSwipeRefresh() {
        root.swiperefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefresh.isRefreshing = true
                getAllUsersFitnessx()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getAllUsersFitnessx()
    }

    fun getAllUsersFitnessx() {
        loader = createLoader(activity!!)
        showLoader(loader, "Downloading All Users Fitnessx from Firebase")
        val fitnessxList = ArrayList<FitnessModel>()
        app.database.child("fitnessx")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Fitness error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val fitness = it.
                            getValue<FitnessModel>(FitnessModel::class.java)

                        fitnessxList.add(fitness!!)
                        root.recyclerView.adapter =
                            FitnessAdapter(fitnessxList, this@ReportAllFragment,true)
                        root.recyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()

                        app.database.child("fitnessx").removeEventListener(this)
                    }
                }
            })
    }
}
