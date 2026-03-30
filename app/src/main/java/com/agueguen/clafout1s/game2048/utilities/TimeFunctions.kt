package com.agueguen.clafout1s.game2048.utilities

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Converts a Unix timestamp (seconds since the epoch) to a human-readable date string in the format "dd/MM/yyyy, HH:mm".
 *
 * @param timestamp The Unix timestamp to convert.
 * @return A formatted date string representing the given timestamp.
 */
fun formatUnixTime(timestamp: Long): String {
	val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm")
	return Instant.ofEpochSecond(timestamp)
	.atZone(ZoneId.systemDefault())
	.format(formatter)
}
