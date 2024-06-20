package com.example.poseexercise.views.graphic

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.mlkit.vision.objects.DetectedObject
import com.example.poseexercise.views.graphic.GraphicOverlay

class ObjectGraphic(
    overlay: GraphicOverlay,
    private val detectedObject: DetectedObject
) : GraphicOverlay.Graphic(overlay) {

    private val rectPaint: Paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8.0f
    }

    private val labelPaint: Paint = Paint().apply {
        color = Color.WHITE
        textSize = 54.0f
    }

    override fun draw(canvas: Canvas) {
        // Draw bounding box around the detected object
        val boundingBox = detectedObject.boundingBox
        canvas.drawRect(boundingBox, rectPaint)

        // Draw labels (if any)
        for (label in detectedObject.labels) {
            val labelText = "${label.text} (${String.format("%.2f", label.confidence)})"
            canvas.drawText(labelText, boundingBox.left.toFloat(), boundingBox.bottom.toFloat(), labelPaint)
        }
    }
}