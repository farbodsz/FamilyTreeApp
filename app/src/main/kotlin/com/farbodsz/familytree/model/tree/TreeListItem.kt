package com.farbodsz.familytree.model.tree

/**
 * This class can be used when representing items in a tree in a list.
 *
 * @param data  the data for the item, of type [T]
 * @param depth to display a tree like a list, the [depth] of the node must be known so that the UI
 *              can be modified accordingly to show hierarchy.
 */
data class TreeListItem<out T>(val data: T, val depth: Int) {

    init {
        if (depth < 0) {
            throw IllegalArgumentException("the depth of a node in a tree must be at least 0")
        }
    }

}
