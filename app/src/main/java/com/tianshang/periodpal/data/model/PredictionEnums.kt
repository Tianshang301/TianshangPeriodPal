package com.tianshang.periodpal.data.model

enum class ConfidenceLevel(val key: String) {
    HIGH("high"),
    MEDIUM("medium"),
    LOW("low");
    
    companion object {
        fun fromKey(key: String): ConfidenceLevel {
            return entries.find { it.key == key } ?: LOW
        }
    }
}

enum class CycleRegularity(val key: String) {
    REGULAR("regular"),
    SOMEWHAT_REGULAR("somewhat_regular"),
    IRREGULAR("irregular"),
    INSUFFICIENT_DATA("insufficient_data");
    
    companion object {
        fun fromKey(key: String): CycleRegularity {
            return entries.find { it.key == key } ?: INSUFFICIENT_DATA
        }
    }
}

enum class OvulationTestResult(val value: String) {
    POSITIVE("positive"),
    NEGATIVE("negative"),
    UNKNOWN("");
    
    companion object {
        fun fromValue(value: String?): OvulationTestResult {
            return when (value) {
                "positive", "阳性" -> POSITIVE
                "negative", "阴性" -> NEGATIVE
                else -> UNKNOWN
            }
        }
    }
}

enum class CervicalMucusType(val value: String) {
    EGG_WHITE("egg_white"),
    CREAMY("creamy"),
    STICKY("sticky"),
    DRY("dry"),
    UNKNOWN("");
    
    companion object {
        fun fromValue(value: String?): CervicalMucusType {
            return when (value) {
                "egg_white", "蛋清样" -> EGG_WHITE
                "creamy", "乳状" -> CREAMY
                "sticky", "粘稠" -> STICKY
                "dry", "干燥" -> DRY
                else -> UNKNOWN
            }
        }
    }
}
