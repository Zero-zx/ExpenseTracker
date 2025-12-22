package ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import com.example.common.R
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.buffer.BarBuffer
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.ceil
import kotlin.math.min

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

    private fun readRadiusAttr(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.RoundedBarChart, 0, 0)
        try {
            setRadius(a.getDimensionPixelSize(R.styleable.RoundedBarChart_radius, 0))
        } finally {
            a.recycle()
        }
    }

    fun setRadius(radius: Int) {
        renderer = RoundedCombinedBarChartRenderer(
            this,
            animator,
            viewPortHandler,
            radius
        )
    }

    private inner class RoundedCombinedBarChartRenderer(
        chart: BarDataProvider?,
        animator: ChartAnimator?,
        viewPortHandler: ViewPortHandler?,
        private val mRadius: Int
    ) : BarChartRenderer(chart, animator, viewPortHandler) {
        private val mBarShadowRectBuffer = RectF()

        /**
         * Vẽ hình chữ nhật với góc bo tròn ở trên
         */
        private fun drawRoundedTopRect(
            canvas: Canvas,
            left: Float,
            top: Float,
            right: Float,
            bottom: Float,
            radius: Float,
            paint: android.graphics.Paint
        ) {
            val path = android.graphics.Path()

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

        /**
         * Vẽ hình chữ nhật với góc bo tròn ở dưới
         */
        private fun drawRoundedBottomRect(
            canvas: Canvas,
            left: Float,
            top: Float,
            right: Float,
            bottom: Float,
            radius: Float,
            paint: android.graphics.Paint
        ) {
            val path = android.graphics.Path()

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

            mBarBorderPaint.color = dataSet.barBorderColor
            mBarBorderPaint.strokeWidth =
                com.github.mikephil.charting.utils.Utils.convertDpToPixel(dataSet.barBorderWidth)

            val drawBorder = dataSet.barBorderWidth > 0f

            val phaseX = mAnimator.phaseX
            val phaseY = mAnimator.phaseY

            // Vẽ shadow nếu cần
            if (mChart.isDrawBarShadowEnabled) {
                mShadowPaint.color = dataSet.barShadowColor

                val barData = mChart.barData
                val barWidth = barData.barWidth
                val barWidthHalf = barWidth / 2.0f

                var i = 0
                val count = min(
                    ceil((dataSet.entryCount.toFloat() * phaseX).toDouble()).toInt(),
                    dataSet.entryCount
                )
                while (i < count) {
                    val e = dataSet.getEntryForIndex(i)
                    val x = e.x

                    mBarShadowRectBuffer.left = x - barWidthHalf
                    mBarShadowRectBuffer.right = x + barWidthHalf

                    trans.rectValueToPixel(mBarShadowRectBuffer)

                    if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                        i++
                        continue
                    }

                    if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left)) break

                    mBarShadowRectBuffer.top = mViewPortHandler.contentTop()
                    mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom()

                    drawRoundedTopRect(
                        c,
                        mBarShadowRectBuffer.left,
                        mBarShadowRectBuffer.top,
                        mBarShadowRectBuffer.right,
                        mBarShadowRectBuffer.bottom,
                        mRadius.toFloat(),
                        mShadowPaint
                    )
                    i++
                }
            }

            // Khởi tạo buffer
            val buffer = mBarBuffers[index]
            buffer.setPhases(phaseX, phaseY)
            buffer.setDataSet(index)
            buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
            buffer.setBarWidth(mChart.barData.barWidth)

            buffer.feed(dataSet)
            trans.pointValuesToPixel(buffer.buffer)

            val barData = mChart.barData
            val dataSets = barData.dataSets

            // Kiểm tra xem có đúng 2 dataset không (X và Y)
            val isCombinedMode = dataSets.size == 2 && index < 2

            if (isCombinedMode) {
                // Chế độ Combined: vẽ 2 dataset trong cùng 1 bar
                drawCombinedDataSet(c, dataSet, index, buffer, trans, drawBorder)
            } else {
                // Chế độ thông thường: vẽ từng bar riêng lẻ
                drawNormalDataSet(c, dataSet, buffer, drawBorder)
            }
        }

        private fun drawCombinedDataSet(
            c: Canvas,
            dataSet: IBarDataSet,
            index: Int,
            buffer: BarBuffer,
            trans: com.github.mikephil.charting.utils.Transformer,
            drawBorder: Boolean
        ) {
            val barData = mChart.barData
            val dataSets = barData.dataSets

            // Dataset 0 = X (phần dưới, màu xanh lá)
            // Dataset 1 = Y (phần trên, màu đỏ/hồng)
            val datasetX = dataSets[0]
            val datasetY = dataSets[1]

            val isSingleColorX = datasetX.colors.size == 1
            val isSingleColorY = datasetY.colors.size == 1

            // Chỉ vẽ một lần khi xử lý dataset đầu tiên
            if (index == 0) {
                var j = 0
                while (j < buffer.size()) {
                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                        j += 4
                        continue
                    }

                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break

                    // Lấy giá trị từ cả 2 dataset cho cùng 1 index
                    val entryIndex = j / 4
                    val entryX = datasetX.getEntryForIndex(entryIndex)
                    val entryY = if (entryIndex < datasetY.entryCount) {
                        datasetY.getEntryForIndex(entryIndex)
                    } else {
                        null
                    }

                    val valueX = entryX?.y ?: 0f
                    val valueY = entryY?.y ?: 0f

                    // Tính toán vị trí pixel
                    val left = buffer.buffer[j]
                    val right = buffer.buffer[j + 2]
                    val bottom = buffer.buffer[j + 3]

                    // Tạo buffer riêng cho từng phần
                    val bufferX = floatArrayOf(left, 0f, right, bottom)
                    val bufferY = floatArrayOf(left, 0f, right, 0f)

                    // Chuyển đổi giá trị Y
                    val rectX = RectF(entryX.x - barData.barWidth / 2f, 0f, entryX.x + barData.barWidth / 2f, valueX)
                    trans.rectValueToPixel(rectX)

                    val topX = rectX.top
                    bufferX[1] = topX

                    // Vẽ phần X (dưới cùng với góc bo tròn ở dưới)
                    if (!isSingleColorX) {
                        mRenderPaint.color = datasetX.getColor(entryIndex)
                    } else {
                        mRenderPaint.color = datasetX.color
                    }

                    // Xử lý gradient cho X nếu có
                    if (datasetX.gradientColor != null) {
                        val gradientColor = datasetX.gradientColor
                        mRenderPaint.shader = LinearGradient(
                            left, bottom, left, topX,
                            gradientColor.startColor,
                            gradientColor.endColor,
                            Shader.TileMode.MIRROR
                        )
                    }

                    // Vẽ phần Y nếu có
                    if (valueY > 0 && entryY != null) {
                        val rectY = RectF(entryY.x - barData.barWidth / 2f, valueX, entryY.x + barData.barWidth / 2f, valueX + valueY)
                        trans.rectValueToPixel(rectY)

                        val topY = rectY.top
                        bufferY[1] = topY
                        bufferY[3] = topX

                        // Vẽ phần X (dưới) - không có góc bo tròn nếu có Y
                        c.drawRect(left, topX, right, bottom, mRenderPaint)

                        // Vẽ phần Y (trên với góc bo tròn ở trên)
                        if (!isSingleColorY) {
                            mRenderPaint.color = datasetY.getColor(entryIndex)
                        } else {
                            mRenderPaint.color = datasetY.color
                        }

                        // Xử lý gradient cho Y nếu có
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

                        // Reset shader
                        mRenderPaint.shader = null

                    } else {
                        // Chỉ có X, vẽ với góc bo tròn ở trên
                        drawRoundedTopRect(c, left, topX, right, bottom, mRadius.toFloat(), mRenderPaint)
                        mRenderPaint.shader = null
                    }

                    j += 4
                }
            }
        }

        private fun drawNormalDataSet(
            c: Canvas,
            dataSet: IBarDataSet,
            buffer: BarBuffer,
            drawBorder: Boolean
        ) {
            val isSingleColor = dataSet.colors.size == 1

            if (isSingleColor) {
                mRenderPaint.color = dataSet.color
            }

            var j = 0
            while (j < buffer.size()) {
                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                    j += 4
                    continue
                }

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break

                if (!isSingleColor) {
                    mRenderPaint.color = dataSet.getColor(j / 4)
                }

                if (dataSet.gradientColor != null) {
                    val gradientColor = dataSet.gradientColor
                    mRenderPaint.shader = LinearGradient(
                        buffer.buffer[j],
                        buffer.buffer[j + 3],
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        gradientColor.startColor,
                        gradientColor.endColor,
                        Shader.TileMode.MIRROR
                    )
                }

                if (dataSet.gradientColors != null) {
                    mRenderPaint.shader = LinearGradient(
                        buffer.buffer[j],
                        buffer.buffer[j + 3],
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        dataSet.getGradientColor(j / 4).startColor,
                        dataSet.getGradientColor(j / 4).endColor,
                        Shader.TileMode.MIRROR
                    )
                }

                drawRoundedTopRect(
                    c,
                    buffer.buffer[j],
                    buffer.buffer[j + 1],
                    buffer.buffer[j + 2],
                    buffer.buffer[j + 3],
                    mRadius.toFloat(),
                    mRenderPaint
                )

                if (drawBorder) {
                    drawRoundedTopRect(
                        c,
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        buffer.buffer[j + 2],
                        buffer.buffer[j + 3],
                        mRadius.toFloat(),
                        mBarBorderPaint
                    )
                }
                j += 4
            }
        }
    }
}
