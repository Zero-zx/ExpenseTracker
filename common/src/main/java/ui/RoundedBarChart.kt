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
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.model.GradientColor
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.ceil
import kotlin.math.min


class RoundedBarChart : BarChart {
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
        val a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundedBarChart, 0, 0)
        try {
            setRadius(a.getDimensionPixelSize(R.styleable.RoundedBarChart_radius, 0))
        } finally {
            a.recycle()
        }
    }

    fun setRadius(radius: Int) {
        setRenderer(
            RoundedBarChartRenderer(
                this,
                getAnimator(),
                getViewPortHandler(),
                radius
            )
        )
    }

    private inner class RoundedBarChartRenderer(
        chart: BarDataProvider?,
        animator: ChartAnimator?,
        viewPortHandler: ViewPortHandler?,
        private val mRadius: Int
    ) : BarChartRenderer(chart, animator, viewPortHandler) {
        private val mBarShadowRectBuffer = RectF()

        /**
         * Draws a rectangle with rounded corners only at the top
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

            // Start from bottom-left (sharp corner)
            path.moveTo(left, bottom)

            // Line to top-left (before the curve)
            path.lineTo(left, top + radius)

            // Top-left rounded corner
            path.arcTo(
                RectF(left, top, left + radius * 2, top + radius * 2),
                180f,
                90f
            )

            // Line across the top
            path.lineTo(right - radius, top)

            // Top-right rounded corner
            path.arcTo(
                RectF(right - radius * 2, top, right, top + radius * 2),
                270f,
                90f
            )

            // Line to bottom-right (sharp corner)
            path.lineTo(right, bottom)

            // Close the path
            path.close()

            canvas.drawPath(path, paint)
        }

        override fun drawHighlighted(
            c: Canvas,
            indices: Array<com.github.mikephil.charting.highlight.Highlight>
        ) {
            val barData: BarData = mChart.getBarData()

            for (high in indices) {
                val set: IBarDataSet? = barData.getDataSetByIndex(high.getDataSetIndex())

                if (set == null || !set.isHighlightEnabled()) continue

                val e: BarEntry = set.getEntryForXValue(high.getX(), high.getY())

                if (!isInBoundsX(e, set)) continue

                val trans: com.github.mikephil.charting.utils.Transformer =
                    mChart.getTransformer(set.getAxisDependency())

                mHighlightPaint.setColor(set.getHighLightColor())
                mHighlightPaint.setAlpha(set.getHighLightAlpha())

                val isStack = high.getStackIndex() >= 0 && e.isStacked()

                val y1: Float
                val y2: Float

                if (isStack) {
                    if (mChart.isHighlightFullBarEnabled()) {
                        y1 = e.getPositiveSum()
                        y2 = -e.getNegativeSum()
                    } else {
                        val range: com.github.mikephil.charting.highlight.Range =
                            e.getRanges()[high.getStackIndex()]

                        y1 = range.from
                        y2 = range.to
                    }
                } else {
                    y1 = e.getY()
                    y2 = 0f
                }

                prepareBarHighlight(e.getX(), y1, y2, barData.getBarWidth() / 2f, trans)

                setHighlightDrawPos(high, mBarRect)

                drawRoundedTopRect(
                    c,
                    mBarRect.left,
                    mBarRect.top,
                    mBarRect.right,
                    mBarRect.bottom,
                    mRadius.toFloat(),
                    mHighlightPaint
                )
            }
        }

        override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
            val trans: com.github.mikephil.charting.utils.Transformer =
                mChart.getTransformer(dataSet.getAxisDependency())

            mBarBorderPaint.setColor(dataSet.getBarBorderColor())
            mBarBorderPaint.setStrokeWidth(
                com.github.mikephil.charting.utils.Utils.convertDpToPixel(
                    dataSet.getBarBorderWidth()
                )
            )

            val drawBorder = dataSet.getBarBorderWidth() > 0f

            val phaseX: Float = mAnimator.getPhaseX()
            val phaseY: Float = mAnimator.getPhaseY()

            // draw the bar shadow before the values
            if (mChart.isDrawBarShadowEnabled()) {
                mShadowPaint.setColor(dataSet.getBarShadowColor())

                val barData: BarData = mChart.getBarData()

                val barWidth: Float = barData.getBarWidth()
                val barWidthHalf = barWidth / 2.0f
                var x: Float

                var i = 0
                val count = min(
                    (ceil(((dataSet.getEntryCount()).toFloat() * phaseX).toDouble())).toInt(),
                    dataSet.getEntryCount()
                )
                while (i < count
                ) {
                    val e: BarEntry = dataSet.getEntryForIndex(i)

                    x = e.getX()

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

            // initialize the buffer
            val buffer: BarBuffer = mBarBuffers[index]
            buffer.setPhases(phaseX, phaseY)
            buffer.setDataSet(index)
            buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()))
            buffer.setBarWidth(mChart.getBarData().getBarWidth())

            buffer.feed(dataSet)

            trans.pointValuesToPixel(buffer.buffer)

            val isSingleColor = dataSet.getColors().size == 1

            if (isSingleColor) {
                mRenderPaint.setColor(dataSet.getColor())
            }

            var j = 0
            while (j < buffer.size()) {
                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                    j += 4
                    continue
                }

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break

                if (!isSingleColor) {
                    // Set the color for the currently drawn value. If the index
                    // is out of bounds, reuse colors.
                    mRenderPaint.setColor(dataSet.getColor(j / 4))
                }

                if (dataSet.getGradientColor() != null) {
                    val gradientColor: GradientColor = dataSet.getGradientColor()
                    mRenderPaint.setShader(
                        LinearGradient(
                            buffer.buffer[j],
                            buffer.buffer[j + 3],
                            buffer.buffer[j],
                            buffer.buffer[j + 1],
                            gradientColor.getStartColor(),
                            gradientColor.getEndColor(),
                            Shader.TileMode.MIRROR
                        )
                    )
                }

                if (dataSet.getGradientColors() != null) {
                    mRenderPaint.setShader(
                        LinearGradient(
                            buffer.buffer[j],
                            buffer.buffer[j + 3],
                            buffer.buffer[j],
                            buffer.buffer[j + 1],
                            dataSet.getGradientColor(j / 4).getStartColor(),
                            dataSet.getGradientColor(j / 4).getEndColor(),
                            Shader.TileMode.MIRROR
                        )
                    )
                }

                // Draw bar with rounded top corners only
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