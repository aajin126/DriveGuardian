package com.example.poseexercise.facedetector

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.poseexercise.R
import com.example.poseexercise.alerting.AlertProcessor
import com.example.poseexercise.util.FrameMetadata
import com.example.poseexercise.util.VisionProcessorBase
import com.example.poseexercise.views.fragment.DetectFragment
import com.example.poseexercise.views.graphic.FaceGraphic
import com.example.poseexercise.views.graphic.GraphicOverlay
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.util.Locale

class FaceDetectorProcessor(context: Context, detectorOptions: FaceDetectorOptions?) :
    VisionProcessorBase<List<Face>>(context) {

    private val detector: FaceDetector
    private lateinit var alertProcessor: AlertProcessor

    init {
        val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .enableTracking()
                .build()

        detector = FaceDetection.getClient(options)
        alertProcessor = AlertProcessor(context)


        Log.v(MANUAL_TESTING_LOG, "Face detector options: $options")
    }

    override fun processBitmap(bitmap: Bitmap?, graphicOverlay: GraphicOverlay?) {
        TODO("Not yet implemented")
    }

    override fun processByteBuffer(
        data: ByteBuffer?,
        frameMetadata: FrameMetadata?,
        graphicOverlay: GraphicOverlay?
    ) {
        TODO("Not yet implemented")
    }

    override fun stop() {
        super.stop()
        detector.close()
    }

    public override fun detectInImage(image: InputImage): Task<List<Face>> {
        val taskCompletionSource = TaskCompletionSource<List<Face>>()
        detector.process(image)
            .addOnSuccessListener { faces ->
                Log.d(TAG, "Face detection success. Number of faces detected: ${faces.size}")
                taskCompletionSource.setResult(faces)
            }
            .addOnFailureListener { e ->
                taskCompletionSource.setException(e)
            }
        return taskCompletionSource.task
    }


    public override fun onSuccess(faces: List<Face>, graphicOverlay: GraphicOverlay) {
        GlobalScope.launch(Dispatchers.Main) {
            for (face in faces) {
                graphicOverlay.add(FaceGraphic(graphicOverlay, face))
                logExtrasForTesting(face)
            }
            alertProcessor.processFace(faces)

        }
    }

    public override fun onFailure(e: Exception) {
        Log.e(TAG, "Face detection failed $e")
    }

    companion object {
        private const val TAG = "FaceDetectorProcessor"
        private fun logExtrasForTesting(face: Face?) {
            if (face != null) {
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face bounding box: " + face.boundingBox.flattenToString()
                )
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face Euler Angle X: " + face.headEulerAngleX
                )
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face Euler Angle Y: " + face.headEulerAngleY
                )
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face Euler Angle Z: " + face.headEulerAngleZ
                )
                // All landmarks
                val landMarkTypes = intArrayOf(
                    FaceLandmark.MOUTH_BOTTOM,
                    FaceLandmark.MOUTH_RIGHT,
                    FaceLandmark.MOUTH_LEFT,
                    FaceLandmark.RIGHT_EYE,
                    FaceLandmark.LEFT_EYE,
                    FaceLandmark.RIGHT_EAR,
                    FaceLandmark.LEFT_EAR,
                    FaceLandmark.RIGHT_CHEEK,
                    FaceLandmark.LEFT_CHEEK,
                    FaceLandmark.NOSE_BASE
                )
                val landMarkTypesStrings = arrayOf(
                    "MOUTH_BOTTOM",
                    "MOUTH_RIGHT",
                    "MOUTH_LEFT",
                    "RIGHT_EYE",
                    "LEFT_EYE",
                    "RIGHT_EAR",
                    "LEFT_EAR",
                    "RIGHT_CHEEK",
                    "LEFT_CHEEK",
                    "NOSE_BASE"
                )
                for (i in landMarkTypes.indices) {
                    val landmark = face.getLandmark(landMarkTypes[i])
                    if (landmark == null) {
                        Log.v(
                            MANUAL_TESTING_LOG,
                            "No landmark of type: " + landMarkTypesStrings[i] + " has been detected"
                        )
                    } else {
                        val landmarkPosition = landmark.position
                        val landmarkPositionStr =
                            String.format(Locale.US, "x: %f , y: %f", landmarkPosition.x, landmarkPosition.y)
                        Log.v(
                            MANUAL_TESTING_LOG,
                            "Position for face landmark: " +
                                    landMarkTypesStrings[i] +
                                    " is :" +
                                    landmarkPositionStr
                        )
                    }
                }
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face left eye open probability: " + face.leftEyeOpenProbability
                )
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face right eye open probability: " + face.rightEyeOpenProbability
                )
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face smiling probability: " + face.smilingProbability
                )
                Log.v(
                    MANUAL_TESTING_LOG,
                    "face tracking id: " + face.trackingId
                )
            }
        }
    }
}