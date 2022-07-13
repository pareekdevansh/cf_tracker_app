package com.pareekdevansh.cftracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pareekdevansh.cftracker.R
import com.pareekdevansh.cftracker.models.Contest
import java.util.*

class ContestAdapter(private val contests: MutableList<Contest>) :
    RecyclerView.Adapter<ContestAdapter.ContestViewHolder>() {
    inner class ContestViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val name: TextView = item.findViewById<TextView>(R.id.name)
        val duration: TextView = item.findViewById<TextView>(R.id.duration)
        val startTime: TextView = item.findViewById<TextView>(R.id.startTime)
        val phase = item.findViewById<TextView>(R.id.phase)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContestViewHolder {
        return ContestViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_contest, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ContestViewHolder, position: Int) {
        val contest: Contest = contests!![position]
        holder.apply {
            name.text = contest.name
            duration.text = getStartTime(contest.durationSeconds)
            Toast.makeText(itemView.context,"OP BHAI",Toast.LENGTH_SHORT).show()
//            phase.text = contest.
            startTime.text = Date(contest.durationSeconds.toLong()).toString()
        }
    }

    private fun getStartTime(duration: Int): CharSequence {
        val hour = duration / 3600
        val minutes = duration / 60 - hour * 60
        return convertToTwoDigits(hour) + ':' + convertToTwoDigits(minutes)

    }

    private fun convertToTwoDigits(time: Int): String {
        val result = time.toString()
        if (result.length < 2)
            return "0$result";
        return result

    }
    //suno contest ko not null bnake usmne dummy data daal k dekhe to

    override fun getItemCount()=contests!!.size


}