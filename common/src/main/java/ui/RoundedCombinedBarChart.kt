package ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import com.example.common.R
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class RoundedCombinedBarChart : BarChart {
    constructor(context: Context?) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        readRadiusAttr(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        readRadiusAttr(context, attrs)
    }

    private var currentRadius = 0

    private fun readRadiusAttr(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.RoundedBarChart, 0, 0)
        try {
            currentRadius = a.getDimensionPixelSize(R.styleable.RoundedBarChart_radius, 0)
            setRadius(currentRadius)
        } finally {
            a.recycle()
        }
    }

    fun setRadius(radius: Int) {
        currentRadius = radius
        renderer = RoundedCombinedBarChartRenderer(
            this,
            animator,
            viewPortHandler,
            currentRadius
        )
    }

    private inner class RoundedCombinedBarChartRenderer(
        chart: BarDataProvider?,
        animator: ChartAnimator?,
        viewPortHandler: ViewPortHandler?,
        private val mRadius: Int
    ) : BarChartRenderer(chart, animator, viewPortHandler) {
        private val mBarShadowRectBuffer = RectF()
        private val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.parseColor("#F5F5F5")
        }

        private fun drawRoundedTopRect(
            canvas: Canvas,
            left: Float,
            top: Float,
            right: Float,
            bottom: Float,
            radius: Float,
            paint: Paint
        ) {
            val path = Path()
            path.moveTo(left, bottom)
            path.lineTo(left, top + radius)
            path.arcTo(
                RectF(left, top, left + radius * 2, top + radius * 2),
                180f,
                90f
            )
            path.lineTo(right - radius, top)
            path.arcTo(
                RectF(right - radius * 2, top, right, top + radius * 2),
                270f,
                90f
            )
            path.lineTo(right, bottom)
            path.close()
            canvas.drawPath(path, paint)
        }

        private fun drawRoundedRect(
            canvas: Canvas,
            left: Float,
            top: Float,
            right: Float,
            bottom: Float,
            radius: Float,
            paint: Paint
        ) {
            val path = Path()
            path.addRoundRect(
                RectF(left, top, right, bottom),
                radius,
                radius,
                Path.Direction.CW
            )
            canvas.drawPath(path, paint)
        }

        private fun drawRoundedBottomRect(
            canvas: Canvas,
            left: Float,
            top: Float,
            right: Float,
            bottom: Float,
            radius: Float,
            paint: Paint
        ) {
            val path = Path()
            path.moveTo(left, top)
            path.lineTo(left, bottom - radius)
            path.arcTo(
                RectF(left, bottom - radius * 2, left + radius * 2, bottom),
                180f,
                -90f
            )
            path.lineTo(right - radius, bottom)
            path.arcTo(
                RectF(right - radius * 2, bottom - radius * 2, right, bottom),
                90f,
                -90f
            )
            path.lineTo(right, top)
            path.close()
            canvas.drawPath(path, paint)
        }

        override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
            val trans = mChart.getTransformer(dataSet.axisDependency)
            val barData = mChart.barData
            val dataSets = barData.dataSets

            // Bắt buộc phải có đúng 2 dataset
            if (dataSets.size != 2) {
                throw IllegalStateException("RoundedCombinedBarChart requires exactly 2 datasets (X and Y)")
            }

            // Chỉ vẽ một lần khi xử lý dataset đầu tiên
            if (index != 0) return

            mBarBorderPaint.color = dataSet.barBorderColor
            mBarBorderPaint.strokeWidth =
                com.github.mikephil.charting.utils.Utils.convertDpToPixel(dataSet.barBorderWidth)

            val phaseX = mAnimator.phaseX
            val phaseY = mAnimator.phaseY

            // Dataset 0 = X (phần dưới)
            // Dataset 1 = Y (phần trên)
            val datasetX = dataSets[0]
            val datasetY = dataSets[1]

            // Khởi tạo buffer
            val buffer = mBarBuffers[index]
            buffer.setPhases(phaseX, phaseY)
            buffer.setDataSet(index)
            buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
            buffer.setBarWidth(barData.barWidth)
            buffer.feed(dataSet)
            trans.pointValuesToPixel(buffer.buffer)

            val isSingleColorX = datasetX.colors.size == 1
            val isSingleColorY = datasetY.colors.size == 1

            var j = 0
            while (j < buffer.size()) {
                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                    j += 4
                    continue
                }

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break

                val entryIndex = j / 4
                val entryX = if (entryIndex < datasetX.entryCount) datasetX.getEntryForIndex(entryIndex) else null
                val entryY = if (entryIndex < datasetY.entryCount) datasetY.getEntryForIndex(entryIndex) else null

                val valueX = entryX?.y ?: 0f
                val valueY = entryY?.y ?: 0f

                val left = buffer.buffer[j]
                val right = buffer.buffer[j + 2]
                val bottom = buffer.buffer[j + 3]

                // Trường hợp không có cả X và Y -> vẽ empty bar
                if (valueX == 0f && valueY == 0f) {
                    // Lấy max value từ tất cả các entry để tính chiều cao tương đương
                    var maxValue = 0f
                    for (i in 0 until datasetX.entryCount) {
                        val eX = datasetX.getEntryForIndex(i)
                        val eY = if (i < datasetY.entryCount) datasetY.getEntryForIndex(i) else null
                        val total = (eX?.y ?: 0f) + (eY?.y ?: 0f)
                        if (total > maxValue) maxValue = total
                    }

                    // Nếu tìm được max value, dùng nó để tính chiều cao
                    if (maxValue > 0f) {
                        val xPos = entryX?.x ?: entryY?.x ?: 0f
                        val maxRect = RectF(
                            xPos - barData.barWidth / 2f,
                            0f,
                            xPos + barData.barWidth / 2f,
                            maxValue
                        )
                        trans.rectValueToPixel(maxRect)
                        val top = maxRect.top

                        // Vẽ nền mờ với bo tròn 4 góc (không có viền)
                        drawRoundedRect(c, left, top, right, bottom, mRadius.toFloat(), emptyPaint)
                    }

                    j += 4
                    continue
                }

                // Có ít nhất 1 giá trị
                if (valueX > 0f && valueY > 0f) {
                    // Có cả X và Y
                    // Tính toán vị trí cho X (dưới)
                    val rectX = RectF(
                        entryX!!.x - barData.barWidth / 2f,
                        0f,
                        entryX.x + barData.barWidth / 2f,
                        valueX
                    )
                    trans.rectValueToPixel(rectX)
                    val topX = rectX.top

                    // Tính toán vị trí cho Y (trên)
                    val rectY = RectF(
                        entryY!!.x - barData.barWidth / 2f,
                        valueX,
                        entryY.x + barData.barWidth / 2f,
                        valueX + valueY
                    )
                    trans.rectValueToPixel(rectY)
                    val topY = rectY.top

                    // Vẽ phần X (dưới) - bo tròn ở góc dưới
                    if (!isSingleColorX) {
                        mRenderPaint.color = datasetX.getColor(entryIndex)
                    } else {
                        mRenderPaint.color = datasetX.color
                    }

                    if (datasetX.gradientColor != null) {
                        val gradientColor = datasetX.gradientColor
                        mRenderPaint.shader = LinearGradient(
                            left, bottom, left, topX,
                            gradientColor.startColor,
                            gradientColor.endColor,
                            Shader.TileMode.MIRROR
                        )
                    }

                    drawRoundedBottomRect(c, left, topX, right, bottom, mRadius.toFloat(), mRenderPaint)
                    mRenderPaint.shader = null

                    // Vẽ phần Y (trên) - bo tròn ở góc trên
                    if (!isSingleColorY) {
                        mRenderPaint.color = datasetY.getColor(entryIndex)
                    } else {
                        mRenderPaint.color = datasetY.color
                    }

                    if (datasetY.gradientColor != null) {
                        val gradientColor = datasetY.gradientColor
                        mRenderPaint.shader = LinearGradient(
                            left, topX, left, topY,
                            gradientColor.startColor,
                            gradientColor.endColor,
                            Shader.TileMode.MIRROR
                        )
                    }

                    drawRoundedTopRect(c, left, topY, right, topX, mRadius.toFloat(), mRenderPaint)
                    mRenderPaint.shader = null

                } else if (valueX > 0f) {
                    // Chỉ có X, vẽ từ y=0 với bo tròn cả trên và dưới
                    val rectX = RectF(
                        entryX!!.x - barData.barWidth / 2f,
                        0f,
                        entryX.x + barData.barWidth / 2f,
                        valueX
                    )
                    trans.rectValueToPixel(rectX)
                    val topX = rectX.top

                    if (!isSingleColorX) {
                        mRenderPaint.color = datasetX.getColor(entryIndex)
                    } else {
                        mRenderPaint.color = datasetX.color
                    }

                    if (datasetX.gradientColor != null) {
                        val gradientColor = datasetX.gradientColor
                        mRenderPaint.shader = LinearGradient(
                            left, bottom, left, topX,
                            gradientColor.startColor,
                            gradientColor.endColor,
                            Shader.TileMode.MIRROR
                        )
                    }

                    drawRoundedRect(c, left, topX, right, bottom, mRadius.toFloat(), mRenderPaint)
                    mRenderPaint.shader = null

                } else if (valueY > 0f) {
                    // Chỉ có Y, vẽ từ y=0 với bo tròn cả trên và dưới
                    val rectY = RectF(
                        entryY!!.x - barData.barWidth / 2f,
                        0f,
                        entryY.x + barData.barWidth / 2f,
                        valueY
                    )
                    trans.rectValueToPixel(rectY)
                    val topY = rectY.top

                    if (!isSingleColorY) {
                        mRenderPaint.color = datasetY.getColor(entryIndex)
                    } else {
                        mRenderPaint.color = datasetY.color
                    }

                    if (datasetY.gradientColor != null) {
                        val gradientColor = datasetY.gradientColor
                        mRenderPaint.shader = LinearGradient(
                            left, bottom, left, topY,
                            gradientColor.startColor,
                            gradientColor.endColor,
                            Shader.TileMode.MIRROR
                        )
                    }

                    drawRoundedRect(c, left, topY, right, bottom, mRadius.toFloat(), mRenderPaint)
                    mRenderPaint.shader = null
                }

                j += 4
            }
        }
    }
}