package com.orbitalsonic.bubblelevel.widget

import android.content.Context
import android.graphics.*
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.orbitalsonic.bubble.R

class AccelerometerDrawer(context: Context, private val isSimple: Boolean) :ViewDrawer<PointF?> {
    private val pathPaint: Paint
    private val fillPaint: Paint
    private val ballPaint: Paint
    private var path: Path? = null
    private var xPos = 0f
    private var yPos = 0f
    private val center: Point
    private var radius = 0
    override fun layout(width: Int, height: Int) {
        radius = width / 8
        center[width / 2] = height / 2

        if (path == null) {
            val radius = width / 2f - width * 0.03f
            path = Path()
            path!!.moveTo(center.x - radius, center.y.toFloat())
            path!!.lineTo(center.x + radius, center.y.toFloat())
            path!!.moveTo(center.x.toFloat(), center.y - radius)
            path!!.lineTo(center.x.toFloat(), center.y + radius)
            if (!isSimple) {
                path!!.addCircle(center.x.toFloat(), center.y.toFloat(), radius, Path.Direction.CCW)
            }
        }
    }

    override fun draw(canvas: Canvas?) {
        canvas?.apply {
            drawPath(path!!,fillPaint)
            drawPath(path!!,pathPaint) //Draw ball
            drawCircle(center.x-xPos,center.y+yPos,radius.toFloat(),ballPaint)
        }
    }

    override fun update(value: PointF?) {
        xPos = value!!.x
        yPos = value.y
    }

    init {
        val gridColor: Int
        val fillColor: Int
        val ballColor: Int
        val typedValue = TypedValue()
        val theme = context.theme


        gridColor = if (theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true)) {
            typedValue.data
        }else {
            ContextCompat.getColor(context,R.color.white)
        }
        fillColor = if (theme.resolveAttribute(R.attr.themeColorAccent,typedValue,true)) {
            typedValue.data
        }else {
            ContextCompat.getColor(context,R.color.black)
        }
        ballColor = if (theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true)) {
            typedValue.data
        }else {
            ContextCompat.getColor(context,R.color.transparent)
        }
        pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        pathPaint.strokeWidth = 1f
        pathPaint.style = Paint.Style.STROKE
        pathPaint.color = gridColor
        fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        fillPaint.style = Paint.Style.FILL
        fillPaint.color = fillColor
        ballPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        ballPaint.style = Paint.Style.FILL
        ballPaint.color = ballColor
        center = Point(0, 0)
    }
}