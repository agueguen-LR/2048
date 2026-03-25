package com.agueguen.clafout1s.game2048.utilities

/**
 * A view into a ByteArray, allowing for efficient access to a subset of the array without copying.
 *
 * @param array The underlying ByteArray to view.
 * @param offset The starting index in the array for this view.
 * @param length The number of elements in this view.
 * @param stride The step size between elements in the view (default is 1 for contiguous access).
 */
data class ByteView(val array: ByteArray, val offset: Int, val length: Int, val stride: Int = 1) {
  operator fun get(i: Int): Byte {
    return array[offset + i * stride]
  }
}
