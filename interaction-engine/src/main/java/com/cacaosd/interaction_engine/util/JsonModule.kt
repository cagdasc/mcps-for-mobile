package com.cacaosd.interaction_engine.util

import kotlinx.serialization.json.Json

val json = Json {
    prettyPrint = true          // formatted output
    isLenient = false            // allow non-strict JSON
    ignoreUnknownKeys = false    // ignore fields not in your class
    encodeDefaults = true       // include default values in output
}
