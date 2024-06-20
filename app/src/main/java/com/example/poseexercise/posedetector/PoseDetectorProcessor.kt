/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.poseexercise.posedetector

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.poseexercise.R
import com.example.poseexercise.alerting.AlertProcessor
import com.google.android.gms.tasks.Task
import com.google.android.odml.image.MlImage
import com.google.mlkit.vision.common.InputImage
import com.example.poseexercise.views.graphic.GraphicOverlay
import com.example.poseexercise.posedetector.classification.PoseClassifierProcessor
import com.example.poseexercise.util.FrameMetadata
import com.example.poseexercise.util.VisionProcessorBase
import com.example.poseexercise.views.fragment.DetectFragment
import com.example.poseexercise.views.graphic.PoseGraphic
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlinx.coroutines.*


/** A processor to run pose detector. */
class PoseDetectorProcessor(
    private val context: Context,
    options: PoseDetectorOptionsBase,
    private val showInFrameLikelihood: Boolean,
    private val visualizeZ: Boolean,
    private val rescaleZForVisualization: Boolean,
    private val runClassification: Boolean,
    private val isStreamMode: Boolean
) : VisionProcessorBase<PoseDetectorProcessor.PoseWithClassification>(context) {

    private val detector: PoseDetector
    private val classificationExecutor: Executor

    private var poseClassifierProcessor: PoseClassifierProcessor? = null

    private lateinit var alertProcessor: AlertProcessor



    /** Internal class to hold Pose and classification results. */
    class PoseWithClassification(val pose: Pose, val classificationResult: List<String>)

    init {
        detector = PoseDetection.getClient(options)
        classificationExecutor = Executors.newSingleThreadExecutor()
        alertProcessor = AlertProcessor(context)

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


    public override fun detectInImage(image: InputImage): Task<PoseWithClassification> {
        val taskCompletionSource = TaskCompletionSource<PoseDetectorProcessor.PoseWithClassification>()

        detector.process(image)
            .addOnSuccessListener(classificationExecutor) { pose ->
                if (runClassification) {
                    if (poseClassifierProcessor == null) {
                        poseClassifierProcessor = PoseClassifierProcessor(context, isStreamMode)
                    }
                    classificationExecutor.execute {
                        val classificationResult = poseClassifierProcessor!!.getPoseResult(pose)
                        val poseWithClassification = PoseDetectorProcessor.PoseWithClassification(pose, classificationResult)
                        taskCompletionSource.setResult(poseWithClassification)
                    }
                } else {
                    taskCompletionSource.setResult(PoseDetectorProcessor.PoseWithClassification(pose, emptyList()))
                }
            }
            .addOnFailureListener { e ->
                taskCompletionSource.setException(e)
            }

        return taskCompletionSource.task
    }


    public override fun onSuccess(
        poseWithClassification: PoseWithClassification,
        graphicOverlay: GraphicOverlay
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            graphicOverlay.add(
                PoseGraphic(
                    graphicOverlay,
                    poseWithClassification.pose,
                    showInFrameLikelihood,
                    visualizeZ,
                    rescaleZForVisualization,
                    poseWithClassification.classificationResult
                )
            )
            alertProcessor.processPose(poseWithClassification.pose)

        }
    }

    public override fun onFailure(e: Exception) {
        Log.e(TAG, "Pose detection failed!", e)
    }



    override fun isMlImageEnabled(context: Context?): Boolean {
        // Use MlImage in Pose Detection by default, change it to OFF to switch to InputImage.
        return true
    }

    companion object {
        private val TAG = "PoseDetectorProcessor"
    }
}
