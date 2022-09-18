package com.androstays.happyplaces.activities

import com.androstays.happyplaces.databinding.ActivityAddHappyPlacesBinding
import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.androstays.happyplaces.R
import com.androstays.happyplaces.database.DatabaseHandler
import com.androstays.happyplaces.model.HappyPlacesModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


class AddHappyPlacesActivity : AppCompatActivity(), View.OnClickListener {

    private var binding: ActivityAddHappyPlacesBinding? = null

    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0


    companion object {
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
    }


    private lateinit var galleryImageResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraImageResultLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlacesBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        setSupportActionBar(binding?.toolbarAddPlace)
        supportActionBar?.let {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Add Happy Place"
        }
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            upDateDateInView()
        }
        upDateDateInView()
        // Setting OnClickListeners
        binding?.etDate?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
        binding?.btnSave?.setOnClickListener(this)


        // Register Activity Result Launcher
        registerOnActivityForGalleryResult()
        registerOnActivityForCameraResult()

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.etDate -> {
                DatePickerDialog(
                    this@AddHappyPlacesActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.tvAddImage -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select photo from Gallery", "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems) { _, which ->
                    when (which) {
                        0 -> choosePhotoFromGallery()
                        1 -> takePictureWithCamera()
                    }
                }
                pictureDialog.show()
            }

            R.id.btnSave -> {
                when {
                    binding?.title?.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            this@AddHappyPlacesActivity,
                            "Enter the title",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    binding?.description?.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            this@AddHappyPlacesActivity,
                            "Enter the description",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    binding?.location?.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            this@AddHappyPlacesActivity,
                            "Enter the location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    saveImageToInternalStorage == null -> {
                        Toast.makeText(
                            this@AddHappyPlacesActivity,
                            "Select the Image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        print("entered into this")
                        val happyPlacesModel = HappyPlacesModel(
                            0, binding?.title?.text.toString(),
                            saveImageToInternalStorage.toString(),
                            binding?.description?.text.toString(),
                            binding?.etDate?.text.toString(),
                            mLatitude,
                            mLongitude
                        )
                        val dbHandler = DatabaseHandler(this)
                        val addHappyPlace = dbHandler.addHappyPlace(happyPlacesModel)
                        if (addHappyPlace > 0) {
                            Toast.makeText(
                                this,
                                "Happy places details has been added successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Happy places details has been not added",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        finish()
                        intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }

            }
        }
    }

    private fun takePictureWithCamera() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {

                    // Start Activity
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraImageResultLauncher.launch(cameraIntent)

                } else showRationalDialogForPermissions()
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>,
                token: PermissionToken
            ) {
                showRationalDialogForPermissions()
                token.continuePermissionRequest()
            }
        }).onSameThread().check()

    }

    private fun choosePhotoFromGallery() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {

                    // Start Activity
                    val galleryIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryImageResultLauncher.launch(galleryIntent)

                } else showRationalDialogForPermissions()
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>,
                token: PermissionToken
            ) {
                showRationalDialogForPermissions()
                token.continuePermissionRequest()
            }
        }).onSameThread().check()

    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It loos like you turned of permission required for this feature. It can be enabled under the Applications Settings ")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }


    private fun registerOnActivityForGalleryResult() {
        galleryImageResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data != null) {
                        val contentUri = data.data
                        try {
                            saveImageToInternalStorage = saveImageToInternalStorage(
                                BitmapFactory.decodeStream(
                                    contentResolver.openInputStream(
                                        contentUri!!
                                    )
                                )
                            )
                            print("error path is $saveImageToInternalStorage")
                            binding?.ivPlaceImage?.setImageURI(contentUri)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(
                                this,
                                "Failed to load image from gallery",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }
            }

    }

    private fun registerOnActivityForCameraResult() {
        cameraImageResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                if (result.resultCode == Activity.RESULT_OK) {

                    val data: Intent? = result.data
                    if (data != null) {
                        try {
                            val thumbNail: Bitmap = result!!.data!!.extras?.get("data") as Bitmap
                            saveImageToInternalStorage = saveImageToInternalStorage(thumbNail)
                            print("error path is $saveImageToInternalStorage")
                            binding?.ivPlaceImage?.setImageBitmap(thumbNail)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(
                                this,
                                "Failed to take photo from Camera",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }
            }

    }


    private fun upDateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding?.etDate?.setText(sdf.format(cal.time).toString())
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)
    }
}