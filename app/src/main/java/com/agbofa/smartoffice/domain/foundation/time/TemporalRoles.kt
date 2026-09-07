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

/**
 * When a structured operational record was created.
 *
 * Distinct from capture, journal admission, and classification time.
 * Not a due date or schedule instant.
 */
@JvmInline
value class OperationalCreationInstant(val value: Instant)

/**
 * When an operational state transition was recorded.
 *
 * Distinct from creation time. Not a due date.
 */
@JvmInline
value class OperationalTransitionInstant(val value: Instant)

/**
 * When temporal information was assigned to an OperationalRecord.
 *
 * Distinct from DueInstant and EvaluationInstant.
 */
@JvmInline
value class TemporalAssignmentInstant(val value: Instant)

/**
 * When an operational dependency was recorded.
 *
 * Distinct from operational creation and state transition time.
 */
@JvmInline
value class OperationalDependencyCreationInstant(val value: Instant)

