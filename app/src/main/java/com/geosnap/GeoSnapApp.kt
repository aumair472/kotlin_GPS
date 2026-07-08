package com.geosnap

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.geosnap.core.designsystem.component.GeoSnapBottomBar
import com.geosnap.core.navigation.MainDestination
import com.geosnap.core.navigation.Routes
import com.geosnap.feature.camera.CameraRoute
import com.geosnap.feature.collection.CollectionRoute
import com.geosnap.feature.language.LanguageScreen
import com.geosnap.feature.mediadetail.MediaDetailRoute
import com.geosnap.feature.onboarding.OnboardingScreen
import com.geosnap.feature.report.ReportEditorRoute
import com.geosnap.feature.reporting.ReportingRoute
import com.geosnap.feature.settings.SettingsRoute
import com.geosnap.feature.splash.SplashScreen
import com.geosnap.feature.templates.TemplatesRoute
import com.geosnap.core.util.openUrl

private const val SPLASH = "splash"

@Composable
fun GeoSnapApp(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val mainDestination = MainDestination.entries.firstOrNull { it.route == currentRoute }
    // Bottom navigation shown on all four main destinations incl. Camera (HP-06): the camera keeps a
    // compact control panel above the bar so users can jump to Collection/Reporting/Templates.
    val showBottomBar = mainDestination != null

    Scaffold(
        bottomBar = {
            AnimatedVisibility(visible = showBottomBar) {
                GeoSnapBottomBar(
                    selected = mainDestination ?: MainDestination.CAMERA,
                    onSelect = { navController.navigateToMain(it) },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = SPLASH,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            composable(SPLASH) {
                SplashScreen(onResolved = { route ->
                    navController.navigate(route) {
                        popUpTo(SPLASH) { inclusive = true }
                    }
                })
            }

            composable(
                route = Routes.LANGUAGE_FIRST,
                arguments = listOf(navArgument(Routes.Arg.FIRST) {
                    type = NavType.BoolType; defaultValue = false
                }),
            ) { entry ->
                val first = entry.arguments?.getBoolean(Routes.Arg.FIRST) ?: false
                LanguageScreen(
                    isFirstLaunch = first,
                    onProceed = {
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(Routes.LANGUAGE_FIRST) { inclusive = true }
                        }
                    },
                    onClose = { if (!navController.popBackStack()) Unit },
                )
            }

            composable(Routes.ONBOARDING) {
                OnboardingScreen(onFinished = {
                    navController.navigate(Routes.CAMERA) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                })
            }

            composable(Routes.CAMERA) {
                CameraRoute(
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                    onOpenCollection = { navController.navigateToMain(MainDestination.COLLECTION) },
                    onOpenMedia = { id -> navController.navigate(Routes.mediaDetail(id)) },
                )
            }
            composable(Routes.COLLECTION) {
                CollectionRoute(onOpenMedia = { id -> navController.navigate(Routes.mediaDetail(id)) })
            }
            composable(Routes.REPORTING) {
                ReportingRoute(
                    onNewReport = { id -> navController.navigate(Routes.reportEdit(id)) },
                    onOpenReport = { id -> navController.navigate(Routes.reportEdit(id)) },
                )
            }
            composable(Routes.TEMPLATES) { TemplatesRoute() }

            composable(Routes.SETTINGS) {
                val context = LocalContext.current
                SettingsRoute(
                    onBack = { navController.popBackStack() },
                    onOpenLanguage = { navController.navigate(Routes.language(first = false)) },
                    onOpenPrivacy = { openUrl(context, context.getString(R.string.privacy_policy_url)) },
                    onOpenTerms = { openUrl(context, context.getString(R.string.terms_url)) },
                )
            }

            composable(
                route = Routes.MEDIA_DETAIL,
                arguments = listOf(navArgument(Routes.Arg.MEDIA_ID) { type = NavType.StringType }),
            ) { entry ->
                MediaDetailRoute(
                    mediaId = entry.arguments?.getString(Routes.Arg.MEDIA_ID).orEmpty(),
                    onBack = { navController.popBackStack() },
                )
            }

            composable(
                route = Routes.REPORT_EDIT,
                arguments = listOf(navArgument(Routes.Arg.REPORT_ID) { type = NavType.StringType }),
            ) { entry ->
                ReportEditorRoute(
                    reportId = entry.arguments?.getString(Routes.Arg.REPORT_ID).orEmpty(),
                    onBack = { navController.popBackStack() },
                    onPickMedia = { navController.navigateToMain(MainDestination.COLLECTION) },
                )
            }
        }
    }
}

/** Bottom-bar navigation: single top, restore state, pop to start (docs/NAVIGATION.md). */
private fun NavHostController.navigateToMain(destination: MainDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
