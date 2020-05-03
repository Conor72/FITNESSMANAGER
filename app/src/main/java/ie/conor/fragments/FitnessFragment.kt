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
import kotlinx.android.synthetic.main.fragment_fitness.view.*
import kotlinx.android.synthetic.main.fragment_fitness.view.imagefavourite
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import java.util.HashMap


class FitnessFragment : Fragment(), AnkoLogger {


    var fitness = FitnessModel()
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



    fun setFavouriteListener (layout: View) {
        layout.imagefavourite.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (!favourite) {
                    layout.imagefavourite.setImageResource(R.drawable.ic_premium_on)
                    favourite = true
                }
                else {
                    layout.imagefavourite.setImageResource(R.drawable.ic_premium_off)
                    favourite = false
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getTotalFitness(app.currentUser?.uid)
    }

    override fun onPause() {
        super.onPause()
        if(app.currentUser != null)             // MAY NEED TO CHANGE THIS BACK TO if(app.uid != null)
            app.database.child("user-fitnessx")
                .child(app.currentUser!!.uid)
                .removeEventListener(eventListener)
    }

    fun writeNewFitness(fitness: FitnessModel) {
        // Create new fitness at /fitnessx & /fitnessx/$uid
        showLoader(loader, "Adding Fitness to Firebase")
        info("Firebase DB Reference : $app.database")
        val uid = app.currentUser!!.uid
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


                }

            }
        }

        app.database.child("user-fitnessx").child(userId!!)
            .addValueEventListener(eventListener)
    }


    fun setButtonListener( layout: View) {
        layout.fitnessButton.setOnClickListener {


            when {
                layout.FirstName.text.toString().isNullOrEmpty() -> {
                    context?.toast("Please enter a first name")


                }
                layout.LastName.text.toString().isNullOrEmpty() -> {

                    context?.toast("Please enter a last name")


                }
                layout.Weight.text.toString().isNullOrEmpty() -> {

                    context?.toast("Please enter the weight")


                }
                layout.Height.text.toString().isNullOrEmpty() -> {

                    context?.toast("Please enter the height")


                }
                else -> {
                    context?.toast("Customer created")

                    writeNewFitness(
                        FitnessModel(
                            profilepic = app.userImage.toString(),
                            isfavourite = favourite,
                            firstName = layout.FirstName.text.toString(),
                            lastName = layout.LastName.text.toString(),
                            height = layout.Height.text.toString(),
                            weight = layout.Weight.text.toString(),
                            latitude = app.currentLocation.latitude,
                            longitude = app.currentLocation.longitude,
                            email = app.currentUser?.email
                        )
                    )


                }

            }


            //Set text fields back to blank once customer is created.

            fitness.firstName = layout.FirstName.setText("").toString()
            fitness.lastName = layout.LastName.setText("").toString()
            fitness.height = layout.Height.setText("").toString()
            fitness.weight = layout.Weight.setText("").toString()

        }

    }










}
