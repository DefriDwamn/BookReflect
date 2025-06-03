package com.defri.bookreflect.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.defri.bookreflect.presentation.auth.ForgotPasswordScreen
import com.defri.bookreflect.presentation.auth.LoginScreen
import com.defri.bookreflect.presentation.auth.RegisterScreen
import com.defri.bookreflect.presentation.common.theme.BookReflectTheme
import com.defri.bookreflect.presentation.home.HomeScreen
import com.defri.bookreflect.presentation.profile.ProfileScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookReflectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                onLoginSuccess = { navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }}
            )
        }
        
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = { navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }}
            )
        }
        
        composable("forgot_password") {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onResetSuccess = { navController.popBackStack() }
            )
        }
        
        composable("home") {
            HomeScreen(
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }
        
        composable("profile") {
            ProfileScreen(
                onNavigateBack = { 
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    } else {
                        navController.navigate("home") {
                            popUpTo("profile") { inclusive = true }
                        }
                    }
                },
                onLogout = { navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }}
            )
        }
    }
} 