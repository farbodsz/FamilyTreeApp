package co.familytreeapp.ui.widget

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import co.familytreeapp.R
import co.familytreeapp.model.TreeNode
import co.familytreeapp.ui.dpToPx

/**
 * A custom view responsible for displaying a tree with [T] data, horizontally.
 *
 * This view class will need to know the number of levels of the tree to display, starting from the
 * given root node.
 */
class TreeView<T> @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : View(context, attrs, defStyle) {

    companion object {
        const val LOG_TAG = "TreeView"
    }

    /** The root node of the tree being displayed, initially null until set in [setTreeSource]. */
    private var rootNode: TreeNode<T>? = null

    /** The portion of the tree to display. */
    private var visibleDepth = - 1

    /** The number of leaf nodes in the portion of the tree being displayed. */
    private var leafNodeCount = -1

    /** Dimensions (width and height) available for drawing the representation of a node. */
    private lateinit var nodeDimens: Pair<Int, Int>

    /** The paint to be used when drawing the box representing the node */
    private lateinit var nodePaint: Paint

    /**
     * Specifies the source data to use for displaying the tree.
     *
     * @param node  the root node of the tree
     * @param depth the depth from the root node to display. This must be at least 0.
     */
    fun setTreeSource(node: TreeNode<T>, depth: Int) {
//        Log.d(LOG_TAG, "setTreeSource called")
        if (node == rootNode && depth == visibleDepth) {
            // Source has stayed the same - no need to change anything else
            return
        }

        require(depth >= 0) { "visible depth of tree must be at least 0" }

        rootNode = node
        visibleDepth = depth

        leafNodeCount = node.trimTree(depth)

        initialiseDrawing()

        invalidate()
        requestLayout()
    }

    /**
     * @return true if the tree source data has been set.
     *
     * @see [setTreeSource]
     */
    private fun hasTreeSource() = rootNode != null && visibleDepth != -1

    private fun initialiseDrawing() {
//        Log.d(LOG_TAG, "initialiseDrawing called")

        // Cache the calculated dimensions so that they can be used in onDraw()
        nodeDimens = calculateNodeDimensions()

        // Cache the paint object used for drawing, so we don't create on every onDraw()
        nodePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        nodePaint.color = ContextCompat.getColor(context, R.color.black)
    }

    /**
     * @return the width and height available per node for drawing
     */
    private fun calculateNodeDimensions(): Pair<Int, Int> {
//        Log.d(LOG_TAG, "calculateNodeDimensions called")

        val displayMetrics = Resources.getSystem().displayMetrics
        val boxSpacing = dpToPx(8)

        val width = (displayMetrics.widthPixels / leafNodeCount) - (boxSpacing * 2)
        val height = (displayMetrics.heightPixels / visibleDepth) - (boxSpacing * 2)

        return Pair(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

//        Log.d(LOG_TAG, "onDraw called")

        if (!hasTreeSource()) {
            // Can't draw anything, since variables have not been set
            return
        }
        val rootNode = rootNode!! // immutable value to guarantee compiler of non-nullability

        drawNodeAndChildren(rootNode, 0, canvas!!)
    }

    private fun drawNodeAndChildren(node: TreeNode<T>, depth: Int, canvas: Canvas) { // TODO currently doesn't draw padding
        Log.d(LOG_TAG, "drawNodeAndChildren called")

        val totalWidth = calculateTotalNodeWidth(node)
        val actualWidth = nodeDimens.first // actual width of the node representation being drawn

        val padding = dpToPx(8)

        val left = (totalWidth / 2) - (actualWidth / 2) + padding
        val right = (totalWidth / 2) + (actualWidth / 2) - padding
        val top = depth * nodeDimens.second //- padding
        val bottom = (depth + 1) * nodeDimens.second //+ padding

        val rect = Rect(left, top, right, bottom)
        canvas.drawRect(rect, nodePaint)

        for (child in node.getChildren()) {
            drawNodeAndChildren(child, depth + 1, canvas)
        }
    }

    /**
     * Calculates the sum of the widths of the leaf nodes connected to the root [node], recursively.
     *
     * This total width value includes padding // TODO at the moment it doesn't
     */
    private fun calculateTotalNodeWidth(node: TreeNode<T>): Int {
//        Log.d(LOG_TAG, "calculateTotalNodeWidth called")

        var widthTotal = nodeDimens.first

        for (child in node.getChildren()) {
            widthTotal += calculateTotalNodeWidth(child)
        }

        return widthTotal
    }

}
