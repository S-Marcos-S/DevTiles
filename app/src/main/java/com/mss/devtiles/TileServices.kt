package com.mss.devtiles

import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast

abstract class BaseTileService(private val settingKey: String) : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    protected fun updateTile() {
        val isActive = Settings.Global.getInt(contentResolver, settingKey, 0) != 0
        qsTile.state = if (isActive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        try {
            val currentState = Settings.Global.getInt(contentResolver, settingKey, 0) != 0
            val newState = if (currentState) 0 else 1
            
            val success = Settings.Global.putInt(contentResolver, settingKey, newState)
            if (success) {
                updateTile()
            } else {
                showPermissionError()
            }
        } catch (e: SecurityException) {
            showPermissionError()
        }
    }

    private fun showPermissionError() {
        Toast.makeText(this, "Permissão WRITE_SECURE_SETTINGS necessária", Toast.LENGTH_LONG).show()
        // Opcional: abrir MainActivity para mostrar instruções
    }
}

class DevModeTileService : BaseTileService(Settings.Global.DEVELOPMENT_SETTINGS_ENABLED)
class UsbDebugTileService : BaseTileService(Settings.Global.ADB_ENABLED)

class WifiDebugTileService : BaseTileService("adb_wifi_enabled") {
    // Usamos a string literal pois Settings.Global.ADB_WIFI_ENABLED pode não estar disponível em compile time se minSdk < 30
    // Mas o Android 11+ reconhece.
    
    override fun onClick() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            Toast.makeText(this, "Depuração WiFi requer Android 11+", Toast.LENGTH_SHORT).show()
            return
        }
        super.onClick()
    }
}
