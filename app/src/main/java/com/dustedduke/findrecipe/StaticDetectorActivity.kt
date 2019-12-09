package com.dustedduke.findrecipe

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.dustedduke.findrecipe.tflite.Classifier
import com.dustedduke.findrecipe.tflite.TFLiteObjectDetectionAPIModel
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import java.util.function.Supplier
import java.util.stream.Collectors

class StaticDetectorActivity : AppCompatActivity() {

    private var detector: Classifier? = null
    private val TF_OD_API_INPUT_SIZE = 300
    private val TF_OD_API_IS_QUANTIZED = true
    private val TF_OD_API_MODEL_FILE = "detect.tflite"
    private val TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt"

    internal var mappedRecognitions: MutableList<Classifier.Recognition> = LinkedList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_static_detector)

        val staticDetectorPredictions = findViewById<TextView>(R.id.static_detector_predictions)
        val staticDetectorImage = findViewById<ImageView>(R.id.static_detector_image)

        val findButton = findViewById<Button>(R.id.staticFindButton)
        findButton.setOnClickListener {


            var predictions = mappedRecognitions.map { it.title }


            // TODO ВЕРНУТЬ mapped recognitions
            val returnIntent = Intent()
            returnIntent.putStringArrayListExtra("predictions", ArrayList(predictions))
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }


        var selectedImageUri: Uri = Uri.parse(intent.getStringExtra("imageUri"))


        var cropSize = TF_OD_API_INPUT_SIZE
        try {
            detector = TFLiteObjectDetectionAPIModel.create(
                assets,
                TF_OD_API_MODEL_FILE,
                TF_OD_API_LABELS_FILE,
                TF_OD_API_INPUT_SIZE,
                TF_OD_API_IS_QUANTIZED
            )
            cropSize = TF_OD_API_INPUT_SIZE
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("STATIC DETECTOR ACTIVITY: ", "Exception initializing classifier!")
            val toast = Toast.makeText(
                applicationContext, "Classifier could not be initialized", Toast.LENGTH_SHORT
            )
            toast.show()
            finish()
        }

        if(detector != null) {
            var croppedBitmap: Bitmap? = null
            val rgbBytes = IntArray(cropSize * cropSize)
            val galleryImageBitmap =
                MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)

            // TODO not crop, but scale

            croppedBitmap = Bitmap.createScaledBitmap(galleryImageBitmap, cropSize, cropSize, true)
//            croppedBitmap = Bitmap.createBitmap(galleryImageBitmap, 0,0, cropSize, cropSize)
            mappedRecognitions = detector!!.recognizeImage(croppedBitmap)

            staticDetectorImage.setImageBitmap(croppedBitmap)
            staticDetectorPredictions.setText(mappedRecognitions.map { it.title }.toString())

            Log.d("STATIC DETECTOR ACTIVITY: ", "GOT RESULTS!: " + mappedRecognitions.toString())

        }





    }
}
