package com.defri.bookreflect.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.defri.bookreflect.presentation.auth.ForgotPasswordScreen
import com.defri.bookreflect.presentation.auth.LoginScreen
import com.defri.bookreflect.presentation.auth.RegisterScreen
import com.defri.bookreflect.presentation.books.BooksScreen
import com.defri.bookreflect.presentation.common.theme.BookReflectTheme
import com.defri.bookreflect.presentation.home.HomeScreen
import com.defri.bookreflect.presentation.profile.ProfileScreen
import com.defri.bookreflect.presentation.moods.MoodsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookReflectApp()
        }
    }
}

@Composable
fun BookReflectApp(viewModel: AppViewModel = hiltViewModel()) {
    BookReflectTheme {
        val navController = rememberNavController()
        val startDestination =
            if (viewModel.isAuthenticated) Screen.Main.route else Screen.Login.route

        Surface(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = navController, startDestination = startDestination) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                        onLoginSuccess = {
                            navController.navigate(Screen.Main.route) {
                                popUpTo(Screen.Login.route) {
                                    inclusive = true
                                }
                            }
                        },
                        onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
                    )
                }
                composable(Screen.Register.route) {
                    RegisterScreen(
                        onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                        onRegisterSuccess = {
                            navController.navigate(Screen.Main.route) {
                                popUpTo(Screen.Register.route) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }
                composable(Screen.ForgotPassword.route) {
                    ForgotPasswordScreen(onNavigateBack = {
                        navController.navigate(Screen.Login.route) {
                            launchSingleTop = true
                        }
                    })
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        onNavigateBack = {
                            navController.navigate(Screen.Main.route) {
                                popUpTo(Screen.Profile.route) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        },
                        onLogout = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0)
                            }
                        }
                    )
                }
                composable(Screen.Main.route) {
                    MainScreen(
                        onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route) {
                            launchSingleTop = true
                        }},
                        appViewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToProfile: () -> Unit,
    appViewModel: AppViewModel
) {
    val tabNavController = rememberNavController()
    val tabs = listOf(
        TabItem("Home", Icons.Default.Home, Screen.MainHome.route),
        TabItem("Books", Icons.Default.Book, Screen.MainBooks.route),
        TabItem("Mood", Icons.Default.EmojiEmotions, Screen.MainMood.route)
    )
    val currentRoute = tabNavController.currentBackStackEntryAsState().value?.destination?.route
    val state by appViewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = tabs.find { it.route == currentRoute }?.title ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) },
                        selected = currentRoute == tab.route,
                        onClick = {
                            tabNavController.navigate(tab.route) {
                                popUpTo(tabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = tabNavController,
            startDestination = Screen.MainHome.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(Screen.MainHome.route) {
                HomeScreen()
            }
            composable(Screen.MainBooks.route) {
                BooksScreen(
                    onNavigateToMoods = { bookId, bookTitle ->
                        appViewModel.handleEvent(
                            AppEvent.SetSelectedBookForMood(bookId, bookTitle)
                        )
                        tabNavController.navigate(Screen.MainMood.route) {
                            popUpTo(tabNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Screen.MainMood.route) {
                MoodsScreen(
                    bookId = state.selectedBookIdForMood,
                    bookTitle = state.selectedBookTitleForMood,
                    onBackFromCreateMood = {
                        appViewModel.handleEvent(AppEvent.ClearSelectedBookForMood)
                    }
                )
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Profile : Screen("profile")
    object Main : Screen("main")
    object MainHome : Screen("main/home")
    object MainBooks : Screen("main/books")
    object MainMood : Screen("main/mood")
}

data class TabItem(val title: String, val icon: ImageVector, val route: String)