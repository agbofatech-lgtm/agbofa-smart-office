package com.agbofa.smartoffice.presentation.navigation

enum class AppDestination(val label: String, val primary: Boolean) {
    DASHBOARD("Home", true),
    JOURNAL("Journal", true),
    DECISION("Decisions", true),
    SEARCH("Search", true),
    ANALYTICS("Analytics", false),
    INTELLIGENCE("Intelligence", false),
    ;

    val shortLabel: String get() = label

    companion object {
        val primaryDestinations: List<AppDestination> = entries.filter { it.primary }
        val insightDestinations: List<AppDestination> = entries.filter { !it.primary }
        val primary: List<AppDestination> get() = primaryDestinations
        val insights: List<AppDestination> get() = insightDestinations
    }
}
