package com.example.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment

class CrimeFragment : Fragment() {
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var crime: Crime
    private lateinit var solvedCheckBox: CheckBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_crime, container, false)
        dateButton = view.findViewById(R.id.crime_date) as Button
        titleField = view.findViewById(R.id.crime_title) as EditText
        solvedCheckBox = view.findViewById(R.id.crime_solved) as
                    CheckBox
        dateButton.apply{
            text = crime.date.toString()
            isEnabled = false
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                //TODO
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                crime.title =
                    sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
            }
        }
        titleField.addTextChangedListener(titleWatcher)
        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }
    }
}