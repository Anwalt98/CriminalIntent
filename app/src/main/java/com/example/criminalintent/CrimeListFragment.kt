package com.example.criminalintent

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat



private const val TAG = "CrimeListFragment"
private const val TYPE_ITEM_WITHOUT_POLICE_BUTTON = 0
private const val TYPE_ITEM_WITH_POLICE_BUTTON = 1


class CrimeListFragment : Fragment() {
    private var   adapter: CrimeAdapter? = null
    private lateinit var crimeRecyclerView: RecyclerView
    @RequiresApi(Build.VERSION_CODES.O)
    var formatter = SimpleDateFormat("EEEE, MMMM dd, yyyy")



    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total crimes:${crimeListViewModel.crimes.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        updateUI()
        return view

    }

    private fun updateUI() {
        val crimes = crimeListViewModel.crimes
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var crime: Crime

        init {
            itemView.setOnClickListener(this)
        }

        val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        val dateTextView: TextView = itemView.findViewById(R.id.crime_date)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind (crime : Crime){
            this.crime = crime
            titleTextView.text = this.crime.title.toString()
            dateTextView.text = formatter.format(this.crime.date).toString()
        }

        override fun onClick(v: View) {
            Toast.makeText(context, "${crime.title} pressed!"
                , Toast.LENGTH_SHORT)
                .show()
        }
    }
    private inner class CrimeAdapter(var crimes : List<Crime>) : RecyclerView.Adapter<CrimeHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            var view : View
            if (viewType == TYPE_ITEM_WITHOUT_POLICE_BUTTON) {
                view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                return CrimeHolder(view)
            }
            else {
                view = layoutInflater.inflate(R.layout.list_item_crime_policy, parent, false)
                return CrimeHolder(view)
                }
        }

        override fun getItemViewType(position: Int): Int {
            return if (crimes[position].requiresPolice) TYPE_ITEM_WITH_POLICE_BUTTON
            else TYPE_ITEM_WITHOUT_POLICE_BUTTON
        }

        override fun getItemCount() = crimes.size


        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }

    }
}