package com.agbofa.smartoffice.presentation.theme

import com.agbofa.smartoffice.domain.classification.ClassificationType
import com.agbofa.smartoffice.domain.decision.DecisionStatus
import com.agbofa.smartoffice.domain.intelligence.AnomalyType
import com.agbofa.smartoffice.domain.intelligence.RecommendationType
import com.agbofa.smartoffice.domain.operations.DueStatus
import com.agbofa.smartoffice.domain.operations.OperationalState
import com.agbofa.smartoffice.domain.search.SearchType
import com.agbofa.smartoffice.presentation.labels.asLabel

internal fun ClassificationType.toLabel(): String = asLabel()
internal fun OperationalState.toLabel(): String = asLabel()
internal fun DueStatus.toLabel(): String = asLabel()
internal fun DecisionStatus.toLabel(): String = asLabel()
internal fun SearchType.toLabel(): String = asLabel()
internal fun RecommendationType.toLabel(): String = asLabel()
internal fun AnomalyType.toLabel(): String = asLabel()
