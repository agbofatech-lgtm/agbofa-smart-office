package com.agbofa.smartoffice.domain.foundation.time

import java.time.Instant

/**
 * Distinct instant roles from the Phase 0 time model.
 *
 * These are not a calendar engine. They exist so a capture time cannot
 * be passed where a due time or evaluation time is required.
 *
 * None of these types read the system clock.
 */
@JvmInline
value class CaptureInstant(val value: Instant)

@JvmInline
value class EventInstant(val value: Instant)

@JvmInline
value class DueInstant(val value: Instant)

@JvmInline
value class ScheduleInstant(val value: Instant)

@JvmInline
value class TransitionInstant(val value: Instant)

@JvmInline
value class EvaluationInstant(val value: Instant) {
    fun hasReached(due: DueInstant): Boolean = !value.isBefore(due.value)
}

@JvmInline
value class JournalAdmissionInstant(val value: Instant)

/**
 * When a classification revision was assigned.
 *
 * Distinct from capture time and journal admission time.
 */
@JvmInline
value class ClassificationInstant(val value: Instant)

