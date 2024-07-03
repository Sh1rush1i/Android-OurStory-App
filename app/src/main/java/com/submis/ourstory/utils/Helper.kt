package com.submis.ourstory.utils

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.submis.ourstory.R
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun String?.generateToken() = "Bearer $this"

// Parse error message from the response body
fun HttpException.parseErrorMessage(): String {
    val errorBodyString = response()?.errorBody()?.string().orEmpty()

    return try {
        val jsonObject = JSONObject(errorBodyString)
        if (jsonObject.has("message")) {
            jsonObject.getString("message")
        } else {
            "No message field in error body."
        }
    } catch (e: JSONException) {
        "Error parsing error body: ${e.message}"
    }
}

/** UI Control **/
// Create and build a custom dialog for displaying messages
fun dialogBuilder(
    context: Context,
    message: String,
    alignment: Int = Gravity.CENTER
): Dialog {
    val dialog = Dialog(context)
    dialog.setCancelable(false)
    dialog.window!!.apply {
        val params: WindowManager.LayoutParams = this.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        attributes.windowAnimations = android.R.transition.fade
        setGravity(Gravity.CENTER)
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    dialog.setContentView(R.layout.custom_dialog_info)
    val tvMessage = dialog.findViewById<TextView>(R.id.message)
    when (alignment) {
        Gravity.CENTER -> tvMessage.gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER
        Gravity.START -> tvMessage.gravity = Gravity.CENTER_VERTICAL or Gravity.START
        Gravity.END -> tvMessage.gravity = Gravity.CENTER_VERTICAL or Gravity.END
    }
    tvMessage.text = message
    return dialog
}

// Show the custom dialog and set up
fun showDialogInfo(
    context: Context,
    message: String,
    alignment: Int = Gravity.CENTER
) {
    val dialog = dialogBuilder(context, message, alignment)
    val btnOk = dialog.findViewById<Button>(R.id.button_ok)
    btnOk.setOnClickListener {
        dialog.dismiss()
    }
    dialog.show()
}

/** DATE INSTANCE **/



fun String?.toRelativeTime(): String {
    if (this.isNullOrEmpty()) return "Unknown"

    val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    val simpleDate = SimpleDateFormat(dateFormat, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }

    val parsedTime = simpleDate.parse(this)?.time ?: return "Unknown"
    val currentTime = System.currentTimeMillis()
    val timeDifference = currentTime - parsedTime

    val minuteInMillis = 60_000L
    val hourInMillis = 60 * minuteInMillis
    val dayInMillis = 24 * hourInMillis
    val monthInMillis = 30 * dayInMillis
    val yearInMillis = 365 * dayInMillis

    return when {
        timeDifference >= yearInMillis -> "${timeDifference / yearInMillis} years ago"
        timeDifference >= monthInMillis -> "${timeDifference / monthInMillis} months ago"
        timeDifference >= dayInMillis -> "${timeDifference / dayInMillis} days ago"
        timeDifference >= hourInMillis -> "${timeDifference / hourInMillis} hours ago"
        timeDifference >= minuteInMillis -> "${timeDifference / minuteInMillis} minutes ago"
        else -> "Just now"
    }
}

/** File Related **/

// Generate new file in media or files directory
fun generateFile(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let { file ->
        File(file, application.resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    val outputDirectory = if (mediaDir != null && mediaDir.exists()) {
        mediaDir
    } else {
        application.filesDir
    }

    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

    return File(outputDirectory, "$timeStamp.jpg")
}

// Generate temporary file in external files directory
fun generateTempFile(context: Context): File {
    val storageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        ?: throw IOException("Unable to access external files directory")

    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

    return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDirectory)
}

// Convert a URI to a File
fun Uri.convertToFile(context: Context): File {
    val contentResolver = context.contentResolver
    val tempFile = generateTempFile(context)

    contentResolver.openInputStream(this)?.use { inputStream ->
        FileOutputStream(tempFile).use { outputStream ->
            val buffer = ByteArray(1024)
            var length: Int

            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
        }
    } ?: throw IOException("Unable to open input stream for URI")

    return tempFile
}


fun File.convertToBitmap(): Bitmap {
    // Check if the file exists and is a regular file
    if (!this.exists() || !this.isFile) {
        throw FileNotFoundException("File does not exist or is not a regular file: ${this.absolutePath}")
    }

    // Decode the file into a Bitmap
    val bitmap = BitmapFactory.decodeFile(this.absolutePath)
        ?: throw IOException("Failed to decode file into a bitmap: ${this.absolutePath}")

    // Check if decoding was successful
    return bitmap
}

