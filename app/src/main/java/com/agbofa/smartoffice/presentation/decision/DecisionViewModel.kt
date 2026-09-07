package com.agbofa.smartoffice.presentation.decision

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.agbofa.smartoffice.application.decision.ApproveDecisionUseCase
import com.agbofa.smartoffice.application.decision.GetDecisionsUseCase
import com.agbofa.smartoffice.application.decision.RejectDecisionUseCase
import com.agbofa.smartoffice.application.decision.WithdrawDecisionUseCase
import com.agbofa.smartoffice.domain.foundation.result.DomainResult
import com.agbofa.smartoffice.domain.foundation.time.DecisionTransitionInstant
import java.time.Instant

class DecisionViewModel(
    private val listDecisions: GetDecisionsUseCase,
    private val approveDecision: ApproveDecisionUseCase,
    private val rejectDecision: RejectDecisionUseCase,
    private val withdrawDecision: WithdrawDecisionUseCase,
) : ViewModel() {
    var state by mutableStateOf(DecisionUiState(loading = true))
        private set

    fun refresh() {
        val items = listDecisions.execute()
        state = DecisionUiState(loading = false, empty = items.isEmpty(), items = items, message = state.message)
    }

    fun approve(decisionId: String, at: Instant) = command(decisionId, "approve") {
        approveDecision.execute("$decisionId:APPROVED:$at", decisionId, DecisionTransitionInstant(at))
    }
    fun reject(decisionId: String, at: Instant) = command(decisionId, "reject") {
        rejectDecision.execute("$decisionId:REJECTED:$at", decisionId, DecisionTransitionInstant(at))
    }
    fun withdraw(decisionId: String, at: Instant) = command(decisionId, "withdraw") {
        withdrawDecision.execute("$decisionId:WITHDRAWN:$at", decisionId, DecisionTransitionInstant(at))
    }

    private fun command(decisionId: String, label: String, block: () -> DomainResult<*>) {
        state = state.copy(message = when (val result = block()) {
            is DomainResult.Success -> "$label $decisionId accepted"
            is DomainResult.Failure -> "$label failed: ${result.error.message}"
        })
        refresh()
    }
}
