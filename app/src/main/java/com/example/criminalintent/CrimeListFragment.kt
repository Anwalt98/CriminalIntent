package com.example.criminalintent

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewSwitcher
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.databinding.FragmentCrimeListBinding
import kotlinx.android.synthetic.main.fragment_crime_list.*
import kotlinx.android.synthetic.main.list_item_crime_policy.*

import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "CrimeListFragment"
private const val TYPE_ITEM_WITHOUT_POLICE_BUTTON = 0
private const val TYPE_ITEM_WITH_POLICE_BUTTON = 1


class CrimeListFragment : Fragment() {

    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }
    lateinit var crimeList: List<Crime>
    var ch = true
    private var callbacks: Callbacks? = null
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())
    private lateinit var crimeRecyclerView: RecyclerView
    @RequiresApi(Build.VERSION_CODES.O)
    var formatter = SimpleDateFormat("EEEE, MMMM dd, yyyy")



    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }
    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter
        return view

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val observer = Observer<List<Crime>>{ crimes -> setCrimes(crimes) }
        crimeListViewModel.crimeListLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun setCrimes(crimes: List<Crime>){
        crimeList = crimes
        Log.d("TAG","setCrimes called")
        if (crimeList.isEmpty() && ch) {
            ch = false
            switcher.showNext()
        }
        updateUI()

    }

    private fun updateUI() {
        Log.d("TAG","updateUI called")
        adapter = CrimeAdapter(crimeList)
        crimeRecyclerView.adapter = adapter
//        if (crimeList.isNotEmpty() && ch)
//            Log.d("CRIMES", "${crimeList.size}")
//        ch = false
//        switcher.showNext()
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
        val solvedView : ImageView = itemView.findViewById(R.id.naruch)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind (crime : Crime){
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = formatter.format(this.crime.date)
            solvedView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }
    private inner class CrimeAdapter(var crimes : List<Crime>) : RecyclerView.Adapter<CrimeHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime_policy, parent, false)
            return CrimeHolder(view)
        }

        override fun getItemCount() = crimes.size


        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }

    }
    override fun onCreateOptionsMenu(menu:Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){ R.id.new_crime ->
        {
            val crime = Crime()
            crimeListViewModel.addCrime(crime)
            callbacks?.onCrimeSelected(crime.id)
            true}
            else -> return super.onOptionsItemSelected(item)

        }
    }
}