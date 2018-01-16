package co.familytreeapp.model

/**
 * Represents a node in the tree, holding data type [T].
 */
class TreeNode<T>(val data: T) {

    var parent: TreeNode<T>? = null
        private set

    private val children = ArrayList<TreeNode<T>>()

    fun addChild(child: TreeNode<T>) {
        child.parent = this
        children.add(child)
    }

    fun addChildren(children: List<TreeNode<T>>) = children.forEach { addChild(it) }

    fun isRoot() = parent == null

    fun isLeaf() = children.isEmpty()

    fun getChildren() = children  // need this, otherwise list property would be exposed

    /**
     * Removes references to child nodes, essentially making this node a leaf node.
     */
    private fun clearChildren() = children.clear()

    /**
     * Returns this node with all child nodes (recursively) in an ordered list representation.
     *
     * @param depth the depth of the node in the tree currently being visited. This should initially
     *              be 0 (when invoked by functions external to this class).
     * @see TreeListItem
     */
    fun asTreeList(depth: Int = 0): List<TreeListItem<T>> {
        val list = ArrayList<TreeListItem<T>>()
        list.add(TreeListItem(data, depth))

        for (child in children) {
            list.addAll(child.asTreeList(depth + 1))
        }

        return list
    }

    /**
     * Trims the tree so that it is of the specified [depth].
     *
     * @param depth nodes at this depth are cleared of references to their child nodes. This can be
     *              null to indicate that the tree should not be trimmed (i.e. only traversed, with
     *              the number of leaf nodes returned).
     *
     * @return the number of leaf nodes in the trimmed tree
     */
    fun trimTree(depth: Int?) = trimTree(this, 0, depth)

    private fun trimTree(node: TreeNode<T>, nodeDepth: Int, maxDepth: Int?): Int {
        var leafCount = when {
            node.isLeaf() -> 1
            nodeDepth == maxDepth -> {
                node.clearChildren() // part of trimming the tree
                1
            }
            else -> 0
        }

        if (maxDepth == null || nodeDepth < maxDepth) {
            for (child in node.getChildren()) {
                leafCount += trimTree(child, nodeDepth + 1, maxDepth)
            }
        }

        return leafCount
    }

    override fun toString() = data.toString()

    /**
     * @return  a string representation of the node (its data) and its children, recursively, to be
     *          used for debugging purposes.
     */
    fun toStringRecursive(): String {
        var string = "$data -> ("
        for (child in children) {
            string += "[${child.toStringRecursive()}]"
        }
        string += ")"
        return string
    }

}
