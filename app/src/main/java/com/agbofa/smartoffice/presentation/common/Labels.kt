package com.agbofa.smartoffice.presentation.common

import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.decision.DecisionStatus
import com.agbofa.smartoffice.domain.intelligence.AdvisorySeverity
import com.agbofa.smartoffice.domain.intelligence.AnomalyType
import com.agbofa.smartoffice.domain.intelligence.RecommendationType
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.search.SearchType
import com.agbofa.smartoffice.presentation.labels.asLabel
import com.agbofa.smartoffice.presentation.theme.toLabel

internal fun ClassificationType.displayLabel(): String = toLabel()
internal fun OperationalState.displayLabel(): String = toLabel()
internal fun DueStatus.displayLabel(): String = toLabel()
internal fun DecisionStatus.displayLabel(): String = asLabel()
internal fun SearchType.displayLabel(): String = toLabel()
internal fun RecommendationType.displayLabel(): String = toLabel()
internal fun AnomalyType.displayLabel(): String = toLabel()
internal fun AdvisorySeverity.displayLabel(): String = asLabel()
