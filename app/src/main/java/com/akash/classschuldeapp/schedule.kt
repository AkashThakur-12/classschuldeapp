package com.akash.classschuldeapp

data class schulde(
    val id: Int = 0,
    val branch: String = "",
    val sem: String = "",
    val day: String = "",
    val subject: String = "",
    val time: String = "",
    val isCancelled: Boolean = false,
    val cancelReason: String = ""
)
