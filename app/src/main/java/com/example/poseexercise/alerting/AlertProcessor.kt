package com.example.poseexercise.alerting

import android.content.Context
import android.graphics.PointF
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import com.example.poseexercise.R
//import com.example.poseexercise.facedetector.FaceDetectorProcess
import com.example.poseexercise.posedetector.PoseDetectorProcessor
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.sqrt
import kotlin.math.pow
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlertProcessor(private val context: Context) {

    private var originalLeftEyeDistance = 0.0f
    private var originalRightEyeDistance = 0.0f
    private var originalShoulderDistance = 0.0f

    // 경고를 위한 미디어 플레이어
    private var mediaPlayer1: MediaPlayer? = null
    private var mediaPlayer2: MediaPlayer? = null


    fun setOriginalEyeMeasurements(pose: Pose) {
        val leftEyeInner = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_INNER)?.position
        val leftEyeOuter = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_OUTER)?.position
        val rightEyeInner = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_INNER)?.position
        val rightEyeOuter = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_OUTER)?.position

        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.position
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position

        if (leftEyeInner != null && leftEyeOuter != null && rightEyeInner != null && rightEyeOuter != null && rightShoulder != null && leftShoulder != null) {
            originalLeftEyeDistance = calculateDistance(leftEyeInner, leftEyeOuter)
            originalRightEyeDistance = calculateDistance(rightEyeInner, rightEyeOuter)
            originalShoulderDistance = calculateDistance(leftShoulder, rightShoulder)
            Log.d("AlertProcessor", "Original left eye distance: $originalLeftEyeDistance, right eye distance: $originalRightEyeDistance")
        }
    }



    suspend fun processPose(pose: Pose) {
        withContext(Dispatchers.Default) {
            setOriginalEyeMeasurements(pose) // 이게 없으면 에러남
            val leftEyeInner = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_INNER)?.position
            val leftEyeOuter = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_OUTER)?.position
            val rightEyeInner = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_INNER)?.position
            val rightEyeOuter = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_OUTER)?.position

            val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.position
            val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position

            if (leftEyeInner != null && leftEyeOuter != null && rightEyeInner != null && rightEyeOuter != null) {
                val currentLeftEyeDistance = calculateDistance(leftEyeInner, leftEyeOuter)
                val currentRightEyeDistance = calculateDistance(rightEyeInner, rightEyeOuter)


                Log.d("AlertProcessor", "Current left eye distance: $currentLeftEyeDistance, right eye distance: $currentRightEyeDistance")

                if(currentLeftEyeDistance < 3.0f || currentRightEyeDistance < 3.0f) { // n.nf 형식으로 넣지 않으면 error
                    withContext(Dispatchers.Main){
                        showAlert("전방을 주시하세요.")
                    }

                }


            }
            if(leftEyeInner == null && leftEyeOuter == null && rightEyeInner == null && rightEyeOuter == null  && rightShoulder == null && leftShoulder == null){

                withContext(Dispatchers.Main){
                    showAlert("자리를 이탈하지 마세요.")
                }
            }


        }
    }

    suspend fun processFace(faces: List<Face>) {
        withContext(Dispatchers.Default) {
            for (face in faces) {
                val leftEyeOpenProbability = face.leftEyeOpenProbability ?: 0.0f
                val rightEyeOpenProbability = face.rightEyeOpenProbability ?: 0.0f
                Log.d(
                    "AlertProcessor",
                    "LeftEyeOpenProbability: $leftEyeOpenProbability, RightEyeOpenProbability: $rightEyeOpenProbability"
                )
                if (leftEyeOpenProbability < 0.1f && rightEyeOpenProbability < 0.1f) {
                    withContext(Dispatchers.Main){
                        showAlert("Eyes are closed!")
                    }
                }
            }
        }
    }

    private fun calculateDistance(p1: PointF, p2: PointF): Float {
        return sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))
    }



    private fun showAlert(message: String) {
        Log.d("AlertProcessor", "Showing alert: $message")
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

        if(message == "전방을 주시하세요.") {

            if (mediaPlayer1 == null) {
                mediaPlayer1 = MediaPlayer.create(context, R.raw.jeonbang)
            }
            mediaPlayer1?.start()
        }

        else if(message == "자리를 이탈하지 마세요.") {

            if (mediaPlayer2 == null) {
                mediaPlayer2 = MediaPlayer.create(context, R.raw.yeetal)
            }
            mediaPlayer2?.start()

        }


    }


}