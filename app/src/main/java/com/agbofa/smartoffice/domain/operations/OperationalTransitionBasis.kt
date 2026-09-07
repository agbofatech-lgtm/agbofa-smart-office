package com.agbofa.smartoffice.domain.operations

/**
 * Why a state transition exists.
 *
 * RULE is provenance-ready only. Phase 7 does not implement a rules engine.
 * AI is excluded.
 */
enum class OperationalTransitionBasis {
    MANUAL,
    RULE,
}
