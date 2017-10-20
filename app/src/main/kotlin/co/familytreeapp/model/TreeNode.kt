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

    fun getChildren() = children  // need this, otherwise mutable list property would be exposed

    override fun toString(): String {
        var string = "$data -> ("
        for (child in children) {
            string += "[$child]"
        }
        string += ")"
        return string
    }

}
