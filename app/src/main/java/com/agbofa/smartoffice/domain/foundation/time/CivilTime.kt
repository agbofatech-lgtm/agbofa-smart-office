package com.agbofa.smartoffice.domain.foundation.time

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * User-intended civil time plus the zone used to interpret it.
 *
 * Distinct from an [Instant]. "Tuesday 8 AM" is not an instant until
 * a later authorized phase interprets language into this type.
 */
data class CivilTime(
    val dateTime: LocalDateTime,
    val zone: ZoneId,
) {
    fun toInstant(): Instant = dateTime.atZone(zone).toInstant()
}
