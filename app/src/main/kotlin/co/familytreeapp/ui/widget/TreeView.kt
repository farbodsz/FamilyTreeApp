package co.familytreeapp.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import co.familytreeapp.R
import co.familytreeapp.model.Person
import co.familytreeapp.model.TreeNode

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

    companion object {
        private const val DEFAULT_LEVELS = 3
    }

    private lateinit var testPaint: Paint

    var levelsToDisplay = DEFAULT_LEVELS
        set(value) {
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
            levelsToDisplay = typedArray.getInt(R.styleable.TreeView_levelsToDisplay, DEFAULT_LEVELS)
        } finally {
            typedArray.recycle()
        }

        initialiseDrawing()
    }

    private fun initialiseDrawing() {
        testPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { // TODO
            style = Paint.Style.FILL
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.drawCircle(100f, 100f, 60f, testPaint) // TODO
    }

}
