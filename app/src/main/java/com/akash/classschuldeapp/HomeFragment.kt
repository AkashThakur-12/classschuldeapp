package com.akash.classschuldeapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var branchSpinner: Spinner
    private lateinit var semSpinner: Spinner
    private lateinit var dayPillContainer: android.widget.LinearLayout
    private var selectedDay = "MON"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.scheduleRecycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        branchSpinner = view.findViewById(R.id.branchSpinner)
        semSpinner = view.findViewById(R.id.semSpinner)
        dayPillContainer = view.findViewById(R.id.dayPillContainer)

        val branches = arrayOf("Select Branch", "CS", "IT", "CSB", "CSAI")
        val semesters = arrayOf("Select Semester", "I", "II", "III", "IV", "V", "VI", "VII", "VIII")

        branchSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, branches)
        semSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, semesters)

        val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val savedBranch = prefs.getString("branch", "")
        val savedSem = prefs.getString("sem", "")

        if (!savedBranch.isNullOrEmpty() && branches.contains(savedBranch)) {
            branchSpinner.setSelection(branches.indexOf(savedBranch))
        }
        if (!savedSem.isNullOrEmpty() && semesters.contains(savedSem)) {
            semSpinner.setSelection(semesters.indexOf(savedSem))
        }

        val calendar = Calendar.getInstance()
        selectedDay = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "MON"
            Calendar.TUESDAY -> "TUE"
            Calendar.WEDNESDAY -> "WED"
            Calendar.THURSDAY -> "THUR"
            Calendar.FRIDAY -> "FRI"
            Calendar.SATURDAY -> "SAT"
            Calendar.SUNDAY -> "SUN"
            else -> "MON"
        }

        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateHeaderSubtitle()
                updateSchedule()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        branchSpinner.onItemSelectedListener = spinnerListener
        semSpinner.onItemSelectedListener = spinnerListener

        setupDayPills()
        updateHeaderSubtitle()
        updateSchedule()
    }

    private fun setupDayPills() {
        val days = listOf("MON", "TUE", "WED", "THUR", "FRI", "SAT", "SUN")
        dayPillContainer.removeAllViews()

        days.forEach { dayText ->
            val textView = TextView(requireContext())
            textView.text = dayText
            textView.gravity = android.view.Gravity.CENTER

            val density = resources.displayMetrics.density
            val px20 = (20 * density).toInt()
            val px10 = (10 * density).toInt()
            val px8 = (8 * density).toInt()

            textView.setPadding(px20, px10, px20, px10)
            val params = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = px8
            textView.layoutParams = params

            try {
                androidx.core.widget.TextViewCompat.setTextAppearance(textView, com.google.android.material.R.style.TextAppearance_Material3_LabelLarge)
            } catch (e: Exception) {
                textView.textSize = 14f
            }

            if (dayText == selectedDay) {
                textView.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary_editorial))
                textView.setBackgroundResource(R.drawable.active_day_pill_bg)
            } else {
                textView.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.on_surface_dim))
                textView.setBackgroundResource(R.drawable.inactive_day_pill_bg)
            }

            textView.setOnClickListener {
                selectedDay = dayText
                setupDayPills()
                updateSchedule()
            }
            dayPillContainer.addView(textView)
        }
    }

    private fun updateHeaderSubtitle() {
        val branch = branchSpinner.selectedItem?.toString() ?: ""
        val sem = semSpinner.selectedItem?.toString() ?: ""
        val subtitleView = view?.findViewById<TextView>(R.id.headerSubtitle)

        if (branch != "Select Branch" && sem != "Select Semester") {
            subtitleView?.text = "SEMESTER $sem • $branch"
        } else {
            subtitleView?.text = "ACADEMIC SESSION"
        }
    }

    private fun updateSchedule() {
        if (!isAdded) return
        val branch = branchSpinner.selectedItem.toString()
        val sem = semSpinner.selectedItem.toString()

        if (branch == "Select Branch" || sem == "Select Semester") {
            recyclerView.adapter = ScheduleAdapter(emptyList())
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val romanToNum = mapOf(
                    "I" to "1", "II" to "2", "III" to "3", "IV" to "4",
                    "V" to "5", "VI" to "6", "VII" to "7", "VIII" to "8"
                )
                val numericSem = romanToNum[sem] ?: sem

                val scheduleList = withTimeout(10_000L) {
                    RetrofitClient.instance.getSchedule(branch.lowercase())
                }

                if (!isAdded || view == null) return@launch

                val filteredList = scheduleList.filter {
                    it.branch.equals(branch, ignoreCase = true) && it.sem == numericSem && it.day == selectedDay
                }

                recyclerView.adapter = ScheduleAdapter(filteredList)

                val emptyText = view?.findViewById<TextView>(R.id.emptyScheduleText)
                if (filteredList.isEmpty()) {
                    emptyText?.visibility = View.VISIBLE
                    emptyText?.text = "No classes scheduled for $selectedDay"
                } else {
                    emptyText?.visibility = View.GONE
                }

                context?.let { ctx ->
                    NotificationScheduler.scheduleClassReminders(ctx, filteredList)
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (isAdded) {
                    context?.let { ctx ->
                        Toast.makeText(ctx, "Failed to fetch: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    recyclerView.adapter = ScheduleAdapter(emptyList())
                }
            }
        }
    }
}
