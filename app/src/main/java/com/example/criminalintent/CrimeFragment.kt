package com.example.criminalintent

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


private const val ARG_CRIME_ID = "crime_id"
private const val TAG = "CrimeFragment"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
private const val REQUEST_CONTACT = 1
private const val REQUEST_PHOTO = 2
private const val DATE_FORMAT = "EEE, MMM, dd"

class CrimeFragment : Fragment() , DatePickerFragment.Callbacks {
    private lateinit var photoUri: Uri
    private lateinit var photoFile: File
    private lateinit var reportButton: Button
    private lateinit var callButton: Button
    private lateinit var suspectButton: Button
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var crime: Crime
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView
    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_crime, container, false)
        dateButton = view.findViewById(R.id.crime_date) as Button
        titleField = view.findViewById(R.id.crime_title) as EditText
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        reportButton = view.findViewById(R.id.crime_report) as Button
        suspectButton = view.findViewById(R.id.crime_suspect) as Button
        callButton = view.findViewById(R.id.call_crime_suspect) as Button
        photoButton = view.findViewById(R.id.crime_camera) as ImageButton
        photoView = view.findViewById(R.id.crime_photo) as ImageView
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner, androidx.lifecycle.Observer { crime ->
                crime?.let {
                    this.crime = crime
                    photoFile = crimeDetailViewModel.getPhotoFile(crime)
                    photoUri = FileProvider.getUriForFile(requireActivity(),
                        "com.example.criminalintent.fileprovider",
                                    photoFile)
                    updateUI()
                }
            })
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
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        dateButton.text = "Crime date"
        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }
        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also { intent ->
                val chooserIntent =
                Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)}
        }
        suspectButton.setOnClickListener {

//            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(requireContext(),"Permission is given", Toast.LENGTH_SHORT).show()
                openContactsBook()
                            }
//                else {
//                requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), REQUEST_CONTACT)
//                }
//            }

        callButton.setOnClickListener {
          val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + crime.suspectPhoneNumber))
            startActivity(callIntent)
        }

        solvedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            crime.isSolved = isChecked
        }
        photoButton.apply {
            val packageManager: PackageManager = requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }
            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                val cameraActivities: List<ResolveInfo> = packageManager.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                startActivityForResult(captureImage, REQUEST_PHOTO)
            }
        }
    }

    private fun openContactsBook(){
        val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        pickContactIntent.type = Phone.CONTENT_TYPE;
        startActivityForResult(pickContactIntent, REQUEST_CONTACT)
    }



//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
//            openContactsBook()
//        }
//    }
    private fun updateUI() {
        var formatter = SimpleDateFormat("EEEE, MMMM dd, yyyy")
        titleField.setText(crime.title)
        dateButton.text = formatter.format(crime.date)

        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        if (crime.suspect.isNotEmpty()) {
            suspectButton.text = crime.suspect
        }
        if (crime.suspectPhoneNumber.isNotEmpty()) {
        callButton.text = "call " + crime.suspectPhoneNumber
    }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {resultCode != Activity.RESULT_OK -> return
            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri: Uri? = data.data
                val queryFieldsName = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                val queryFieldsNumber = arrayOf(Phone.NUMBER)
                val cursorName = contactUri?.let { requireActivity().contentResolver.query(it, queryFieldsName, null, null, null) }
                val cursorNumber = contactUri?.let { requireActivity().contentResolver.query(it, queryFieldsNumber, null, null, null) }
                var nameSuspect = ""
                var phoneNumber = ""
                cursorName?.use { if (it.count == 0) { return }
                    it.moveToFirst()
                    nameSuspect = it.getString(0)
                }
                cursorNumber?.use {
                    if (it.count == 0) {
                        return
                    }
                    it.moveToFirst()
                    phoneNumber =it.getString(0)
                    crime.suspect = nameSuspect
                    crime.suspectPhoneNumber = phoneNumber
                    crimeDetailViewModel.saveCrime(crime)
                    suspectButton.text = nameSuspect
                }

                    }
                }
        }


    private fun getCrimeReport(): String {
        val solvedString =
            if (crime.isSolved) {
            getString(R.string.crime_report_solved) }
        else { getString(R.string.crime_report_unsolved) }
        val dateString =
            DateFormat.format(DATE_FORMAT, crime.date).toString()
        var suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else { getString(R.string.crime_report_suspect, crime.suspect) }
        return getString(R.string.crime_report,
            crime.title, dateString,
            solvedString, suspect
        )
    }



    companion object {
        fun newInstance(crimeId: UUID):
                CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }
}