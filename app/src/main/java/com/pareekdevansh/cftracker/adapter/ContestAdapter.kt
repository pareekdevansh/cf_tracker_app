package com.pareekdevansh.cftracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pareekdevansh.cftracker.R
import com.pareekdevansh.cftracker.models.Contest
import java.text.SimpleDateFormat
import java.util.*

class ContestAdapter() :
    RecyclerView.Adapter<ContestAdapter.ContestViewHolder>() {

    inner class ContestViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val name: TextView = item.findViewById<TextView>(R.id.name)
        val duration: TextView = item.findViewById<TextView>(R.id.duration)
        val startTime: TextView = item.findViewById<TextView>(R.id.startTime)
        val phase: TextView = item.findViewById<TextView>(R.id.phase)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Contest>() {
        override fun areItemsTheSame(oldItem: Contest, newItem: Contest): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Contest, newItem: Contest): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContestViewHolder {
        return ContestViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_contest, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ContestViewHolder, position: Int) {
        val contest: Contest = differ.currentList[position]
        holder.apply {
            name.text = contest.name
            duration.text = contest.durationSeconds?.let { getDuration(it) }
            phase.text = contest.phase
            startTime.text = contest.startTimeSeconds?.let { getStartTime(it) }

        }
    }

    private fun getDuration(it: Int) : String {
        var minutes = it / 60
        val hours = minutes / 60
        minutes -= hours * 60
        var time  :String = ""
        if(hours < 9)
            time += "0"
        time += hours.toString()
        time += ':'
        if(minutes < 9)
            time += "0"
        time += minutes.toString()
        time += " hrs"
        return time

    }

    private fun getStartTime(registrationTimeSeconds: Int): String {
    val timestamp = registrationTimeSeconds.toLong()
    val timeD = Date(timestamp * 1000)
    val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss ")

    return sdf.format(timeD)
}

override fun getItemCount() = differ.currentList.size


}