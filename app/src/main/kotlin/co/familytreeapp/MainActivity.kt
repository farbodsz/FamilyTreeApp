package co.familytreeapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import co.familytreeapp.model.Gender
import co.familytreeapp.model.Person
import co.familytreeapp.model.TreeNode

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        basicTreeTest()
    }

    private fun basicTreeTest() {
        val root = TreeNode(Person(1, "Grandfather", "S", Gender.MALE))
        root.addChild(TreeNode(Person(2, "Albert", "S", Gender.MALE)))
        root.addChild(TreeNode(Person(3, "Bethany", "S", Gender.FEMALE)))

        val child3 = TreeNode(Person(4, "Camilla", "S", Gender.FEMALE))
        child3.addChild(TreeNode(Person(5, "Daniel", "X", Gender.MALE)))
        child3.addChild(TreeNode(Person(6, "Egbert", "X", Gender.MALE)))

        root.addChild(child3)

        Log.d("Node", "Node: $root")
    }

}
