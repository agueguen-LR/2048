package com.agueguen.clafout1s.game2048.utilities

import androidx.compose.runtime.mutableStateListOf

/**
 * Converts a ByteArray to a SnapshotStateList<Byte> for use in Compose state management.
 */
fun byteArrayToStateList(array: ByteArray) =
	mutableStateListOf<Byte>().apply {
		addAll(array.toList())
	}

/**
 * Converts a SnapshotStateList<Byte> back to a ByteArray for storage or processing.
 */
fun stateListToByteArray(list: List<Byte>): ByteArray =
	list.toByteArray()
