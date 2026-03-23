package com.example.convapp.engine

object KeywordMatcher {

    /**
     * Checks whether the combined STT alternatives contain enough expected keywords.
     * Uses substring matching so "playing" matches keyword "play".
     * @param alternatives  List of STT result strings (up to 3 alternatives)
     * @param keywords      Expected keyword list from the node
     * @param minRequired   How many distinct keywords must be found (almost always 1)
     */
    fun matches(
        alternatives: List<String>,
        keywords: List<String>,
        minRequired: Int = 1
    ): Boolean {
        if (keywords.isEmpty()) return true          // node expects no specific word
        if (alternatives.isEmpty()) return false

        val combined = alternatives
            .joinToString(" ")
            .lowercase()
            .replace(Regex("[^a-z0-9 ]"), "")

        val count = keywords.count { keyword ->
            combined.contains(keyword.lowercase().trim())
        }
        return count >= minRequired
    }
}
