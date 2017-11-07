package co.familytreeapp.ui.widget

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import co.familytreeapp.R
import co.familytreeapp.model.Person
import co.familytreeapp.model.TreeNode
import co.familytreeapp.ui.dpToPx

/**
 * A custom view responsible for displaying a tree with [Person] data, horizontally.
 *
 * This view class will need to know the number of levels of the tree to display, starting from the
 * given root node.
 */
class TreeView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : View(context, attrs, defStyle) {

    var visibleDepth = 0
        /**
         * The depth of the tree displayed. When [value] = 0, the whole tree is displayed.
         */
        set(value) {
            require(value >= 0) { "visible depth of tree must be at least 0" }
            field = value
            invalidate()
            requestLayout()
        }

    var rootNode: TreeNode<Person>? = null

    init {
        // Obtain attributes from XML
        val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.TreeView,
                0,
                0
        )

        // Assign attributes to fields in this class
        try {
            visibleDepth = typedArray.getInt(R.styleable.TreeView_visibleDepth, 0)
        } finally {
            typedArray.recycle()
        }

        initialiseDrawing()
    }

    private fun initialiseDrawing() {
        if (rootNode == null) {
            return
        }

        val leafNodeCount = rootNode!!.countLeafNodes(if (visibleDepth == 0) null else visibleDepth)

        // Get screen dimensions and use leaf node count to calculate appropriate box dimensions
        val width = Resources.getSystem().displayMetrics.widthPixels
        val boxSpacing = dpToPx(8)
        val boxWidth = (width / leafNodeCount) - (boxSpacing * 2) // left and right spacing
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // TODO
    }

}
