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

fun ClassificationType.displayLabel(): String = asLabel()
fun OperationalState.displayLabel(): String = asLabel()
fun DueStatus.displayLabel(): String = asLabel()
fun DecisionStatus.displayLabel(): String = asLabel()
fun SearchType.displayLabel(): String = asLabel()
fun RecommendationType.displayLabel(): String = asLabel()
fun AnomalyType.displayLabel(): String = asLabel()
fun AdvisorySeverity.displayLabel(): String = asLabel()
