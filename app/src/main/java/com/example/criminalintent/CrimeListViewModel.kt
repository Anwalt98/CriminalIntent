package com.example.criminalintent

import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.util.*

class CrimeListViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    val crimeListLiveData = crimeRepository.getCrimes()
}
