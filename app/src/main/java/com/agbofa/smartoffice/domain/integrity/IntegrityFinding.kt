package com.agbofa.smartoffice.domain.integrity

data class IntegrityFinding(
    val code: IntegrityCode,
    val severity: IntegritySeverity,
    val domain: IntegrityDomain,
    val entityId: String,
    val description: String,
    val evidence: String = "",
) {
    val findingKey: String
        get() = listOf(code.name, domain.name, entityId, evidence).joinToString(":")
}
