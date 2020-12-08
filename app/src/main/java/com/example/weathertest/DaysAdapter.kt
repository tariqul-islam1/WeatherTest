package com.example.weathertest

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class DaysAdapter(private val dataSet: List<String>): RecyclerView.Adapter<DaysAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.days_row_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.dateItemTV.text = dataSet[position]
        holder.itemView.setOnClickListener { v ->
//            Toast.makeText(v.context, "Got..." + dataSet.get(position), Toast.LENGTH_SHORT).show()
            val detailsActivity = Intent(holder.itemView.context, DayDetailsActivity::class.java)
            detailsActivity.putExtra("date", dataSet.get(position))
            holder.itemView.context.startActivity(detailsActivity)
        }
    }

    override fun getItemCount() = dataSet.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)  {
        val dateItemTV: TextView

        init {
            dateItemTV = view.findViewById(R.id.date)
        }
    }
}