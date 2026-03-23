package com.example.convapp.engine

import com.example.convapp.model.Node

class ConversationEngine(private val nodes: Map<String, Node>) {

    var currentNode: Node? = null
        private set

    private var retryCount: Int = 0

    /** Called whenever the engine moves to a new node. Must be set before loadScenario(). */
    var onNavigate: ((Node) -> Unit)? = null

    fun loadScenario(startNodeId: String) {
        retryCount = 0
        navigateTo(startNodeId)
    }

    fun navigateTo(nodeId: String) {
        val node = nodes[nodeId] ?: return
        currentNode = node
        retryCount = 0
        onNavigate?.invoke(node)
    }

    /**
     * Called when speech recognition returns results.
     * Only acts if current node is of type "prompt".
     */
    fun handleSpeechResult(alternatives: List<String>) {
        val node = currentNode ?: return
        if (node.type != "prompt") return

        val matched = KeywordMatcher.matches(
            alternatives,
            node.expectedKeywords,
            node.minKeywordsRequired
        )

        if (matched) {
            node.successNode?.let { navigateTo(it) }
        } else {
            retryCount++
            if (retryCount <= node.maxRetries) {
                val nextId = node.retryNode ?: node.nodeId
                navigateTo(nextId)
            } else {
                node.failNode?.let { navigateTo(it) }
                    ?: node.successNode?.let { navigateTo(it) } // fallback: skip ahead
            }
        }
    }

    /** Treat silence/timeout as a failed speech attempt. */
    fun handleNoSpeech() {
        handleSpeechResult(emptyList())
    }

    /**
     * Called after TTS finishes on teach/bridge/celebrate nodes
     * to auto-advance to successNode.
     */
    fun advanceFromAutoNode() {
        val node = currentNode ?: return
        node.successNode?.let { navigateTo(it) }
    }

    fun reset() {
        currentNode = null
        retryCount = 0
    }
}
