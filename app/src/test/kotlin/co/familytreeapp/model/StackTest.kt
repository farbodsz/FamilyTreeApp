package co.familytreeapp.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for [Stack].
 */
class StackTest {

    @Test
    fun popsLastItemPushed() {
        val stack = Stack<String>(4).apply {
            push("To")
            push("be")
            push("or")
            push("not")
        }

        val actualValue = stack.pop()
        assertEquals("not", actualValue)
    }

    @Test
    fun isEmptyTest() {
        val stack = Stack<String>(2)
        assertTrue(stack.isEmpty())

        with(stack) {
            push("To")
            push("be")
            pop()
            pop()
        }
        val expectedSize = 0
        assertEquals(expectedSize, stack.size())
        assertTrue(stack.isEmpty())
    }

    @Test
    fun isFullTest() {
        val stack = Stack<String>(3).apply {
            push("Lorem")
            push("ipsum")
            push("dolor")
        }
        val expectedSize = 3
        assertEquals(expectedSize, stack.size())
        assertTrue(stack.isFull())
    }

    @Test
    fun sizeTest() {
        val stack = Stack<String>(5).apply {
            push("Lorem")
            push("ipsum")
            push("dolor")
        }
        val expectedSize = 3
        assertEquals(expectedSize, stack.size())
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun exceptionThrownWhenPoppingEmptyStack() {
        Stack<String>(2).apply {
            push("Hello")
            pop()
            pop()
        }
        fail("Expected an exception to be thrown when popping an empty stack")
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun exceptionThrownWhenPushingOntoFullStack() {
        Stack<String>(2).apply {
            push("To")
            push("be")
            push("or")
        }
        fail("Expected an exception to be thrown when pushing onto a full stack")
    }

}
