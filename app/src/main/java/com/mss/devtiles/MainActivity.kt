package com.mss.devtiles

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.net.Inet4Address

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            DevTilesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var isPermissionGranted by remember { 
        mutableStateOf(checkPermission(context)) 
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        PermissionStatusCard(isPermissionGranted)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (!isPermissionGranted) {
            AdbInstructionsSection(context)
        } else {
            WifiDebugDetailsSection(context)
            Spacer(modifier = Modifier.height(16.dp))
            LegacyModeSection(context)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Tudo pronto! Você já pode adicionar os botões no seu painel de configurações rápidas.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { isPermissionGranted = checkPermission(context) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verificar Novamente")
        }
    }
}

@Composable
fun PermissionStatusCard(granted: Boolean) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val containerColor = if (granted) {
        if (isDark) Color(0xFF1B5E20) else Color(0xFFE8F5E9)
    } else {
        if (isDark) Color(0xFFB71C1C) else Color(0xFFFFEBEE)
    }
    val contentColor = if (granted) {
        if (isDark) Color(0xFFC8E6C9) else Color(0xFF2E7D32)
    } else {
        if (isDark) Color(0xFFFFCDD2) else Color(0xFFC62828)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (granted) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.perm_status_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (granted) stringResource(id = R.string.perm_granted) 
                           else stringResource(id = R.string.perm_not_granted),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun AdbInstructionsSection(context: Context) {
    val adbCommand = stringResource(id = R.string.adb_command)
    
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = stringResource(id = R.string.perm_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.adb_instruction),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = adbCommand,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextButton(
            onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("ADB Command", adbCommand)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Comando copiado!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(id = R.string.copy_command))
        }
    }
}

@Composable
fun LegacyModeSection(context: Context) {
    val termuxLabel = stringResource(id = R.string.termux_instruction_label)
    val termuxCmd = stringResource(id = R.string.termux_command_value)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.legacy_mode_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.legacy_mode_desc),
                style = MaterialTheme.typography.bodySmall
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = {
                    try {
                        Settings.Global.putInt(context.contentResolver, "adb_wifi_enabled", 1)
                        Settings.Global.putInt(context.contentResolver, "adb_wifi_port", 5555)
                        Toast.makeText(context, "Tentando ativar porta 5555...", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.activate_5555))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = termuxLabel,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = termuxCmd,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Termux Command", termuxCmd)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Comando copiado!", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copiar comando",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun WifiDebugDetailsSection(context: Context) {
    var isWifiDebugEnabled by remember { 
        mutableStateOf(Settings.Global.getInt(context.contentResolver, "adb_wifi_enabled", 0) != 0) 
    }
    
    DisposableEffect(Unit) {
        val observer = object : android.database.ContentObserver(android.os.Handler(android.os.Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                isWifiDebugEnabled = Settings.Global.getInt(context.contentResolver, "adb_wifi_enabled", 0) != 0
            }
        }
        context.contentResolver.registerContentObserver(
            Settings.Global.getUriFor("adb_wifi_enabled"),
            false,
            observer
        )
        onDispose {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }

    if (isWifiDebugEnabled) {
        val ipAddress = getIpAddress(context)
        val adbPort = getAdbWifiPort(context)
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.wifi_debug_details),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(id = R.string.ip_address, ipAddress))
                Text(text = stringResource(id = R.string.adb_port, adbPort))
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        try {
                            val intent = Intent("android.settings.WIFI_ADB_SETTINGS")
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, R.string.pairing_not_found, Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.open_pairing_screen))
                }
            }
        }
    }
}

fun getIpAddress(context: Context): String {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return "N/A"
    val lp = cm.getLinkProperties(network) ?: return "N/A"
    for (addr in lp.linkAddresses) {
        val address = addr.address
        if (address is Inet4Address && !address.isLoopbackAddress) {
            return address.hostAddress ?: "N/A"
        }
    }
    return "N/A"
}

fun getAdbWifiPort(context: Context): String {
    return try {
        Settings.Global.getInt(context.contentResolver, "adb_wifi_port", 0).let {
            if (it == 0) "5555 (Padrão)" else it.toString()
        }
    } catch (e: Exception) {
        "Desconhecida"
    }
}

fun checkPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, 
        Manifest.permission.WRITE_SECURE_SETTINGS
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun DevTilesTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFFD0BCFF),
            secondary = Color(0xFFCCC2DC),
            tertiary = Color(0xFFEFB8C8),
            background = Color(0xFF000000), 
            surface = Color(0xFF000000),    
            onPrimary = Color(0xFF381E72),
            onSecondary = Color(0xFF332D41),
            onBackground = Color(0xFFE6E1E5),
            onSurface = Color(0xFFE6E1E5),
            surfaceVariant = Color(0xFF212121), 
            onSurfaceVariant = Color(0xFFCAC4D0)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF6750A4),
            secondary = Color(0xFF625B71),
            tertiary = Color(0xFF7D5260),
            background = Color(0xFFFFFBFE),
            surface = Color(0xFFFFFBFE),
            onPrimary = Color(0xFFFFFFFF),
            onSecondary = Color(0xFFFFFFFF),
            onBackground = Color(0xFF1C1B1F),
            onSurface = Color(0xFF1C1B1F),
            surfaceVariant = Color(0xFFE7E0EC),
            onSurfaceVariant = Color(0xFF49454F)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
