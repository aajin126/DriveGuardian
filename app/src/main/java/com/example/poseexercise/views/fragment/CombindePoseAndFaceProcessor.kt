package com.example.poseexercise.views.fragment

import android.content.Context
import android.graphics.Bitmap
import com.example.poseexercise.facedetector.FaceDetectorProcessor
import com.example.poseexercise.objectdetector.ObjectDetectorProcessor
import com.example.poseexercise.posedetector.PoseDetectorProcessor
import com.example.poseexercise.util.FrameMetadata
import com.example.poseexercise.util.VisionProcessorBase
import com.example.poseexercise.views.graphic.GraphicOverlay
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import com.google.mlkit.vision.objects.DetectedObject
import java.nio.ByteBuffer
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class CombinedPoseAndFaceProcessor(
    context: Context,
    poseDetectorOptions: PoseDetectorOptionsBase,
    shouldShowInFrameLikelihood: Boolean,
    visualizeZ: Boolean,
    rescaleZ: Boolean,
    runClassification: Boolean,
    faceDetectorOptions: FaceDetectorOptions,
    //objectDetectorOptions : ObjectDetectorOptionsBase
) : VisionProcessorBase<CombinedPoseAndFaceProcessor.CombinedResult>(context) {


    private val poseDetectorProcessor: PoseDetectorProcessor
    private val faceDetectorProcessor: FaceDetectorProcessor
    //private val objectDetectorProcessor: ObjectDetectorProcessor
    private val executor: Executor

    init {
        poseDetectorProcessor = PoseDetectorProcessor(
            context,
            poseDetectorOptions,
            shouldShowInFrameLikelihood,
            visualizeZ,
            rescaleZ,
            runClassification,
            true
        )
        faceDetectorProcessor = FaceDetectorProcessor(context, faceDetectorOptions)
        //objectDetectorProcessor = ObjectDetectorProcessor(context, "model.tflite")
        executor = Executors.newSingleThreadExecutor()
    }

    data class CombinedResult(
        val poseWithClassification: PoseDetectorProcessor.PoseWithClassification?,
        val faces: List<Face>?,
        //val detectedObjects: List<DetectedObject>?
    )

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

    override fun detectInImage(image: InputImage): Task<CombinedResult> {
        val poseTask = poseDetectorProcessor.detectInImage(image)
        val faceTask = faceDetectorProcessor.detectInImage(image)
        //val objectTask = objectDetectorProcessor.detectInImage(image)

        return Tasks.whenAllComplete(poseTask, faceTask).continueWith(executor) {
            val poseResult = poseTask.result
            val faceResult = faceTask.result
            //val objectResult = objectTask.result
            CombinedResult(poseResult, faceResult)
        }
    }

    override fun onSuccess(
        result: CombinedResult,
        graphicOverlay: GraphicOverlay
    ) {
        result.poseWithClassification?.let {
            poseDetectorProcessor.onSuccess(it, graphicOverlay)
        }
        result.faces?.let {
            faceDetectorProcessor.onSuccess(it, graphicOverlay)
        }
        /*
        result.detectedObjects?.let {
            objectDetectorProcessor.onSuccess(it, graphicOverlay)
        }*/


    }

    override fun onFailure(e: Exception) {
        poseDetectorProcessor.onFailure(e)
        faceDetectorProcessor.onFailure(e)
        //objectDetectorProcessor.onFailure(e)
    }
}