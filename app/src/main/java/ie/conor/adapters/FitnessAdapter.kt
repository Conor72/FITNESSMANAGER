package ie.conor.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.conor.R
import ie.conor.models.FitnessModel
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.card_fitness.view.*

interface FitnessListener {
    fun onFitnessClick(fitness: FitnessModel)
}

class FitnessAdapter constructor(var fitnessx: ArrayList<FitnessModel>,
                                  private val listener: FitnessListener, reportall : Boolean)
    : RecyclerView.Adapter<FitnessAdapter.MainHolder>() {

    val reportAll = reportall

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent?.context).inflate(
                R.layout.card_fitness,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val fitness = fitnessx[holder.adapterPosition]
        holder.bind(fitness,listener,reportAll)
    }

    override fun getItemCount(): Int = fitnessx.size

    fun removeAt(position: Int) {
        fitnessx.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(fitness: FitnessModel, listener: FitnessListener, reportAll: Boolean) {
            itemView.tag = fitness
            itemView.firstname.text = fitness.firstName
            itemView.customerWeight.text = fitness.weight.toString()
            if(fitness.isfavourite) itemView.imagefavourite.setImageResource(android.R.drawable.star_big_on)

            if(!reportAll)
                itemView.setOnClickListener { listener.onFitnessClick(fitness) }

            if(!fitness.profilepic.isEmpty()) {
                Picasso.get().load(fitness.profilepic.toUri())
                    //.resize(180, 180)
                    .transform(CropCircleTransformation())
                    .into(itemView.imageIcon)
            }
            else
                itemView.imageIcon.setImageResource(R.mipmap.ic_launcher_homer_round)
        }
    }
}