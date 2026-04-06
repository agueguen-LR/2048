package com.agueguen.clafout1s.game2048.utilities

import com.agueguen.clafout1s.game2048.database.Score

/**
 * Converts a power of two Byte to its corresponding Long value. 
 * For example, if the input is 11, the output will be 2048, since 2^11 = 2048.
 *
 * @param powerOfTwo The power of two to convert, represented as a Byte.
 * @return The corresponding Long value of 2 raised to the power of the input Byte.
 */
fun powerToBase(powerOfTwo: Byte): Long {
	return 1L shl powerOfTwo.toInt()
}

/**
 * Converts a Long to its corresponding power of 2, as a Byte. 
 * For example, if the input is 2048, the output will be 11, since 2^11 = 2048.
 *
 * @param base The Long value to convert, which should be a power of 2.
 * @return The corresponding power of 2 as a Byte, which is the number of trailing
 */
fun baseToPower(base: Long): Byte {
	return base.countTrailingZeroBits().toByte()
}
