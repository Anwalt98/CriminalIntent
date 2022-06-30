package com.example.criminalintent

import java.time.LocalDate
import java.util.*

data class Crime(val id: UUID = UUID.randomUUID(),
                 var title: String = "",
                 val date: Date = Date(),
                 var isSolved: Boolean = false,
                 var requiresPolice: Boolean = false)