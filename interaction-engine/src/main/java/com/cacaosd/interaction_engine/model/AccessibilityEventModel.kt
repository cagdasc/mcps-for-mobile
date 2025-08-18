package com.cacaosd.interaction_engine.model

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.serializers.LocalDateTimeIso8601Serializer
import kotlinx.serialization.Serializable

@Serializable
data class AccessibilityEventModel(
    @Serializable(LocalDateTimeIso8601Serializer::class)
    val timestamp: LocalDateTime,
    val eventType: String,
    val applicationPackage: String? = null,
    val viewClass: String? = null,
    val text: String,
    val source: Source,
    val windowChanges: String
) {
    @Serializable
    data class Source(
        val id: String?,
        val contentDescription: String? = null,
        val isEnabled: Boolean,
        val classname: String? = null,
        val text: String? = null
    )
}
