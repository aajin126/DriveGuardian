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
import java.util.concurrent.TimeUnit

class AlertProcessor(private val context: Context) {

    private var originalLeftEyeDistance = 0.0f
    private var originalRightEyeDistance = 0.0f
    private var originalShoulderDistance = 0.0f

    // 경고를 위한 미디어 플레이어
    private var mediaPlayer1: MediaPlayer? = null
    private var mediaPlayer2: MediaPlayer? = null
    private var mediaPlayer3: MediaPlayer? = null
    private var mediaPlayer4: MediaPlayer? = null

    // 졸린 상태 탐지를 위한 변수
    private var yawningCount = 0
    private var blinkingCount = 0
    private var eyeClosedDuration = 0L
    private var lastBlinkTime = 0L
    private var lastYawnTime = 0L

    // Initialize variables for time tracking
    private var lastUpdateTime = System.currentTimeMillis()
    private var eyeCloseStartTime = 0L


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

                val currentTime = System.currentTimeMillis()
                // Detect yawning
                val mouthOpenProbability = face.smilingProbability ?: 0.0f
                if (mouthOpenProbability > 0.3f) {
                    if (currentTime - lastYawnTime > TimeUnit.SECONDS.toMillis(5)) {
                        yawningCount++
                        lastYawnTime = currentTime
                    }
                }

                // Detect blinking
                val leftEyeOpenProbability = face.leftEyeOpenProbability ?: 1.0f
                val rightEyeOpenProbability = face.rightEyeOpenProbability ?: 1.0f
                if (leftEyeOpenProbability < 0.5f && rightEyeOpenProbability < 0.5f) {
                    if (currentTime - lastBlinkTime > TimeUnit.SECONDS.toMillis(1)) {
                        blinkingCount++
                        lastBlinkTime = currentTime
                    }
                }

                // Detect prolonged eye closure
                if (leftEyeOpenProbability < 0.5f || rightEyeOpenProbability < 0.5f) {
                    if (eyeCloseStartTime == 0L) {
                        eyeCloseStartTime = currentTime
                    }
                    eyeClosedDuration = currentTime - eyeCloseStartTime
                } else {
                    eyeCloseStartTime = 0L
                    eyeClosedDuration = 0L
                }
                
                // detect head tilt(ElerX)
                var eulerX = face.headEulerAngleX
                if(eulerX < -20.0f){
                    Log.d("AlertProcessor", "Head tilt detected! EulerX: $eulerX")
                    withContext(Dispatchers.Main){
                        showAlert("잠에서 깨어나세요!")
                        // 알림 중복 발생 방지를 위한 초기화.
                        eulerX = 0.0f
                    }
                }
                
                // Calculate drowsiness score
                val yawningScore = if (yawningCount >= 1) 0.9f else 0.0f
                val blinkingScore = if (blinkingCount >= 20) 0.3f else 0.0f
                val eyeClosureScore = if (eyeClosedDuration >= TimeUnit.SECONDS.toMillis(15)) 0.4f else 0.0f

                var score = yawningScore + blinkingScore + eyeClosureScore

                Log.d("AlertProcessor", "Yawning count: $yawningCount")
                Log.d("AlertProcessor", "Blinking count: $blinkingCount")
                Log.d("AlertProcessor", "Eye closed duration: $eyeClosedDuration")
                Log.d("AlertProcessor", "Drowsiness score: $score")

                // Trigger alert if score is above threshold
                if (score >= 0.9f) {
                    withContext(Dispatchers.Main){
                        showAlert("가까운 쉼터로 이동하세요.")
                        // 알림 중복 발생 방지를 위한 초기화.
                        yawningCount = 0
                        blinkingCount = 0
                        lastUpdateTime = currentTime
                    }
                }

                // Reset counts every minute
                if (currentTime - lastUpdateTime > TimeUnit.MINUTES.toMillis(1)) {
                    yawningCount = 0
                    blinkingCount = 0
                    lastUpdateTime = currentTime
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

        else if(message == "가까운 쉼터로 이동하세요.") {

            if (mediaPlayer3 == null) {
                mediaPlayer3 = MediaPlayer.create(context, R.raw.drowsiness)
            }
            mediaPlayer3?.start()

        }

        else if(message == "잠에서 깨어나세요!") {

            if (mediaPlayer4 == null) {
                mediaPlayer4 = MediaPlayer.create(context, R.raw.dangerous)
            }
            mediaPlayer4?.start()

        }

    }


}