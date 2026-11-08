package com.kyro.app.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class KyroAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // This is a skeleton for Module C (Accessibility-based App Automation)
        // Here we can listen for window state changes and perform automated UI actions
        // like clicking buttons, scrolling Instagram reels, or reading screen text.

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.d("KyroAccessibility", "Window state changed: \${event.packageName}")
        }
    }

    override fun onInterrupt() {
        Log.d("KyroAccessibility", "Service interrupted")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("KyroAccessibility", "Service connected")
        // Configure service info here if not fully defined in XML
    }

    // Example automation method to be called when user asks KYRO to click something
    fun clickNodeByText(text: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val foundNodes = rootNode.findAccessibilityNodeInfosByText(text)

        for (node in foundNodes) {
            if (node.isClickable) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                return true
            }

            // Sometimes the parent is the clickable element
            var parent = node.parent
            while (parent != null) {
                if (parent.isClickable) {
                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    return true
                }
                parent = parent.parent
            }
        }
        return false
    }
}
