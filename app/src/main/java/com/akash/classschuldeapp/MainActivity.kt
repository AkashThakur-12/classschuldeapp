package com.akash.classschuldeapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akash.classsschuldeapp.schulde

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var branchSpinner: Spinner
    private lateinit var semSpinner: Spinner
    private lateinit var daySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // RecyclerView
        recyclerView = findViewById(R.id.scheduleRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Spinners
        branchSpinner = findViewById(R.id.branchSpinner)
        semSpinner = findViewById(R.id.semSpinner)
        daySpinner = findViewById(R.id.daySpinner)

        // Data
        val branches = arrayOf("Select Branch", "CS", "IT", "CSB", "CSAI")
        val semesters = arrayOf("Select Semester", "I", "II", "III", "IV", "V", "VI", "VII","VIII")
        val days = arrayOf("Select Day", "MON", "TUE", "WED", "THUR", "FRI")

        // Adapters
        branchSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, branches)
        semSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, semesters)
        daySpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, days)
//        setupSpinner(branchSpinner, branches.toList())
//        setupSpinner(semSpinner, semesters.toList())
//        setupSpinner(daySpinner, days.toList())



        // ONE listener for ALL spinners
        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateSchedule()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        branchSpinner.onItemSelectedListener = spinnerListener
        semSpinner.onItemSelectedListener = spinnerListener
        daySpinner.onItemSelectedListener = spinnerListener
    }

