package ie.conor.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import ie.conor.R
import ie.conor.main.FitnessApp
import ie.conor.models.FitnessModel
import ie.conor.utils.createLoader
import ie.conor.utils.hideLoader
import ie.conor.utils.showLoader
import kotlinx.android.synthetic.main.fragment_edit.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class EditFragment : Fragment(), AnkoLogger {

    lateinit var app: FitnessApp
    lateinit var loader : AlertDialog
    lateinit var root: View
    var editFitness: FitnessModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as FitnessApp

        arguments?.let {
            editFitness = it.getParcelable("editfitness")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_edit, container, false)
        activity?.title = getString(R.string.action_edit)
        loader = createLoader(activity!!)

        root.editWeight.setText(editFitness!!.weight.toString())
        root.FirstName.setText(editFitness!!.firstName)
        root.LastName.setText(editFitness!!.lastName)
        root.Height.setText(editFitness!!.height)

        root.editUpdateButton.setOnClickListener {
            showLoader(loader, "Updating Fitness on Server...")
            updateFitnessData()
            updateFitness(editFitness!!.uid, editFitness!!)
            updateUserFitness(app.auth.currentUser!!.uid,
                               editFitness!!.uid, editFitness!!)
        }

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(fitness: FitnessModel) =
            EditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("editfitness",fitness)
                }
            }
    }

    fun updateFitnessData() {
        editFitness!!.firstName = root.FirstName.text.toString()
        editFitness!!.lastName = root.LastName.text.toString()
        editFitness!!.weight = root.editWeight.text.toString()
        editFitness!!.height = root.Height.text.toString()
    }

    fun updateUserFitness(userId: String, uid: String?, fitness: FitnessModel) {
        app.database.child("user-fitnessx").child(userId).child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(fitness)
                        activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.homeFrame, ReportFragment.newInstance())
                        .addToBackStack(null)
                        .commit()
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Fitness error : ${error.message}")
                    }
                })
    }

    fun updateFitness(uid: String?, fitness: FitnessModel) {
        app.database.child("fitnessx").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(fitness)
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Fitness error : ${error.message}")
                    }
                })
    }
}
