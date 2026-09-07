package com.agbofa.smartoffice.data.persistence

import com.agbofa.smartoffice.domain.decision.AuthorizedActionExecution
import com.agbofa.smartoffice.domain.decision.AuthorizedActionExecutionRepository
import com.agbofa.smartoffice.domain.decision.AuthorizedActionRequest
import com.agbofa.smartoffice.domain.decision.AuthorizedActionRequestId
import com.agbofa.smartoffice.domain.decision.AuthorizedActionRequestRepository
import com.agbofa.smartoffice.domain.decision.Decision
import com.agbofa.smartoffice.domain.decision.DecisionId
import com.agbofa.smartoffice.domain.decision.DecisionProjection
import com.agbofa.smartoffice.domain.decision.DecisionRepository
import com.agbofa.smartoffice.domain.decision.DecisionTransition
import com.agbofa.smartoffice.domain.decision.DecisionTransitionId
import com.agbofa.smartoffice.domain.decision.DecisionTransitionRepository
import com.agbofa.smartoffice.domain.foundation.error.DomainError
import com.agbofa.smartoffice.domain.foundation.result.DomainResult

class RoomDecisionRepository(
    private val dao: DecisionDao,
) : DecisionRepository {
    override fun save(decision: Decision): DomainResult<Decision> {
        if (dao.findById(decision.id.value) != null) {
            return DomainResult.Failure(DomainError.InvalidState("Decision id already exists"))
        }
        return try {
            dao.insert(decision.toEntity())
            DomainResult.Success(decision)
        } catch (error: Exception) {
            DomainResult.Failure(DomainError.PersistenceFailure(error.message ?: "Failed to persist decision"))
        }
    }

    override fun findById(id: DecisionId): Decision? = dao.findById(id.value)?.toDomain()

    override fun listAll(): List<Decision> =
        dao.list().mapNotNull { it.toDomain() }
            .sortedWith(compareBy<Decision> { it.createdAt.value }.thenBy { it.id.value })
}

class RoomDecisionTransitionRepository(
    private val dao: DecisionTransitionDao,
) : DecisionTransitionRepository {
    override fun save(transition: DecisionTransition): DomainResult<DecisionTransition> {
        if (dao.findById(transition.id.value) != null) {
            return DomainResult.Failure(DomainError.InvalidState("Decision transition id already exists"))
        }
        return try {
            dao.insert(transition.toEntity())
            DomainResult.Success(transition)
        } catch (error: Exception) {
            DomainResult.Failure(
                DomainError.PersistenceFailure(error.message ?: "Failed to persist decision transition"),
            )
        }
    }

    override fun findById(id: DecisionTransitionId): DecisionTransition? = dao.findById(id.value)?.toDomain()

    override fun listByDecisionId(decisionId: DecisionId): List<DecisionTransition> =
        DecisionProjection.ordered(dao.listByDecisionId(decisionId.value).mapNotNull { it.toDomain() })
}

class RoomAuthorizedActionRequestRepository(
    private val dao: AuthorizedActionRequestDao,
) : AuthorizedActionRequestRepository {
    override fun save(request: AuthorizedActionRequest): DomainResult<AuthorizedActionRequest> {
        if (dao.findById(request.id.value) != null) {
            return DomainResult.Failure(DomainError.InvalidState("Authorized action request id already exists"))
        }
        return try {
            dao.insert(request.toEntity())
            DomainResult.Success(request)
        } catch (error: Exception) {
            DomainResult.Failure(
                DomainError.PersistenceFailure(error.message ?: "Failed to persist authorized action request"),
            )
        }
    }

    override fun findById(id: AuthorizedActionRequestId): AuthorizedActionRequest? = dao.findById(id.value)?.toDomain()

    override fun listByDecisionId(decisionId: DecisionId): List<AuthorizedActionRequest> =
        dao.listByDecisionId(decisionId.value).mapNotNull { it.toDomain() }
            .sortedWith(compareBy<AuthorizedActionRequest> { it.requestedAt.value }.thenBy { it.id.value })
}

class RoomAuthorizedActionExecutionRepository(
    private val dao: AuthorizedActionExecutionDao,
) : AuthorizedActionExecutionRepository {
    override fun save(execution: AuthorizedActionExecution): DomainResult<AuthorizedActionExecution> {
        if (dao.findByRequestId(execution.requestId.value) != null) {
            return DomainResult.Failure(DomainError.InvalidState("Action request already executed"))
        }
        return try {
            dao.insert(execution.toEntity())
            DomainResult.Success(execution)
        } catch (error: Exception) {
            DomainResult.Failure(
                DomainError.PersistenceFailure(error.message ?: "Failed to persist action execution"),
            )
        }
    }

    override fun findByRequestId(requestId: AuthorizedActionRequestId): AuthorizedActionExecution? =
        dao.findByRequestId(requestId.value)?.toDomain()

    override fun listByDecisionId(decisionId: DecisionId): List<AuthorizedActionExecution> =
        dao.listByDecisionId(decisionId.value).mapNotNull { it.toDomain() }
            .sortedWith(compareBy<AuthorizedActionExecution> { it.executedAt.value }.thenBy { it.id.value })
}
