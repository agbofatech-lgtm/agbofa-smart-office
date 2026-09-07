package com.agbofa.smartoffice.data.decision

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

class InMemoryDecisionRepository : DecisionRepository {
    private val byId = LinkedHashMap<String, Decision>()

    override fun save(decision: Decision): DomainResult<Decision> {
        if (byId.containsKey(decision.id.value)) {
            return DomainResult.Failure(DomainError.InvalidState("Decision id already exists"))
        }
        byId[decision.id.value] = decision
        return DomainResult.Success(decision)
    }

    override fun findById(id: DecisionId): Decision? = byId[id.value]

    override fun listAll(): List<Decision> =
        byId.values.sortedWith(compareBy<Decision> { it.createdAt.value }.thenBy { it.id.value })
}

class InMemoryDecisionTransitionRepository : DecisionTransitionRepository {
    private val byId = LinkedHashMap<String, DecisionTransition>()

    override fun save(transition: DecisionTransition): DomainResult<DecisionTransition> {
        if (byId.containsKey(transition.id.value)) {
            return DomainResult.Failure(DomainError.InvalidState("Decision transition id already exists"))
        }
        byId[transition.id.value] = transition
        return DomainResult.Success(transition)
    }

    override fun findById(id: DecisionTransitionId): DecisionTransition? = byId[id.value]

    override fun listByDecisionId(decisionId: DecisionId): List<DecisionTransition> =
        DecisionProjection.ordered(byId.values.filter { it.decisionId == decisionId })
}

class InMemoryAuthorizedActionRequestRepository : AuthorizedActionRequestRepository {
    private val byId = LinkedHashMap<String, AuthorizedActionRequest>()

    override fun save(request: AuthorizedActionRequest): DomainResult<AuthorizedActionRequest> {
        if (byId.containsKey(request.id.value)) {
            return DomainResult.Failure(DomainError.InvalidState("Authorized action request id already exists"))
        }
        byId[request.id.value] = request
        return DomainResult.Success(request)
    }

    override fun findById(id: AuthorizedActionRequestId): AuthorizedActionRequest? = byId[id.value]

    override fun listByDecisionId(decisionId: DecisionId): List<AuthorizedActionRequest> =
        byId.values.filter { it.decisionId == decisionId }.sortedWith(
            compareBy<AuthorizedActionRequest> { it.requestedAt.value }.thenBy { it.id.value },
        )
}

class InMemoryAuthorizedActionExecutionRepository : AuthorizedActionExecutionRepository {
    private val byId = LinkedHashMap<String, AuthorizedActionExecution>()

    override fun save(execution: AuthorizedActionExecution): DomainResult<AuthorizedActionExecution> {
        if (byId.containsKey(execution.id.value)) {
            return DomainResult.Failure(DomainError.InvalidState("Authorized action execution id already exists"))
        }
        if (byId.values.any { it.requestId == execution.requestId }) {
            return DomainResult.Failure(DomainError.InvalidState("Action request already executed"))
        }
        byId[execution.id.value] = execution
        return DomainResult.Success(execution)
    }

    override fun findByRequestId(requestId: AuthorizedActionRequestId): AuthorizedActionExecution? =
        byId.values.firstOrNull { it.requestId == requestId }

    override fun listByDecisionId(decisionId: DecisionId): List<AuthorizedActionExecution> =
        byId.values.filter { it.decisionId == decisionId }.sortedWith(
            compareBy<AuthorizedActionExecution> { it.executedAt.value }.thenBy { it.id.value },
        )
}
