package com.pareekdevansh.cftracker.ui.profile

import android.content.Context
import android.widget.Toast
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

class PieChartOnChartValueSelectedListener(private val requireContext: Context) :
    OnChartValueSelectedListener {

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        Toast.makeText(requireContext , e.toString() , Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected() {}
}