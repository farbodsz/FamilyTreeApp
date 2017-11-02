package co.familytreeapp.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import co.familytreeapp.R
import co.familytreeapp.ui.widget.TreeView

/**
 * Activity to display a [TreeView].
 */
class TreeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tree)

        val treeView = findViewById<TreeView>(R.id.treeView)
        //treeView.rootNode =
    }

}
