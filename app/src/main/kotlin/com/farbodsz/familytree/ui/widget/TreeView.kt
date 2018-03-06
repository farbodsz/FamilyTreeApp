package com.farbodsz.familytree.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import com.farbodsz.familytree.R
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.model.tree.TreeNode
import com.farbodsz.familytree.util.OnPersonClick
import com.farbodsz.familytree.util.dpToPx

/**
 * A custom view responsible for displaying a tree with [Person] data, horizontally.
 *
 * In layouts, it should be placed inside a [vertical `ScrollView`][ScrollView] and a
 * [HorizontalScrollView] to enable scrolling in both directions.
 */
class TreeView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    /**
     * Width (in pixels) allocated per node for drawing.
     */
    private val NODE_WIDTH = dpToPx(72) // must match the width in widget_person.xml

    /**
     * The default lateral spacing (in pixels) on *each* side of the space allocated per node.
     * I.e. half of the total spacing between adjacent nodes.
     */
    private val NODE_LATERAL_SPACING = dpToPx(4)

    /**
     * Total width (in pixels) allocated per node - the sum of its displayed width and spacing.
     */
    private val NODE_TOTAL_WIDTH = NODE_WIDTH + 2 * NODE_LATERAL_SPACING

    /**
     * Height (in pixels) allocated per node for drawing.
     */
    private val NODE_HEIGHT = dpToPx(96) // must match the height in widget_person.xml

    /**
     * Height (in pixels) allocated for drawing one level of the tree.
     */
    private val LEVEL_MAX_HEIGHT = NODE_HEIGHT + dpToPx(48)


    /**
     * The root node of the tree being displayed, initially null until set in [setTreeSource].
     */
    var rootNode: TreeNode<Person>? = null
        private set

    /**
     * The height of the portion of the tree being drawn.
     */
    var displayedHeight = -1
        private set

    /**
     * The total number of leaf nodes of the part of the tree being drawn.
     */
    private var numberOfLeafNodes = -1

    /**
     * The paint to be used when drawing the box representing the node.
     */
    private lateinit var nodePaint: Paint

    /**
     * The paint to be used when drawing connections (lines) between nodes.
     */
    private lateinit var nodeLinePaint: Paint

    /**
     * Function to be invoked when a [PersonView] has been clicked.
     */
    var onPersonViewClick: OnPersonClick? = null

    init {
        setWillNotDraw(false) // the view doesn't draw on its own
    }

    /**
     * Specifies the source data to use for displaying the tree.
     *
     * @param node              the root node of the tree
     * @param displayedHeight   the height of the tree to display. This can be null to display the
     *                          whole tree (optional parameter with null as default).
     */
    fun setTreeSource(node: TreeNode<Person>, displayedHeight: Int? = null) {
        if (isSameSource(node, displayedHeight)) {
            // Source has stayed the same - no need to change anything else
            return
        }

        rootNode = node
        this.displayedHeight = displayedHeight ?: node.height()

        // Trim the tree to the specified height, and get the number of leaf nodes
        numberOfLeafNodes = node.trimAndCountTree(displayedHeight)

        initialiseDrawing()

        invalidate()
        requestLayout()
    }

    /**
     * Returns whether the node and height are the same as before.
     */
    private fun isSameSource(node: TreeNode<Person>, height: Int? = null): Boolean {
        val isSameRoot = node != rootNode
        val isSameHeight = if (height == null) {
            // The node height is used in setTreeSource if height argument is null
            node.height() == displayedHeight
        } else {
            height == displayedHeight
        }
        return isSameRoot && isSameHeight
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Determine the total width and height required for drawing this tree
        val totalWidth = numberOfLeafNodes * NODE_TOTAL_WIDTH
        val totalHeight = displayedHeight * LEVEL_MAX_HEIGHT

        setMeasuredDimension(totalWidth, totalHeight)
    }

    private fun initialiseDrawing() {
        // Cache the paint object used for drawing, so we don't create on every onDraw()
        nodePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        nodePaint.color = ContextCompat.getColor(context, R.color.black)

        nodeLinePaint = nodePaint
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        rootNode?.let {
            // Only draw if rootNode not null
            drawNodeAndChildren(canvas!!, it)
        }
    }

    /**
     * Draws a representation of a node and its children onto the canvas.
     *
     * @param canvas        the canvas to draw to (parameter from [View.onDraw])
     * @param node          the node to be drawn
     * @param depth         the depth of the node being drawn (0 by default)
     * @param parentXLeft   X coordinate of the left of the space *allocated* for drawing this
     *                      node's parent (0 by default, indicating this node has no parent)
     * @param parentCentre  pair of float coordinates (X then Y) of the bottom centre of this node's
     *                      parent's drawn area. It can be null, indicating no parent.
     * @param childPos      a number representing this (child) node's position in relation to its
     *                      siblings *starting from 0*. (0 is also used by default to indicate this
     *                      node has no parent, i.e. is the root).
     */
    private fun drawNodeAndChildren(canvas: Canvas,
                                    node: TreeNode<Person>,
                                    depth: Int = 0,
                                    parentXLeft: Int = 0,
                                    parentCentre: Pair<Float, Float>? = null,
                                    childPos: Int = 0) {
        val totalAllocatedWidth = node.trimAndCountTree(null) * NODE_TOTAL_WIDTH

        val personView = PersonView(context).apply {
            person = node.data
            setOnClickListener {
                onPersonViewClick?.invoke(this, person!!)
            }
        }
        addView(personView)

        // Calculate allocated coordinates (i.e. total available space)
        val top = (LEVEL_MAX_HEIGHT * depth).toFloat()
        val bottom = top + NODE_HEIGHT
        val allocatedLeft = parentXLeft + (childPos * NODE_TOTAL_WIDTH)
        val allocatedRight = allocatedLeft + totalAllocatedWidth

        // Calculate X coordinates for area to draw in
        val centre = (allocatedLeft + 0.5 * (allocatedRight - allocatedLeft)).toFloat()
        val left = centre - (NODE_TOTAL_WIDTH / 2) + NODE_LATERAL_SPACING

        // Draw node representation
        personView.x = left
        personView.y = top

        // Draw line connection if not root
        parentCentre?.let {
            canvas.drawLine(parentCentre.first, parentCentre.second, centre, top, nodeLinePaint)
        }

        // Repeat for children
        var childPositionCounter = 0
        for (child in node.getChildren()) {
            drawNodeAndChildren(
                    canvas,
                    child,
                    depth + 1,
                    allocatedLeft,
                    Pair(centre, bottom),
                    childPositionCounter
            )

            val leafNodes = child.trimAndCountTree(null)
            childPositionCounter += leafNodes
        }
    }

}