//    private fun setupSpinner(spinner: Spinner, items: List<String>) {
//
//        val adapter = object : ArrayAdapter<String>(
//            this,
//            R.layout.spinner_items,
//            items
//        ) {
//
//            override fun isEnabled(position: Int): Boolean {
//                // Disable first item (hint)
//                return position != 0
//            }
//
//            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//                val view = super.getView(position, convertView, parent) as TextView
//                view.setTextColor(
//                    if (position == 0) Color.GRAY else Color.BLACK
//                )
//                return view
//            }
//
//            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
//                val view = super.getDropDownView(position, convertView, parent) as TextView
//                view.setTextColor(Color.BLACK)
//                view.setPadding(24, 24, 24, 24)
//                return view
//            }
//        }
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinner.adapter = adapter
//    }




    private fun isCSorIT(branch: String): Boolean {
    return branch == "CS" || branch == "IT"
}
    private fun isCSBorCSAI(branch: String): Boolean {
        return branch == "CSB" || branch == "CSAI"
    }

    private fun updateSchedule() {

        val branch = branchSpinner.selectedItem.toString()
        val sem = semSpinner.selectedItem.toString()
        val day = daySpinner.selectedItem.toString()


        val fullSchedule: List<schulde> = when {


            isCSorIT(branch) && sem == "I" ->
                getScheduleForCSITSem1()

            isCSorIT(branch) && sem == "II" ->
                getScheduleForCSITSem2()

            isCSBorCSAI(branch)&& sem=="II" ->
                getScheduleForCSBCASISem2()


            isCSorIT(branch) && sem == "III" ->
                getScheduleForCSITSem3()

            isCSorIT(branch) && sem == "IV" ->
                getScheduleForCSITSem4()


            isCSBorCSAI(branch)&& sem=="I" ->
                getScheduleForCSBCSAISem1()

            isCSBorCSAI(branch)&& sem=="III" ->
                getScheduleForCSBCSAISem3()

            isCSBorCSAI(branch)&& sem=="IV" ->
                getScheduleForCSBCSAISem4()



            // 🔹 Sem 5 different
            branch == "CS" && sem == "V" ->
                getScheduleForCSSem5()

            branch == "IT" && sem == "V" ->
                getScheduleForITSem5()
            branch == "CSB" && sem == "V" ->
                getScheduleForCSBSem5()

            branch == "CSAI" && sem == "V" ->
                getScheduleForCSAISem5()

            isCSorIT(branch) && sem == "VI" ->
                getScheduleForCSITSem6()


            isCSBorCSAI(branch)&& sem=="VI" ->
                getScheduleForCSBCSAISem6()

            isCSBorCSAI(branch)&& sem=="VII" ->
                getScheduleForCSBCSAISem7()

            isCSorIT(branch) && sem == "VII" ->
                getScheduleForCSITSem7()

            else -> emptyList()
        }

        val filteredSchedule =
            if (day == "Select Day") fullSchedule
            else fullSchedule.filter { it.day == day }


        recyclerView.adapter = ScheduleAdapter(filteredSchedule)
    }


    private fun getScheduleForCSITSem1(): List<schulde> {
        return listOf(
            schulde("10:00 - 11:00", "Professional Communication", "MON"),
            schulde("11:15 - 01:20", "DataBase Management System", "MON"),
            schulde("02:30 - 03:30", "System Programming & Scripting", "MON"),
            schulde("03:35 - 04:35", "Web Development", "MON"),
            schulde("04:40 - 05:40", "Sport Class", "MON"),

            schulde("10:00 - 11:00", "Professional Communication", "TUE"),
            schulde("11:15 - 12:15", "DataBase Management System", "TUE"),
            schulde("12:20 - 01:20", "Computational Thinking", "TUE"),
            schulde("02:30 - 03:30", "System Programming & Scripting", "TUE"),
            schulde("03:35 - 05:40", "CTP Lab", "TUE"),

            schulde("10:00 - 11:00", "Professional Communication", "WED"),
            schulde("11:15 - 12:15", "DataBase Management System", "WED"),
            schulde("12:20 - 01:20", "Computational Thinking", "WED"),
            schulde("02:30 - 03:30", "System Programming & Scripting", "WED"),
            schulde("03:35 - 04:35", "Web Development", "WED"),

            schulde("10:00 - 11:00", "Professional Communication", "THUR"),
            schulde("11:15 - 12:15", "DataBase Management System", "THUR"),
            schulde("12:20 - 01:20", "Computational Thinking", "THUR"),
            schulde("02:30 - 03:30", "System Programming & Scripting", "THUR"),
            schulde("03:35 - 04:35", "Web Development", "THUR"),

            schulde("11:15 - 12:15", "SPS Lab", "FRI"),
            schulde("03:35 - 04:35", "Web Dev Lab", "FRI")
        )
    }
    private fun getScheduleForCSITSem3(): List<schulde> {
        return listOf(


            schulde("10:00 - 11:00", "Probability and Statistics", "MON"),   // MS
            schulde("12:20 - 13:20", "Design and Analysis of Algorithms", "MON"), // DA
            schulde("14:30 - 15:30", "Software Engineering", "MON"), // BS
            schulde("15:35 - 16:35", "Data Communication", "MON"), // ST
            schulde("16:40 - 17:40", "Sports - III", "MON"), // DKM/ANS


            schulde("10:00 - 11:00", "Probability and Statistics", "TUE"),
            schulde("11:15 - 12:15", "Theory of Automata", "TUE"), // SC
            schulde("12:20 - 13:20", "Software Engineering", "TUE"),
            schulde("14:30 - 15:30", "Design and Analysis of Algorithms", "TUE"),
            schulde("15:35 - 16:35", "Data Communication", "TUE"),


            schulde("10:00 - 11:00", "Probability and Statistics", "WED"),
            schulde("11:15 - 12:15", "Theory of Automata", "WED"),
            schulde("12:20 - 13:20", "Software Engineering", "WED"),
            schulde("15:35 - 16:35", "Data Communication", "WED"),


            schulde("11:15 - 12:15", "Theory of Automata", "THUR"),
            schulde("12:20 - 13:20", "Design and Analysis of Algorithms", "THUR"),
            schulde("15:35 - 16:35", "Software Engineering LAB", "THUR"),


            schulde("11:15 - 12:15", "Competitive Coding - I", "FRI"),
            schulde("15:35 - 16:35", "Design and Analysis of Algorithms LAB", "FRI")
        )
    }
    private fun getScheduleForCSSem5(): List<schulde> {
        return listOf(

            // ---------- MONDAY ----------
            schulde("10:10 - 11:10", "Machine Learning", "MON"),           // NA
            schulde("11:15 - 12:15", "Foundations of Cryptography", "MON"), // DD
            schulde("12:20 - 13:20", "Elective-I (AI / Blockchain / SC)", "MON"), // RKV
            schulde("14:30 - 15:30", "Computer Graphics", "MON"),          // CG
            schulde("15:35 - 16:35", "Mathematics for CS-II", "MON"),       // SSB
            schulde("16:40 - 17:40", "Elective-I (AI / Blockchain / SC)", "MON"), // SAS/SS

            // ---------- TUESDAY ----------
            schulde("10:10 - 11:10", "Machine Learning", "TUE"),
            schulde("11:15 - 12:15", "Foundations of Cryptography", "TUE"),
            schulde("12:20 - 13:20", "Elective-I (AI / Blockchain / SC)", "TUE"),
            schulde("14:30 - 15:30", "Computer Graphics", "TUE"),
            schulde("15:35 - 16:35", "Mathematics for CS-II", "TUE"),
            schulde("16:40 - 17:40", "Elective-I (AI / Blockchain / SC)", "TUE"),

            // ---------- WEDNESDAY ----------
            schulde("10:10 - 11:10", "Machine Learning", "WED"),
            schulde("11:15 - 12:15", "Foundations of Cryptography", "WED"),
            schulde("12:20 - 13:20", "Elective-I (AI / Blockchain / SC)", "WED"),
            schulde("14:30 - 15:30", "Computer Graphics", "WED"),
            schulde("15:35 - 16:35", "Mathematics for CS-II", "WED"),
            schulde("16:40 - 17:40", "Elective-I LAB", "WED"),

            // ---------- THURSDAY ----------
            schulde("11:15 - 13:20", "Elective-I LAB", "THUR"),             // RKV LAB
            schulde("14:30 - 15:30", "Elective-I (AI / Blockchain / SC)", "THUR"),
            schulde("15:35 - 16:35", "Computer Graphics LAB", "THUR"),

            // ---------- FRIDAY ----------
            schulde("14:30 - 15:30", "Competitive Coding-III", "FRI")      // ST
        )
    }

    private fun getScheduleForCSITSem7(): List<schulde> {
        return listOf(

            // ---------- MON ----------
            schulde("10:10 - 11:10", "Data Mining & Warehousing (SAS)", "MON"),
            schulde("11:15 - 12:15", "Professional Ethics (PE)", "MON"),
            schulde("12:20 - 01:20", "Reinforcement Learning / FinTech (SS/VF)", "MON"),
            schulde("02:30 - 03:30", "Natural Language Processing (GS)", "MON"),

            // ---------- TUE ----------
            schulde("10:10 - 11:10", "Data Mining & Warehousing (SAS)", "TUE"),
            schulde("11:15 - 12:15", "Professional Ethics (PE)", "TUE"),
            schulde("12:20 - 01:20", "Reinforcement Learning / FinTech (SS/VF)", "TUE"),
            schulde("02:30 - 03:30", "Natural Language Processing (GS)", "TUE"),

            // ---------- WED ----------
            schulde("10:10 - 11:10", "Data Mining & Warehousing (SAS)", "WED"),
            schulde("12:20 - 01:20", "Reinforcement Learning / FinTech (SS/VF)", "WED"),
            schulde("02:30 - 03:30", "Natural Language Processing (GS)", "WED"),

            // ---------- THUR ----------
            schulde("10:10 - 11:10", "Data Mining & Warehousing (SAS)", "THUR"),
            schulde("11:15 - 01:20", "Reinforcement Learning / FinTech LAB (SS/VF LAB)", "THUR"),
            schulde("02:30 - 03:30", "Natural Language Processing (GS)", "THUR")

            // ---------- FRI & SAT ----------
            // No regular classes as per timetable
        )
    }
    private fun getScheduleForITSem5(): List<schulde> {
        return listOf(
            // ---------- MON ----------
            schulde("10:10 - 11:10", "Cloud Computing", "MON"),
            schulde("11:15 - 12:15", "Foundations of Cryptography", "MON"),
            schulde("12:20 - 01:20", "Soft Computing", "MON"),
            schulde("02:30 - 03:30", "Computer Graphics", "MON"),
            schulde("04:40 - 05:40", "Elective-I (Blockchain / AI)", "MON"),

            // ---------- TUE ----------
            schulde("10:10 - 11:10", "Cloud Computing", "TUE"),
            schulde("11:15 - 12:15", "Foundations of Cryptography", "TUE"),
            schulde("12:20 - 01:20", "Soft Computing", "TUE"),
            schulde("02:30 - 03:30", "Computer Graphics", "TUE"),
            schulde("04:40 - 05:40", "Elective-I (Blockchain / AI)", "TUE"),

            // ---------- WED ----------
            schulde("10:10 - 11:10", "Cloud Computing", "WED"),
            schulde("11:15 - 12:15", "Foundations of Cryptography", "WED"),
            schulde("12:20 - 01:20", "Soft Computing", "WED"),
            schulde("02:30 - 03:30", "Computer Graphics", "WED"),
            schulde("04:40 - 05:40", "Elective-I LAB", "WED"),

            // ---------- THUR ----------
            schulde("12:20 - 01:20", "Soft Computing LAB", "THUR"),
            schulde("02:30 - 04:35", "Computer Graphics LAB", "THUR"),

            // ---------- FRI ----------
            schulde("02:30 - 03:30", "Competitive Coding - III", "FRI")
        )
    }

    private fun getScheduleForCSBCSAISem1(): List<schulde> {
        return listOf(

            // -------- MON --------
            schulde("10:00 - 11:00", "Professional Communication", "MON"),
            schulde("11:15 - 12:15", "Database Management System (SJ LAB)", "MON"),
            schulde("12:20 - 01:20", "System Programming & Scripting", "MON"),
            schulde("02:30 - 03:30", "Web Design & App Dev-I", "MON"),
            schulde("03:35 - 04:35", "Web Design & App Dev-I", "MON"),
            schulde("04:40 - 05:40", "Sports-I", "MON"),

            // -------- TUE --------
            schulde("10:00 - 11:00", "Professional Communication", "TUE"),
            schulde("11:15 - 12:15", "Database Management System", "TUE"),
            schulde("12:20 - 01:20", "Computational Thinking", "TUE"),
            schulde("02:30 - 03:30", "System Programming & Scripting", "TUE"),
            schulde("03:35 - 05:40", "Computational Thinking LAB", "TUE"),

            // -------- WED --------
            schulde("10:00 - 11:00", "Professional Communication", "WED"),
            schulde("11:15 - 12:15", "Database Management System", "WED"),
            schulde("12:20 - 01:20", "Computational Thinking", "WED"),
            schulde("02:30 - 03:30", "System Programming & Scripting", "WED"),
            schulde("03:35 - 04:35", "Web Design & App Dev-I", "WED"),

            // -------- THUR --------
            schulde("11:15 - 12:15", "Database Management System", "THUR"),
            schulde("12:20 - 01:20", "Computational Thinking", "THUR"),
            schulde("02:30 - 04:35", "Database Management System LAB", "THUR"),

            // -------- FRI --------
            schulde("11:15 - 01:20", "System Programming & Scripting LAB", "FRI"),
            schulde("03:35 - 04:35", "Web Design & App Dev-I LAB", "FRI")
        )
    }

    private fun getScheduleForCSITSem2(): List<schulde> {
        return listOf(

            // -------- MON --------
            schulde("11:10 - 12:10", "Computer Organization & Architecture", "MON"),
            schulde("12:15 - 13:15", "Data Structures", "MON"),
            schulde("14:30 - 15:30", "Professional Communication", "MON"),
            schulde("15:35 - 16:35", "OOPS & System Design", "MON"),

            // -------- TUE --------
            schulde("11:10 - 12:10", "Computer Organization & Architecture", "TUE"),
            schulde("12:15 - 13:15", "Data Structures", "TUE"),
            schulde("14:30 - 15:30", "Professional Communication", "TUE"),
            schulde("15:35 - 16:35", "OOPS & System Design", "TUE"),

            // -------- WED --------
            schulde("11:10 - 12:10", "Computer Organization & Architecture", "WED"),
            schulde("12:15 - 13:15", "Data Structures", "WED"),
            schulde("14:30 - 15:30", "Professional Communication", "WED"),
            schulde("15:35 - 16:35", "OOPS & System Design", "WED"),

            // -------- THUR --------
            schulde("09:00 - 11:00", "Web Design & App Dev-II LAB", "THUR"),
            schulde("14:30 - 16:30", "Data Structures LAB", "THUR"),

            // -------- FRI --------
            schulde("09:00 - 10:00", "Web Design & App Dev-II", "FRI"),
            schulde("11:00 - 13:00", "OOPS & System Design LAB", "FRI"),

            // -------- SAT --------
            schulde("11:00 - 13:00", "Web Design & App Dev-II", "SAT")
        )
    }
    private fun getScheduleForCSBCASISem2(): List<schulde> {
        return listOf(

            // ---------- MONDAY ----------
            schulde("12:15 - 13:15", "Professional Communication", "MON"), // NL
            schulde("14:30 - 15:30", "Computer Organization & Architecture", "MON"), // AS
            schulde("15:35 - 16:35", "Data Structures", "MON"), // RV
            schulde("16:40 - 17:40", "OOPS & System Design", "MON"), // DBS

            // ---------- TUESDAY ----------
            schulde("12:15 - 13:15", "Professional Communication", "TUE"), // NL
            schulde("14:30 - 15:30", "Computer Organization & Architecture", "TUE"), // AS
            schulde("15:35 - 16:35", "Data Structures", "TUE"), // RV
            schulde("16:40 - 17:40", "OOPS & System Design", "TUE"), // DBS

            // ---------- WEDNESDAY ----------
            schulde("12:15 - 13:15", "Professional Communication", "WED"), // NL
            schulde("14:30 - 15:30", "Computer Organization & Architecture", "WED"), // AS
            schulde("15:35 - 16:35", "Data Structures", "WED"), // RV
            schulde("16:40 - 17:40", "OOPS & System Design", "WED"), // DBS

            // ---------- THURSDAY ----------
            schulde("11:10 - 12:10", "Data Structures LAB", "THUR"), // RV LAB
            schulde("15:35 - 16:35", "Web Design & Application Dev-II LAB", "THUR"), // PS LAB

            // ---------- FRIDAY ----------
            schulde("10:05 - 11:05", "Web Design & Application Dev-II", "FRI"), // PS
            schulde("16:40 - 17:40", "OOPS & System Design LAB", "FRI") // DBS LAB
        )
    }

    private fun getScheduleForCSBCSAISem3(): List<schulde> {
        return listOf(

            // ================= MONDAY =================
            schulde("11:15 - 12:15", "Probability and Statistics", "MON"),     // MD
            schulde("12:20 - 13:20", "Software Engineering", "MON"),          // BS
            schulde("14:30 - 15:30", "Design and Analysis of Algorithms", "MON"), // DA
            schulde("16:40 - 17:40", "Data Communication", "MON"),            // ST

            // ================= TUESDAY =================
            schulde("12:20 - 13:20", "Theory of Automata", "TUE"),            // SC
            schulde("14:30 - 15:30", "Software Engineering", "TUE"),          // BS
            schulde("16:40 - 17:40", "Data Communication", "TUE"),            // ST
            schulde("17:45 - 18:45", "Sports-III", "TUE"),                   // DKM/ANS

            // ================= WEDNESDAY =================
            schulde("11:15 - 12:15", "Probability and Statistics", "WED"),    // MD
            schulde("12:20 - 13:20", "Theory of Automata", "WED"),            // SC
            schulde("14:30 - 15:30", "Design and Analysis of Algorithms", "WED"), // DA
            schulde("16:40 - 17:40", "Data Communication", "WED"),            // ST

            // ================= THURSDAY =================
            schulde("11:15 - 12:15", "Probability and Statistics", "THUR"),   // MD
            schulde("12:20 - 13:20", "Theory of Automata", "THUR"),           // SC
            schulde("14:30 - 15:30", "Design and Analysis of Algorithms", "THUR"), // DA
            schulde("15:35 - 16:35", "Competitive Coding - I", "THUR"),      // ST1

            // ================= FRIDAY =================
            schulde("12:20 - 13:20", "Software Engineering", "FRI"),          // BS
            schulde("14:30 - 16:35", "Software Engineering LAB", "FRI"),     // BS LAB
            schulde("16:40 - 18:45", "Design and Analysis of Algorithms LAB", "FRI") // DA LAB
        )
    }
    private fun getScheduleForCSITSem4(): List<schulde> {
        return listOf(

            // ---------- MONDAY ----------
            schulde("10:05 - 11:05", "Compiler Design", "MON"),
            schulde("11:10 - 12:10", "Maths for CS-I", "MON"),
            schulde("12:15 - 01:15", "Operating System", "MON"),

            // ---------- TUESDAY ----------
            schulde("09:00 - 10:00", "Programming & Data Structures", "TUE"),
            schulde("10:05 - 11:05", "Compiler Design", "TUE"),
            schulde("11:10 - 12:10", "Maths for CS-I", "TUE"),
            schulde("02:30 - 03:30", "Computer Networks", "TUE"),

            // ---------- WEDNESDAY ----------
            schulde("09:00 - 10:00", "Programming & Data Structures", "WED"),
            schulde("10:05 - 11:05", "Compiler Design", "WED"),
            schulde("11:10 - 12:10", "Maths for CS-I", "WED"),
            schulde("02:30 - 03:30", "Computer Networks", "WED"),

            // ---------- THURSDAY ----------
            schulde("09:00 - 10:00", "Programming & Data Structures", "THUR"),
            schulde("10:05 - 11:05", "Compiler Design", "THUR"),
            schulde("11:10 - 12:10", "Operating System", "THUR"),
            schulde("03:35 - 05:35", "Operating System Lab", "THUR"),

            // ---------- FRIDAY ----------
            schulde("10:05 - 12:05", "Programming & Data Structures Lab", "FRI"),
            schulde("12:15 - 01:15", "Competitive Coding", "FRI"),
            schulde("02:30 - 04:30", "Computer Networks Lab", "FRI")
        )
    }
    private fun getScheduleForCSBCSAISem4(): List<schulde> {
        return listOf(

            // ---------- MONDAY ----------
            schulde("10:05 - 11:05", "Computer Networks", "MON"),        // NA
            schulde("11:10 - 12:10", "Compiler Design", "MON"),          // AK
            schulde("12:15 - 01:15", "Maths for CS-I", "MON"),           // DD
            schulde("02:30 - 03:30", "Operating System", "MON"),        // ST
            schulde("03:35 - 04:35", "Advanced Programming Language", "MON"), // PDS

            // ---------- TUESDAY ----------
            schulde("10:05 - 11:05", "Computer Networks", "TUE"),        // NA
            schulde("11:10 - 12:10", "Compiler Design", "TUE"),          // AK
            schulde("12:15 - 01:15", "Maths for CS-I", "TUE"),           // DD
            schulde("02:30 - 03:30", "Operating System", "TUE"),        // ST
            schulde("03:35 - 04:35", "Advanced Programming Language", "TUE"), // PDS

            // ---------- WEDNESDAY ----------
            schulde("10:05 - 11:05", "Computer Networks", "WED"),        // NA
            schulde("11:10 - 12:10", "Compiler Design", "WED"),          // AK
            schulde("12:15 - 01:15", "Maths for CS-I", "WED"),           // DD
            schulde("02:30 - 03:30", "Operating System", "WED"),        // ST
            schulde("03:35 - 04:35", "Advanced Programming Language", "WED"), // PDS
            schulde("04:40 - 05:40", "Competitive Coding", "WED"),      // SSB

            // ---------- THURSDAY ----------
            schulde("11:10 - 12:10", "Compiler Design", "THUR"),         // AK
            schulde("02:30 - 03:30", "Operating System Lab", "THUR"),   // ST LAB

            // ---------- FRIDAY ----------
            schulde("10:05 - 12:05", "Computer Networks Lab", "FRI"),   // NA LAB
            schulde("12:15 - 02:15", "Advanced Programming Language Lab", "FRI") // PDS LAB
        )
    }
    private fun getScheduleForCSBSem5(): List<schulde> {
        return listOf(

            // ---------- MONDAY ----------
            schulde("10:10 - 11:10", "Business Economics", "MON"),        // VS
            schulde("11:15 - 12:15", "Machine Learning", "MON"),         // NA
            schulde("12:20 - 01:20", "Soft Computing", "MON"),           // RKV
            schulde("02:30 - 03:30", "People Management", "MON"),        // PT
            schulde("03:35 - 04:35", "Business Decision Making", "MON"), // BDM
            schulde("04:40 - 05:40", "Artificial Intelligence", "MON"), // SS

            // ---------- TUESDAY ----------
            schulde("10:10 - 11:10", "Business Economics", "TUE"),        // VS
            schulde("11:15 - 12:15", "Machine Learning", "TUE"),         // NA
            schulde("12:20 - 01:20", "Soft Computing", "TUE"),           // RKV
            schulde("02:30 - 03:30", "People Management", "TUE"),        // PT
            schulde("03:35 - 04:35", "Business Decision Making", "TUE"), // BDM
            schulde("04:40 - 05:40", "Artificial Intelligence", "TUE"), // SS

            // ---------- WEDNESDAY ----------
            schulde("10:10 - 11:10", "Business Economics", "WED"),        // VS
            schulde("11:15 - 12:15", "Machine Learning", "WED"),         // NA
            schulde("12:20 - 01:20", "Soft Computing", "WED"),           // RKV
            schulde("02:30 - 03:30", "People Management", "WED"),        // PT
            schulde("03:35 - 04:35", "Business Decision Making", "WED"), // BDM
            schulde("04:40 - 05:40", "Artificial Intelligence Lab", "WED"), // SS LAB

            // ---------- THURSDAY ----------
            schulde("11:15 - 01:15", "Soft Computing Lab", "THUR"),       // RKV LAB
            schulde("02:30 - 03:30", "Artificial Intelligence", "THUR"), // SS

            // ---------- FRIDAY ----------
            schulde("04:40 - 05:40", "Competitive Coding - III", "FRI")  // ST
        )
    }

    private fun getScheduleForCSAISem5(): List<schulde> {
        return listOf(

            // ---------- MONDAY ----------
            schulde("11:15 - 12:15", "Machine Learning", "MON"),        // NA
            schulde("12:20 - 01:20", "Soft Computing", "MON"),         // RKV
            schulde("02:30 - 03:30", "Artificial Intelligence", "MON"), // SS
            schulde("03:35 - 04:35", "Mathematics for CS II", "MON"),  // SSB
            schulde("04:40 - 05:40", "Computer Graphics", "MON"),     // CG

            // ---------- TUESDAY ----------
            schulde("11:15 - 12:15", "Machine Learning", "TUE"),        // NA
            schulde("12:20 - 01:20", "Soft Computing", "TUE"),         // RKV
            schulde("02:30 - 03:30", "Artificial Intelligence", "TUE"), // SS
            schulde("03:35 - 04:35", "Mathematics for CS II", "TUE"),  // SSB
            schulde("04:40 - 05:40", "Computer Graphics", "TUE"),     // CG

            // ---------- WEDNESDAY ----------
            schulde("11:15 - 12:15", "Machine Learning", "WED"),        // NA
            schulde("12:20 - 01:20", "Soft Computing", "WED"),         // RKV
            schulde("02:30 - 03:30", "Artificial Intelligence", "WED"), // SS
            schulde("03:35 - 04:35", "Mathematics for CS II", "WED"),  // SSB
            schulde("04:40 - 05:40", "Computer Graphics Lab", "WED"), // CG LAB

            // ---------- THURSDAY ----------
            schulde("11:15 - 01:15", "Soft Computing Lab", "THUR"),    // RKV LAB
            schulde("02:30 - 03:30", "Computer Graphics", "THUR"),    // CG

            // ---------- FRIDAY ----------
            schulde("02:30 - 04:30", "Artificial Intelligence Lab", "FRI"), // SS LAB
            schulde("04:40 - 05:40", "Competitive Coding - III", "FRI")     // ST
        )
    }

    private fun getScheduleForCSITSem6(): List<schulde> {
        return listOf(

            // ---------- MONDAY ----------
            schulde("09:00 - 10:00", "Competitive Coding (SSB)", "MON"),
            schulde("10:05 - 11:05", "Probability & Statistics (MS)", "MON"),
            schulde("11:45 - 01:15", "Techno-Entrepreneurship", "MON"), // BS
            schulde("02:30 - 03:30", "Elective-II", "MON"), // SS

            // ---------- TUESDAY ----------
            schulde("09:00 - 10:00", "Competitive Coding (SSB)", "TUE"),
            schulde("10:05 - 11:05", "Probability & Statistics (MS)", "TUE"),
            schulde("11:45 - 01:15", "Techno-Entrepreneurship", "TUE"), // BS
            schulde("02:30 - 03:30", "Elective-II", "TUE"), // SS

            // ---------- WEDNESDAY ----------
            schulde("09:00 - 10:00", "Competitive Coding (SSB)", "WED"),
            schulde("10:05 - 11:05", "Probability & Statistics (MS)", "WED"),
            schulde("11:45 - 01:15", "Techno-Entrepreneurship", "WED"), // BS
            schulde("02:30 - 03:30", "Elective-II", "WED"), // SS

            // ---------- THURSDAY ----------
            schulde("09:00 - 10:00", "Competitive Coding Tutorial", "THUR"),
            schulde("10:05 - 11:05", "Probability & Statistics Tutorial", "THUR"),
            schulde("02:30 - 04:30", "Elective-II Lab", "THUR"), // SS LAB

            // ---------- FRIDAY ----------
            schulde("11:45 - 01:15", "Computer Networks Lab", "FRI") // SC LAB
        )
    }

    private fun getScheduleForCSBCSAISem6(): List<schulde> {
        return listOf(

            // ---------- MONDAY ----------
            schulde("09:00 - 10:00", "Competitive Coding (SSB)", "MON"),
            schulde("10:05 - 11:05", "Maths-I / Soft Computing (SA)", "MON"),
            schulde("11:10 - 12:10", "Maths-II (MS2)", "MON"),
            schulde("12:15 - 01:15", "People Management (PT)", "MON"),
            schulde("02:30 - 03:30", "Artificial Intelligence (SS)", "MON"),
            schulde("03:35 - 04:35", "Techno-Entrepreneurship (BS)", "MON"),

            // ---------- TUESDAY ----------
            schulde("09:00 - 10:00", "Competitive Coding (SSB)", "TUE"),
            schulde("10:05 - 11:05", "Maths-I / Soft Computing (SA)", "TUE"),
            schulde("11:10 - 12:10", "Deep Learning / Maths-II (SC/MS2)", "TUE"),
            schulde("12:15 - 01:15", "People Management (PT)", "TUE"),
            schulde("02:30 - 03:30", "Artificial Intelligence (SS)", "TUE"),
            schulde("03:35 - 04:35", "Techno-Entrepreneurship (BS)", "TUE"),

            // ---------- WEDNESDAY ----------
            schulde("09:00 - 10:00", "Competitive Coding (SSB)", "WED"),
            schulde("10:05 - 11:05", "Maths-I / Soft Computing (SA)", "WED"),
            schulde("11:10 - 12:10", "Deep Learning / Maths-II (SC/MS2)", "WED"),
            schulde("12:15 - 01:15", "People Management (PT)", "WED"),
            schulde("02:30 - 03:30", "Artificial Intelligence (SS)", "WED"),
            schulde("03:35 - 04:35", "Techno-Entrepreneurship (BS)", "WED"),

            // ---------- THURSDAY ----------
            schulde("09:00 - 10:00", "Competitive Coding Tutorial", "THUR"),
            schulde("10:05 - 11:05", "Maths-I Tutorial / SA", "THUR"),
            schulde("11:10 - 12:10", "Deep Learning / Maths-II (SC/MS2)", "THUR"),
            schulde("12:15 - 01:15", "People Management (PT)", "THUR"),
            schulde("02:30 - 04:30", "Artificial Intelligence Lab (SS LAB)", "THUR"),

            // ---------- FRIDAY ----------
            schulde("02:30 - 04:30", "Deep Learning Lab (SC LAB)", "FRI")
        )
    }

    private fun getScheduleForCSBCSAISem7(): List<schulde> {
        return listOf(

            // ---------- MONDAY ----------
            schulde("10:10 - 11:10", "Professional Ethics", "MON"),
            schulde("11:15 - 12:15", "Elective III", "MON"),
            schulde("12:20 - 01:20", "Elective IV", "MON"),

            // ---------- TUESDAY ----------
            schulde("10:10 - 11:10", "Professional Ethics", "TUE"),
            schulde("11:15 - 12:15", "Elective III", "TUE"),
            schulde("12:20 - 01:20", "Elective IV", "TUE"),

            // ---------- WEDNESDAY ----------
            schulde("11:15 - 12:15", "Elective III", "WED"),
            schulde("12:20 - 01:20", "Elective IV", "WED"),

            // ---------- THURSDAY ----------
            schulde("11:15 - 12:15", "Elective III", "THUR"),
            schulde("12:20 - 01:20", "Elective IV", "THUR")
        )
    }




}
