package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Laptop
import androidx.compose.material.icons.outlined.Speaker
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DolbyTopAppBar
import com.example.ui.theme.DolbyBorder
import com.example.ui.theme.DolbyCardDark
import com.example.ui.theme.DolbyGold
import com.example.ui.theme.DolbyObsidian
import com.example.ui.theme.SpatialCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MainViewModel

@Composable
fun MainAppScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val isBangla = userProfile.language == "বাংলা"

    // BackHandler: Return to Home tab if on a sub-tab
    BackHandler(enabled = currentTab != 0) {
        viewModel.setTab(0)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DolbyObsidian,
        topBar = {
            DolbyTopAppBar(
                currentLanguage = userProfile.language,
                onToggleLanguage = {
                    val next = if (userProfile.language == "English") "বাংলা" else "English"
                    viewModel.setLanguage(next)
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DolbyCardDark,
                modifier = Modifier
                    .border(width = 1.dp, color = DolbyBorder)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Tab 0: Home / Cinema
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { viewModel.setTab(0) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 0) Icons.Filled.Speaker else Icons.Outlined.Speaker,
                            contentDescription = "Cinema",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (isBangla) "থিয়েটার" else "Cinema",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DolbyGold,
                        selectedTextColor = DolbyGold,
                        indicatorColor = DolbyGold.copy(alpha = 0.18f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_cinema")
                )

                // Tab 1: 3D Calibrate
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { viewModel.setTab(1) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 1) Icons.Filled.Tune else Icons.Outlined.Tune,
                            contentDescription = "Calibrate",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (isBangla) "ক্যালিব্রেট" else "3D Room",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SpatialCyan,
                        selectedTextColor = SpatialCyan,
                        indicatorColor = SpatialCyan.copy(alpha = 0.18f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_calibrate")
                )

                // Tab 2: Devices
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { viewModel.setTab(2) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 2) Icons.Filled.Devices else Icons.Outlined.Devices,
                            contentDescription = "Devices",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (isBangla) "ডিভাইস" else "Devices",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DolbyGold,
                        selectedTextColor = DolbyGold,
                        indicatorColor = DolbyGold.copy(alpha = 0.18f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_devices")
                )

                // Tab 3: Laptop Bridge
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { viewModel.setTab(3) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 3) Icons.Filled.Laptop else Icons.Outlined.Laptop,
                            contentDescription = "Laptop",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (isBangla) "ল্যাপটপ" else "Laptop",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == 3) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SpatialCyan,
                        selectedTextColor = SpatialCyan,
                        indicatorColor = SpatialCyan.copy(alpha = 0.18f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_laptop")
                )

                // Tab 4: Profile
                NavigationBarItem(
                    selected = currentTab == 4,
                    onClick = { viewModel.setTab(4) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == 4) Icons.Filled.AccountCircle else Icons.Outlined.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (isBangla) "প্রোফাইল" else "Profile",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == 4) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DolbyGold,
                        selectedTextColor = DolbyGold,
                        indicatorColor = DolbyGold.copy(alpha = 0.18f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DolbyObsidian)
        ) {
            when (currentTab) {
                0 -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToCalibration = { viewModel.setTab(1) },
                    onNavigateToDesktop = { viewModel.setTab(3) }
                )
                1 -> RoomCalibrationScreen(viewModel = viewModel)
                2 -> DevicesScreen(viewModel = viewModel)
                3 -> DesktopBridgeScreen(viewModel = viewModel)
                4 -> ProfileScreen(viewModel = viewModel)
            }
        }
    }
}
