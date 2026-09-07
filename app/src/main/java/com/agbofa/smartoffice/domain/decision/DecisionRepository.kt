package com.agbofa.smartoffice.domain.decision

import com.agbofa.smartoffice.domain.foundation.result.DomainResult

interface DecisionRepository {
    fun save(decision: Decision): DomainResult<Decision>
    fun findById(id: DecisionId): Decision?
    fun listAll(): List<Decision>
}

interface DecisionTransitionRepository {
    fun save(transition: DecisionTransition): DomainResult<DecisionTransition>
    fun findById(id: DecisionTransitionId): DecisionTransition?
    fun listByDecisionId(decisionId: DecisionId): List<DecisionTransition>
}

interface AuthorizedActionRequestRepository {
    fun save(request: AuthorizedActionRequest): DomainResult<AuthorizedActionRequest>
    fun findById(id: AuthorizedActionRequestId): AuthorizedActionRequest?
    fun listByDecisionId(decisionId: DecisionId): List<AuthorizedActionRequest>
}

interface AuthorizedActionExecutionRepository {
    fun save(execution: AuthorizedActionExecution): DomainResult<AuthorizedActionExecution>
    fun findByRequestId(requestId: AuthorizedActionRequestId): AuthorizedActionExecution?
    fun listByDecisionId(decisionId: DecisionId): List<AuthorizedActionExecution>
}
