@file:OptIn(ExperimentalUuidApi::class)

package com.cacaosd.mcps_for_mobile.di

import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi

internal object GoogleAgentQualifier : Qualifier {
    override val value: QualifierValue
        get() = TypeQualifier(GoogleAgentQualifier::class).value
}

internal object MetaAgentQualifier : Qualifier {
    override val value: QualifierValue
        get() = TypeQualifier(MetaAgentQualifier::class).value
}

internal object McpMessageFlowQualifier : Qualifier {
    override val value: QualifierValue
        get() = TypeQualifier(McpMessageFlowQualifier::class).value
}

val mainModule = module {
    includes(utilityModule, toolsModule, agentModule)
}
