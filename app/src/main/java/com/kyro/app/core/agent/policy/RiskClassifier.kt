package com.kyro.app.core.agent.policy

import com.kyro.app.core.agent.tools.ToolRisk

class RiskClassifier {
    fun isHighRisk(risk: ToolRisk): Boolean {
        return risk == ToolRisk.HIGH || risk == ToolRisk.CRITICAL
    }
}
