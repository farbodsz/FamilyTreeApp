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
     * Traverses the tree, starting from this node, to calculate the number of its leaf nodes.
     *
     * If a portion of the tree will be traversed, nodes at the [maxDepth] from this node will be
     * considered leaf nodes.
     *
     * @param maxDepth  the depth of the tree being considered during counting of leaf nodes. This
     *                  can be null to indicate that the whole tree should be traversed rather than
     *                  a section of it.
     */
    fun countLeafNodes(maxDepth: Int? = null) = countLeafNodes(this, 0, maxDepth)

    /**
     * @param node      the node to start traversal from
     * @param maxDepth  the depth of the tree being traversed. Null indicates the whole tree should
     *                  be traversed.
     * @param nodeDepth the depth of the node being visited
     *
     * @see countLeafNodes
     */
    private fun countLeafNodes(node: TreeNode<T>, nodeDepth: Int, maxDepth: Int?): Int {
        var leaves = if (node.isLeaf() || nodeDepth == maxDepth) 1 else 0

        if (maxDepth == null || nodeDepth < maxDepth) {
            for (child in node.getChildren()) {
                leaves += countLeafNodes(child, nodeDepth + 1, maxDepth)
            }
        }

        return leaves
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
