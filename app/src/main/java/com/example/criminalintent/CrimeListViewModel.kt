package com.example.criminalintent

import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.util.*

class CrimeListViewModel : ViewModel() {
    val crimes = mutableListOf<Crime>()
    init {
        for (i in 0 until 100) {
            val crime = Crime()
            crime.title = "Crime #$i"
            crime.isSolved = i % 2 == 0
            if (i%5 == 0) crime.requiresPolice = true
            crimes += crime
        }
    }
}
