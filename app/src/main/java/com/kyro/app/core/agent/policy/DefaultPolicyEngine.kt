package com.kyro.app.core.agent.policy

import com.kyro.app.core.agent.tools.AgentTool
import com.kyro.app.core.agent.tools.ToolRisk

class DefaultPolicyEngine : PolicyEngine {
    override fun evaluate(tool: AgentTool): PolicyDecision {
        // High risk or critical tools require approval
        if (tool.metadata.riskLevel == ToolRisk.CRITICAL) {
            return PolicyDecision.RequiresApproval("Action involves critical risk.")
        }
        if (tool.metadata.requiresApproval) {
            return PolicyDecision.RequiresApproval("Tool explicitly requires approval.")
        }
        return PolicyDecision.Allowed
    }
}
