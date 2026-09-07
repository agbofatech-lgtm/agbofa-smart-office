package com.agbofa.smartoffice.domain.operations

/**
 * A depends on B is expressed as A REQUIRES B.
 *
 * BLOCKED_BY is not a second type; it would invert the same edge.
 */
enum class OperationalDependencyType {
    REQUIRES,
}
