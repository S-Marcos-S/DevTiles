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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween 
    ) {
        // Top Section: Title
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        // Middle Section: Status and Tools (Flexible)
        Column(
            modifier = Modifier.weight(1f, fill = false),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PermissionStatusCard(isPermissionGranted)
            
            if (!isPermissionGranted) {
                Box(modifier = Modifier.weight(1f, fill = false)) {
                    AdbInstructionsSection(context)
                }
            } else {
                WifiDebugDetailsSection(context)
                LegacyModeSection(context)
            }
        }
        
        // Bottom Section: Footer and Refresh
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isPermissionGranted) {
                Text(
                    text = "Tudo pronto para uso!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Button(
                onClick = { isPermissionGranted = checkPermission(context) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Verificar Novamente")
            }
            Spacer(modifier = Modifier.height(16.dp))
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
    var showDialog by remember { mutableStateOf<String?>(null) }
    var showInfoDialog by remember { mutableStateOf(false) }
    val shizukuCmd = stringResource(id = R.string.termux_command_value)
    val termuxAdbCmd = stringResource(id = R.string.termux_adb_connect)
    
    var currentPort by remember { 
        mutableStateOf(Settings.Global.getInt(context.contentResolver, "adb_wifi_port", 0)) 
    }
    var isWifiDebugEnabled by remember { 
        mutableStateOf(Settings.Global.getInt(context.contentResolver, "adb_wifi_enabled", 0) != 0) 
    }

    DisposableEffect(Unit) {
        val observer = object : android.database.ContentObserver(android.os.Handler(android.os.Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                currentPort = Settings.Global.getInt(context.contentResolver, "adb_wifi_port", 0)
                isWifiDebugEnabled = Settings.Global.getInt(context.contentResolver, "adb_wifi_enabled", 0) != 0
            }
        }
        context.contentResolver.registerContentObserver(Settings.Global.getUriFor("adb_wifi_port"), false, observer)
        context.contentResolver.registerContentObserver(Settings.Global.getUriFor("adb_wifi_enabled"), false, observer)
        onDispose { context.contentResolver.unregisterContentObserver(observer) }
    }

    val isAlreadyOn5555 = isWifiDebugEnabled && currentPort == 5555

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text(stringResource(R.string.legacy_info_title)) },
            text = {
                Text(
                    text = android.text.Html.fromHtml(
                        stringResource(R.string.legacy_info_content),
                        android.text.Html.FROM_HTML_MODE_LEGACY
                    ).toString()
                )
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

    if (showDialog != null) {
        val commandToCopy = showDialog!!
        LaunchedEffect(commandToCopy) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("ADB Command", commandToCopy)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, R.string.command_copied, Toast.LENGTH_SHORT).show()
        }

        AlertDialog(
            onDismissRequest = { showDialog = null },
            title = { 
                Text(if (commandToCopy == shizukuCmd) stringResource(R.string.shizuku_dialog_title) 
                     else stringResource(R.string.termux_dialog_title)) 
            },
            text = {
                Text(
                    text = commandToCopy,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                        .padding(8.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = { showDialog = null }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.legacy_mode_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { showInfoDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Informações",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            if (!isAlreadyOn5555) {
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
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(stringResource(id = R.string.activate_5555), fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            OutlinedButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("ADB Reset", "adb kill-server")
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Comando 'adb kill-server' copiado!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(stringResource(id = R.string.reset_adb), fontSize = 13.sp)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppShortcutButton(
                    label = stringResource(R.string.shizuku_label),
                    iconRes = R.drawable.ic_shizuku,
                    onClick = { showDialog = shizukuCmd },
                    modifier = Modifier.weight(1f)
                )
                AppShortcutButton(
                    label = stringResource(R.string.termux_label),
                    iconRes = R.drawable.ic_termux,
                    onClick = { showDialog = termuxAdbCmd },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AppShortcutButton(
    label: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedButton(
        onClick = onClick,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, fontSize = 12.sp)
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
