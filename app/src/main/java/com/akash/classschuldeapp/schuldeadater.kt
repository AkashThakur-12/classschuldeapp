package com.akash.classschuldeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akash.classschuldeapp.schulde

class ScheduleAdapter(private val scheduleList: List<schulde>) :
    RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectText: TextView = itemView.findViewById(R.id.subjectText)
        val timeText: TextView = itemView.findViewById(R.id.timeText)
        val typeTag: TextView = itemView.findViewById(R.id.typeTag)
        val instructorText: TextView = itemView.findViewById(R.id.instructorText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_editorial, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = scheduleList[position]
        holder.subjectText.text = schedule.subject
        holder.timeText.text = schedule.time
        holder.instructorText.text = "Academic Session"
        holder.typeTag.text = "LECTURE"
    }

    override fun getItemCount() = scheduleList.size
}
