package com.akash.classschuldeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akash.classsschuldeapp.schulde

import org.jetbrains.annotations.Async



class ScheduleAdapter(private val scheduleList: List<schulde>) :
    RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectText: TextView = itemView.findViewById(R.id.subjectText)
        val timeText: TextView = itemView.findViewById(R.id.timeText)
//        val timeLabel: TextView = itemView.findViewById(R.id.timeText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schulde, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = scheduleList[position]
        holder.subjectText.text = schedule.subject
        holder.timeText.text = schedule.time

        // First part of time for the left label (e.g. "10:00 AM")
//        holder.timeLabel.text = schedule.time.split("-")[0].trim()
    }

    override fun getItemCount() = scheduleList.size
}
