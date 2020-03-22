package com.etologic.mahjongscoring2.app.custom_views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

class VerticalTextView(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }
    
    override fun onDraw(canvas: Canvas) {
        val textPaint = paint
        textPaint.color = currentTextColor
        textPaint.drawableState = drawableState
        
        canvas.save()
        
        canvas.translate(width.toFloat(), 0f)
        canvas.rotate(90f)
        
        canvas.translate(compoundPaddingLeft.toFloat(), extendedPaddingTop.toFloat())
        
        layout.draw(canvas)
        canvas.restore()
    }
}