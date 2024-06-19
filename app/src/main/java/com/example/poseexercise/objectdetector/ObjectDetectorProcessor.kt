package com.example.poseexercise.objectdetector

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.poseexercise.util.FrameMetadata
import com.example.poseexercise.util.VisionProcessorBase
import com.example.poseexercise.views.graphic.ObjectGraphic
import com.example.poseexercise.views.graphic.GraphicOverlay
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
//import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptionsBase
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import java.nio.ByteBuffer
//import java.util.Locale

class ObjectDetectorProcessor(context: Context, modelPath: String) :
    VisionProcessorBase<List<DetectedObject>>(context) {

    private val detector: ObjectDetector

    init {
        // Load the custom model from assets
        val localModel = LocalModel.Builder()
            .setAssetFilePath(modelPath)
            .build()

        // Configure the custom object detector
        val customObjectDetectorOptions =
            CustomObjectDetectorOptions.Builder(localModel)
                .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                .enableClassification()
                .setClassificationConfidenceThreshold(0.5f)
                .setMaxPerObjectLabelCount(3)
                .build()

        detector = ObjectDetection.getClient(customObjectDetectorOptions)
        Log.v(TAG, "Object Detector initialized with model: $modelPath")
    }

    override fun processBitmap(bitmap: Bitmap?, graphicOverlay: GraphicOverlay) {
        bitmap?.let {
            val image = InputImage.fromBitmap(it, 0)
            detectInImage(image)
                .addOnSuccessListener { results -> onSuccess(results, graphicOverlay) }
                .addOnFailureListener { e -> onFailure(e) }
        }
    }

    override fun processByteBuffer(
        data: ByteBuffer?,
        frameMetadata: FrameMetadata?,
        graphicOverlay: GraphicOverlay
    ) {
        data?.let {
            val image = InputImage.fromByteBuffer(
                it,
                frameMetadata!!.width,
                frameMetadata.height,
                frameMetadata.rotation,
                InputImage.IMAGE_FORMAT_NV21
            )
            detectInImage(image)
                .addOnSuccessListener { results -> onSuccess(results, graphicOverlay) }
                .addOnFailureListener { e -> onFailure(e) }
        }
    }

    override fun stop() {
        super.stop()
        detector.close()
    }

    public override fun detectInImage(image: InputImage): Task<List<DetectedObject>> {
        return detector.process(image)
    }

    public override fun onSuccess(objects: List<DetectedObject>, graphicOverlay: GraphicOverlay) {
        for (obj in objects) {
            graphicOverlay.add(ObjectGraphic(graphicOverlay, obj))
            logExtrasForTesting(obj)
        }
    }

    public override fun onFailure(e: Exception) {
        Log.e(TAG, "Object detection failed $e")
    }

    companion object {
        private const val TAG = "ObjectDetectorProcessor"

        private fun logExtrasForTesting(obj: DetectedObject) {
            Log.v(TAG, "Object bounding box: ${obj.boundingBox.flattenToString()}")
            Log.v(TAG, "Object tracking id: ${obj.trackingId}")

            obj.labels.forEach { label ->
                Log.v(TAG, "Label: ${label.text}, Confidence: ${label.confidence}")
            }
        }
    }
}