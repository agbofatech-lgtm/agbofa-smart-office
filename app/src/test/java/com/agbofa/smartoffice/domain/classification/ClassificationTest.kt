package com.agbofa.smartoffice.domain.classification

import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.ClassificationInstant
import com.agbofa.smartoffice.domain.journal.JournalEntryId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class ClassificationTest {
    private val at = ClassificationInstant(Instant.parse("2026-09-07T08:10:00Z"))
    private val journalId = (JournalEntryId.of("jrn-1") as DomainResult.Success).value
    private val id = (ClassificationId.of("cls-1") as DomainResult.Success).value

    private fun classification(
        type: ClassificationType = ClassificationType.FOLLOW_UP,
        basis: ClassificationBasis = ClassificationBasis.MANUAL,
        revision: Int = 1,
        ruleVersion: String? = null,
        supersedesId: ClassificationId? = null,
        rawId: ClassificationId = id,
    ): DomainResult<Classification> = Classification.of(
        id = rawId,
        journalEntryId = journalId,
        type = type,
        basis = basis,
        classifiedAt = at,
        revision = revision,
        ruleVersion = ruleVersion,
        supersedesId = supersedesId,
    )

    @Test
    fun ct01ValidManualClassificationSucceeds() {
        val result = classification()
        assertTrue(result is DomainResult.Success)
        val value = (result as DomainResult.Success).value
        assertEquals(ClassificationType.FOLLOW_UP, value.type)
        assertEquals(ClassificationBasis.MANUAL, value.basis)
        assertEquals(1, value.revision)
        assertNull(value.ruleVersion)
        assertNull(value.supersedesId)
        assertEquals(at, value.classifiedAt)
        assertEquals(journalId, value.journalEntryId)
    }

    @Test
    fun ct02BlankClassificationIdRejected() {
        val result = ClassificationId.of("   ")
        assertTrue(result is DomainResult.Failure)
        assertTrue((result as DomainResult.Failure).error is DomainError.ValidationError)
    }

    @Test
    fun ct03ClassificationReferencesJournalEntry() {
        val value = (classification() as DomainResult.Success).value
        assertEquals("jrn-1", value.journalEntryId.value)
        val names = Classification::class.java.declaredFields.map { it.name }
        assertTrue(names.none { it.equals("captureId", ignoreCase = true) })
        assertTrue(names.none { it.equals("originalExpression", ignoreCase = true) })
    }

    @Test
    fun ct04UnclassifiedCannotBeStored() {
        val result = classification(type = ClassificationType.UNCLASSIFIED)
        assertTrue(result is DomainResult.Failure)
    }

    @Test
    fun ct05ManualProvenanceForbidsRuleVersion() {
        val result = classification(ruleVersion = "rule-1")
        assertTrue(result is DomainResult.Failure)
    }

    @Test
    fun ct06RuleProvenanceRequiresRuleVersion() {
        val missing = classification(basis = ClassificationBasis.RULE)
        assertTrue(missing is DomainResult.Failure)
        val ok = classification(basis = ClassificationBasis.RULE, ruleVersion = "rule-1")
        assertTrue(ok is DomainResult.Success)
        assertEquals("rule-1", (ok as DomainResult.Success).value.ruleVersion)
    }

    @Test
    fun ct07ExplicitInstantIsStored() {
        val value = (classification() as DomainResult.Success).value
        assertEquals(Instant.parse("2026-09-07T08:10:00Z"), value.classifiedAt.value)
    }

    @Test
    fun ct08NoPublicMutationPath() {
        val value = (classification() as DomainResult.Success).value
        assertTrue(Classification::class.java.methods.none { it.name.startsWith("setType") })
        assertEquals(ClassificationType.FOLLOW_UP, value.type)
    }

    @Test
    fun revisionMustBePositive() {
        val result = classification(revision = 0)
        assertTrue(result is DomainResult.Failure)
    }

    @Test
    fun reclassificationMustNameSupersededRow() {
        val previous = (ClassificationId.of("cls-0") as DomainResult.Success).value
        val missing = classification(revision = 2)
        assertTrue(missing is DomainResult.Failure)
        val ok = classification(revision = 2, supersedesId = previous)
        assertTrue(ok is DomainResult.Success)
        assertEquals(previous, (ok as DomainResult.Success).value.supersedesId)
    }
}
