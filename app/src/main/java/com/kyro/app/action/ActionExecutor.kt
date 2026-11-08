package com.kyro.app.action

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings

class ActionExecutor(private val context: Context) {

    fun openApp(appName: String): String {
        val packageManager = context.packageManager
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        var targetPackage: String? = null
        for (appInfo in packages) {
            val name = packageManager.getApplicationLabel(appInfo).toString()
            if (name.equals(appName, ignoreCase = true) || name.contains(appName, ignoreCase = true)) {
                targetPackage = appInfo.packageName
                break
            }
        }

        if (targetPackage != null) {
            val intent = packageManager.getLaunchIntentForPackage(targetPackage)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return "Opening \$appName"
            }
        }

        return "I could not find an app named \$appName."
    }

    fun makeCall(contactNameOrNumber: String): String {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:\$contactNameOrNumber")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        return try {
            context.startActivity(intent)
            "Calling \$contactNameOrNumber"
        } catch (e: SecurityException) {
            "I need permission to make phone calls."
        }
    }

    fun sendSms(contactNumber: String, message: String): String {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("smsto:\$contactNumber")
        intent.putExtra("sms_body", message)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        return try {
            context.startActivity(intent)
            "Opening SMS app to send message."
        } catch (e: Exception) {
            "Could not open SMS app."
        }
    }

    fun toggleFlashlight(on: Boolean): String {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, on)
            if (on) "Turning flashlight on." else "Turning flashlight off."
        } catch (e: Exception) {
            "I could not access the flashlight."
        }
    }

    fun toggleWifi(on: Boolean): String {
        // Direct Wi-Fi toggling is restricted in newer Android versions
        // We open the settings panel as a fallback if needed
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val intent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                "Opening internet settings."
            } else {
                @Suppress("DEPRECATION")
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                @Suppress("DEPRECATION")
                wifiManager.isWifiEnabled = on
                if (on) "Turning Wi-Fi on." else "Turning Wi-Fi off."
            }
        } catch (e: Exception) {
             "I could not change Wi-Fi settings."
        }
    }

    fun searchYouTube(query: String): String {
        val intent = Intent(Intent.ACTION_SEARCH)
        intent.setPackage("com.google.android.youtube")
        intent.putExtra("query", query)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        return try {
            context.startActivity(intent)
            "Searching YouTube for \$query"
        } catch (e: Exception) {
            "YouTube app is not installed or accessible."
        }
    }
}
