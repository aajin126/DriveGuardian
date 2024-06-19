package com.example.poseexercise.alerting

import android.content.Context
import android.graphics.PointF
import android.media.MediaPlayer
import android.widget.Toast
import com.example.poseexercise.R
//import com.example.poseexercise.facedetector.FaceDetectorProcess
import com.example.poseexercise.posedetector.PoseDetectorProcessor
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.sqrt
import kotlin.math.pow

class AlertProcessor(private val context: Context) {

    private var originalEyeDistance = 0.0f
    private var originalEyeSize = 0.0f

    // 경고를 위한 미디어 플레이어
    private var mediaPlayer: MediaPlayer? = null


    fun setOriginalEyeMeasurements(pose: Pose) {
        val leftEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE)?.position
        val rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE)?.position
        if (leftEye != null && rightEye != null) {
            originalEyeDistance = calculateDistance(leftEye, rightEye)
            originalEyeSize = calculateEyeSize(pose)
        }
    }



    fun processPose(pose: Pose) {
        setOriginalEyeMeasurements(pose)
        val leftEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE)?.position
        val rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE)?.position
        val currentEyeSize = calculateEyeSize(pose)

        if (leftEye != null && rightEye != null) {
            val currentEyeDistance = calculateDistance(leftEye, rightEye)

            if (currentEyeDistance < originalEyeDistance * 0.8 || currentEyeDistance > originalEyeDistance * 1.2) {
                showAlert("Eye distance changed significantly!")
            }

            if (currentEyeSize < originalEyeSize * 0.5 || currentEyeSize > originalEyeSize * 1.5) {
                showAlert("Eye size changed significantly!")
            }
        }
    }

    fun processFace(faces: List<Face>) {
        for (face in faces) {
            val leftEyeOpenProbability = face.leftEyeOpenProbability ?: 0.0f
            val rightEyeOpenProbability = face.rightEyeOpenProbability ?: 0.0f

            if (leftEyeOpenProbability < 0.5f && rightEyeOpenProbability < 0.5f) {
                showAlert("Eyes are closed!")
            }
        }
    }

    private fun calculateDistance(p1: PointF, p2: PointF): Float {
        return sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))
    }



    private fun calculateEyeSize(pose: Pose): Float {
        val leftEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE)?.position
        val rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE)?.position
        return if (leftEye != null && rightEye != null) {
            calculateDistance(leftEye, rightEye)
        } else {
            0.0f
        }
    }

    private fun showAlert(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

        if (mediaPlayer == null) {
            val player = MediaPlayer()
            player.setDataSource("assets/sounds/BbiBbi.mp3")
            player.prepare()
        }
        mediaPlayer?.start()
    }


}