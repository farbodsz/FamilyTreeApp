package co.familytreeapp.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import co.familytreeapp.R
import co.familytreeapp.model.TreeNode
import co.familytreeapp.ui.dpToPx

/**
 * A custom view responsible for displaying a tree with [T] data, horizontally.
 *
 * In layouts, it should be placed inside a [vertical `ScrollView`][ScrollView] and a
 * [HorizontalScrollView] to enable scrolling in both directions.
 */
class TreeView<T> @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : View(context, attrs, defStyle) {

    companion object {
        private const val LOG_TAG = "TreeView"
    }

    /** The default width (in pixels) allocated per node for drawing. */
    private val NODE_WIDTH = dpToPx(48)

    /** The default height (in pixels) allocated for drawing one level of the tree */
    private val LEVEL_MAX_HEIGHT = dpToPx(72)

    /**
     * The default lateral spacing (in pixels) on *each* side of the space allocated per node.
     * I.e. half of the total spacing between adjacent nodes.
     */
    private val NODE_LATERAL_SPACING = dpToPx(8)

    /**
     * The total width (in pixels) allocated per node - the sum of its displayed width and spacing
     */
    private val NODE_TOTAL_WIDTH = NODE_WIDTH + 2 * NODE_LATERAL_SPACING


    /** The root node of the tree being displayed, initially null until set in [setTreeSource]. */
    private var rootNode: TreeNode<T>? = null

    /** The height of the portion of the tree being drawn */
    private var displayedHeight = -1

    /** The total number of leaf nodes of the part of the tree being drawn */
    private var numberOfLeafNodes = -1

    /** The paint to be used when drawing the box representing the node */
    private lateinit var nodePaint: Paint

    /**
     * Specifies the source data to use for displaying the tree.
     *
     * @param node              the root node of the tree
     * @param displayedHeight   the height of the tree to display
     */
    fun setTreeSource(node: TreeNode<T>, displayedHeight: Int) {
//        Log.d(LOG_TAG, "setTreeSource called")
        if (node == rootNode) {
            // Source has stayed the same - no need to change anything else
            return
        }

        rootNode = node
        this.displayedHeight = displayedHeight

        numberOfLeafNodes = node.trimAndCountTree(displayedHeight) // trim the tree to the specified height

        initialiseDrawing()

        invalidate()
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Determine the total width and height required for drawing this tree
        val totalWidth = numberOfLeafNodes * NODE_TOTAL_WIDTH
        val totalHeight = displayedHeight * LEVEL_MAX_HEIGHT

        setMeasuredDimension(totalWidth, totalHeight)
    }

    private fun initialiseDrawing() {
//        Log.d(LOG_TAG, "initialiseDrawing called")

        // Cache the paint object used for drawing, so we don't create on every onDraw()
        nodePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        nodePaint.color = ContextCompat.getColor(context, R.color.black)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        Log.d(LOG_TAG, "onDraw called")

        rootNode?.let {
            // Only draw if rootNode not null
            drawNodeAndChildren(canvas!!, it)
        }
    }

    /**
     * Draws a representation of a node and its children onto the canvas.
     *
     * @param canvas    the canvas to draw to (parameter from [View.onDraw])
     * @param node      the node to be drawn
     * @param depth     the depth of the node being drawn (0 by default)
     * @param parentX   X coordinate of the left of the space *allocated* for drawing this [node]'s
     *                  parent (0 by default, indicating this node has no parent, i.e. is the root)
     * @param childPos  a number representing this (child) node's position in relation to its
     *                  siblings *starting from 0*. (0 is also used by default to indicate this node
     *                  has no parent, i.e. is the root).
     *
     * @return the total width (in pixels) allocated for drawing this [node]
     */
    private fun drawNodeAndChildren(canvas: Canvas,
                                    node: TreeNode<T>,
                                    depth: Int = 0,
                                    parentX: Int = 0,
                                    childPos: Int = 0): Int { // TODO currently doesn't draw padding
        Log.d(LOG_TAG, "drawNodeAndChildren called, with: depth=$depth; parentX=$parentX; childPos=$childPos")

        val totalAllocatedWidth = node.trimAndCountTree(null) * NODE_TOTAL_WIDTH

        val top = LEVEL_MAX_HEIGHT * depth
        val bottom = top + dpToPx(32) // TODO resize automatically based on contents of node

        val left = parentX + (childPos * NODE_TOTAL_WIDTH)
        val right = left + totalAllocatedWidth

        val rect = Rect(left + 10, top, right - 10, bottom)
        canvas.drawRect(rect, nodePaint)

        var childPositionCounter = 0
        for (child in node.getChildren()) {
            drawNodeAndChildren(canvas, child, depth + 1, left, childPositionCounter)

            val leafNodes = child.trimAndCountTree(null)
            childPositionCounter += leafNodes
        }

        return totalAllocatedWidth
    }

}
