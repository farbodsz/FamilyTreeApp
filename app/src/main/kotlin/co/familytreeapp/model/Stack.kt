package co.familytreeapp.model

/**
 * A simple generic stack class, holding elements of type [E].
 */
class Stack<E>(initialCapacity: Int = DEFAULT_CAPACITY) {

    companion object {
        private const val DEFAULT_CAPACITY = 10
    }

    private var list = MutableList<E?>(initialCapacity) { null } // each item null value by default

    private var pointer = 0

    fun push(element: E) = if (isFull()) {
        throw IndexOutOfBoundsException("the stack is full - cannot push any more elements")
    } else {
        list[pointer++] = element
    }

    fun pop() = if (isEmpty()) {
        throw IndexOutOfBoundsException("the stack is empty - cannot pop any more elements")
    } else {
        list[--pointer]
    }

    fun isEmpty() = pointer == 0

    fun isFull() = pointer == list.size

    fun size() = pointer

}
