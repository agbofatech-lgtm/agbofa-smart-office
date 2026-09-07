package com.agbofa.smartoffice.domain.operations

/**
 * Why an operational record was created.
 *
 * RULE is provenance-ready only. Phase 6 does not implement a rules engine.
 * AI is excluded.
 */
enum class OperationalCreationBasis {
    MANUAL,
    RULE,
}
