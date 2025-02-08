package com.example.gym_workout


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.gym_workout.R
import com.example.gym_workout.database.DataManager
import com.example.gym_workout.database.UserData
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Profile : Fragment() {

    private lateinit var dataManager: DataManager
    private lateinit var profileImageView: ImageView
    private var currentPhotoPath: String? = null

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_PICK_IMAGE = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())
        val userData = dataManager.getUserData()
        profileImageView = view.findViewById(R.id.profileImage)

        userData?.let {
            populateUserData(it, view)
            it.profileImagePath?.let { path ->
                val bitmap = BitmapFactory.decodeFile(path)
                profileImageView.setImageBitmap(bitmap)
            }
        }

        view.findViewById<ImageView>(R.id.editIcon).setOnClickListener {
            showImagePickerDialog()
        }
    }

    private fun populateUserData(user: UserData, view: View) {
        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
        val weightTextView = view.findViewById<TextView>(R.id.weightTextView)
        val heightTextView = view.findViewById<TextView>(R.id.heightTextView)
        val ageTextView = view.findViewById<TextView>(R.id.ageTextView)

        nameTextView.text = "${user.firstName} ${user.lastName}"
        weightTextView.text = "${user.weight} KG"
        heightTextView.text = "${user.height / 100.0} Ft"  // Convert height to feet
        ageTextView.text = "${dataManager.calculateAge(user.dateOfBirth)} Years"

        val metrics = dataManager.calculateMetrics(user)
        // Populate additional metrics as needed
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Choose your profile picture")

        builder.setItems(options) { _, which ->
            when (which) {
                0 -> dispatchTakePictureIntent()
                1 -> dispatchPickPictureIntent()
            }
        }

        builder.show()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.gym_workout.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun dispatchPickPictureIntent() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val file = File(currentPhotoPath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                profileImageView.setImageBitmap(bitmap)
                dataManager.saveProfileImagePath(file.absolutePath)
            }
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri? = data?.data
            selectedImage?.let {
                val filePath = copyUriToFile(selectedImage)
                val bitmap = BitmapFactory.decodeFile(filePath)
                profileImageView.setImageBitmap(bitmap)
                dataManager.saveProfileImagePath(filePath)
            }
        }
    }


    private fun getRealPathFromURI(contentUri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(contentUri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }


    private fun saveImageToDatabase(file: File) {
        // Convert file to byte array
        val byteArray = file.readBytes()

        // Resize the image to avoid OutOfMemoryError
        val resizedImage = resizeImage(byteArray, 800, 800) // Adjust dimensions as needed

        // Save file path to the database using dataManager
        dataManager.saveProfileImagePath(file.absolutePath)
    }

    private fun resizeImage(image: ByteArray, maxWidth: Int, maxHeight: Int): ByteArray {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeByteArray(image, 0, image.size, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size, options)

        // Convert bitmap to byte array
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return outputStream.toByteArray()
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
    private fun copyUriToFile(uri: Uri): String {
        val inputStream = requireContext().contentResolver.openInputStream(uri)!!
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(storageDir, "JPEG_${timeStamp}.jpg")
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return file.absolutePath
    }

}
