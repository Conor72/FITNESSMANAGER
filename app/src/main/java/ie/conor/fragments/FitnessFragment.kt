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
import ie.conor.utils.*
import kotlinx.android.synthetic.main.fragment_fitness.*
import kotlinx.android.synthetic.main.fragment_fitness.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import java.lang.String.format
import java.util.HashMap


class FitnessFragment : Fragment(), AnkoLogger {

    lateinit var app: FitnessApp
    var totalFitness = 0
    lateinit var loader : AlertDialog
    lateinit var eventListener : ValueEventListener
    var favourite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as FitnessApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_fitness, container, false)
        loader = createLoader(activity!!)
        activity?.title = getString(R.string.action_fitness)
        setButtonListener(root)
        setFavouriteListener(root)
        return root;
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FitnessFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    fun setButtonListener( layout: View) {
        layout.fitnessButton.setOnClickListener {
            val weight = if (layout.paymentAmount.text.isNotEmpty())
                layout.paymentAmount.text.toString().toInt() else layout.amountPicker.value
            if(totalFitness >= layout.progressBar.max)
                activity?.toast("Fitness Amount Exceeded!")
            else {
                val firstName = if(layout.paymentMethod.checkedRadioButtonId == R.id.Direct) "Direct" else "Paypal"
                writeNewFitness(FitnessModel(firstName = firstName, weight = weight,
                    profilepic = app.userImage.toString(),
                    isfavourite = favourite,
                    latitude = app.currentLocation.latitude,
                    longitude = app.currentLocation.longitude,
                    email = app.auth.currentUser?.email))
            }
        }
    }

    fun setFavouriteListener (layout: View) {
        layout.imagefavourite.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (!favourite) {
                    layout.imagefavourite.setImageResource(android.R.drawable.star_big_on)
                    favourite = true
                }
                else {
                    layout.imagefavourite.setImageResource(android.R.drawable.star_big_off)
                    favourite = false
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getTotalFitness(app.auth.currentUser?.uid)
    }

    override fun onPause() {
        super.onPause()
        if(app.auth.uid != null)
            app.database.child("user-fitnessx")
                .child(app.auth.currentUser!!.uid)
                .removeEventListener(eventListener)
    }

    fun writeNewFitness(fitness: FitnessModel) {
        // Create new donation at /donations & /donations/$uid
        showLoader(loader, "Adding Fitness to Firebase")
        info("Firebase DB Reference : $app.database")
        val uid = app.auth.currentUser!!.uid
        val key = app.database.child("fitnessx").push().key
        if (key == null) {
            info("Firebase Error : Key Empty")
            return
        }
        fitness.uid = key
        val fitnessValues = fitness.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/fitnessx/$key"] = fitnessValues
        childUpdates["/user-fitnessx/$uid/$key"] = fitnessValues

        app.database.updateChildren(childUpdates)
        hideLoader(loader)
    }

    fun getTotalFitness(userId: String?) {
        eventListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                info("Firebase Fitness error : ${error.message}")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                totalFitness = 0
                val children = snapshot.children
                children.forEach {
                    val fitness = it.getValue<FitnessModel>(FitnessModel::class.java)
                    totalFitness += fitness!!.weight
                }
                progressBar.progress = totalFitness
                totalSoFar.text = format("$ $totalFitness")
            }
        }

        app.database.child("user-fitnessx").child(userId!!)
            .addValueEventListener(eventListener)
    }
}
