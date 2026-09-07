package com.agbofa.smartoffice.domain.operations

import com.agbofa.smartoffice.domain.classification.ClassificationType

/**
 * Structured operational type.
 *
 * Option A: mirrors persistable [ClassificationType] values so creation
 * does not invent a second meaning vocabulary. UNCLASSIFIED is not
 * operationalizable. These labels do not create Task or Payment classes.
 */
enum class OperationalRecordType {
    INFORMATION,
    ACTION,
    FOLLOW_UP,
    FINANCIAL_OBLIGATION,
    FINANCIAL_RECORD,
    COMMITMENT,
    EVENT,
    NOTE,
    ;

    companion object {
        fun from(type: ClassificationType): OperationalRecordType? =
            entries.firstOrNull { it.name == type.name }
    }
}
