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
                return "Opened $appName"
            }
        }

        return "Could not find an app named $appName."
    }

    fun makeCall(contactNumber: String): String {
        val intent = Intent(Intent.ACTION_DIAL) // Safer than ACTION_CALL
        intent.data = Uri.parse("tel:$contactNumber")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        return try {
            context.startActivity(intent)
            "Opened dialer for $contactNumber"
        } catch (e: Exception) {
            "Could not open dialer."
        }
    }

    fun sendSms(contactNumber: String, message: String): String {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("smsto:$contactNumber")
        intent.putExtra("sms_body", message)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        return try {
            context.startActivity(intent)
            "Opened SMS composer for $contactNumber"
        } catch (e: Exception) {
            "Could not open SMS composer."
        }
    }

    fun toggleFlashlight(on: Boolean): String {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraIdList = cameraManager.cameraIdList
            if (cameraIdList.isEmpty()) {
                return "No camera available for flashlight."
            }
            // In a real app we'd check if the camera actually has a flash unit
            val cameraId = cameraIdList[0]
            cameraManager.setTorchMode(cameraId, on)
            if (on) "Turned flashlight on." else "Turned flashlight off."
        } catch (e: Exception) {
            "Could not access the flashlight."
        }
    }

    fun toggleWifi(on: Boolean): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val intent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                "Opened internet settings panel."
            } else {
                @Suppress("DEPRECATION")
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                @Suppress("DEPRECATION")
                wifiManager.isWifiEnabled = on
                if (on) "Turned Wi-Fi on." else "Turned Wi-Fi off."
            }
        } catch (e: Exception) {
             "Could not change Wi-Fi settings."
        }
    }

    fun searchYouTube(query: String): String {
        val intent = Intent(Intent.ACTION_SEARCH)
        intent.setPackage("com.google.android.youtube")
        intent.putExtra("query", query)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        return try {
            context.startActivity(intent)
            "Searched YouTube for $query"
        } catch (e: Exception) {
            "YouTube app is not installed or accessible."
        }
    }
}
