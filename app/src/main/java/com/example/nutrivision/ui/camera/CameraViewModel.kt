package com.example.nutrivision.ui.camera

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nutrivision.data.BoundingBox

class CameraViewModel : ViewModel(), Detector.DetectorListener {

    private val _detectionResults = MutableLiveData<List<BoundingBox>>()
    val detectionResults: LiveData<List<BoundingBox>> = _detectionResults

    private val _inferenceTime = MutableLiveData<Long>()
    val inferenceTime: LiveData<Long> = _inferenceTime

    private lateinit var detector: Detector

    fun initializeDetector(context: Context) {
//        BIkin if else
        detector = Detector(context, MODEL_PATH, LABEL_PATH, this)
        detector.setup()
    }

    fun detect(bitmap: Bitmap) {
        detector.detect(bitmap)
    }

    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
        _detectionResults.postValue(boundingBoxes)
        _inferenceTime.postValue(inferenceTime)
    }

    override fun onEmptyDetect() {
        _detectionResults.postValue(emptyList())
    }

    override fun onCleared() {
        super.onCleared()
        detector.clear()
    }

//    TOdo()

    companion object {
        private const val MODEL_PATH = "model.tflite"
        private const val MODEL_PATH_FOOD = "model_food.tflite"
        private const val LABEL_PATH = "labels.txt"
        private const val LABEL_PATH_FOOD = "labels_food.txt"
    }

}
