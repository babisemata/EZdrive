package com.example.ezdrive.navigation

import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ezdrive.homescreen.CarRentalHomeScreen
import com.example.ezdrive.loginpage.LoginScreen
import com.example.ezdrive.loginpage.RegisterScreen
import com.example.ezdrive.maps.BranchMapScreen
import com.example.ezdrive.navigationbar.BottomNavigationPane
import com.example.ezdrive.navigationbar.NavItem
import com.example.ezdrive.navigationbar.AdminBottomNavigationPane
import com.example.ezdrive.navigationbar.BottomNavigationPane
import com.example.ezdrive.profile.ProfileScreen
import com.example.ezdrive.service.SessionManager
import com.example.ezdrive.service.handleLogin
import com.example.ezdrive.service.handleRegister
import com.example.ezdrive.screens.SewaScreen
import com.example.ezdrive.homescreens.AdminDashboardScreen
import com.example.ezdrive.homescreen.CarItem
import com.example.ezdrive.detailscreen.CarDetailScreen
import com.example.ezdrive.homescreen.AdminCarListScreen
import com.example.ezdrive.navigationbar.AdminNavItem
import com.example.ezdrive.search.fiturpencarian
import com.example.ezdrive.profile.EditProfileScreen
import com.example.ezdrive.screens.admin.AdminAddCarScreen
import com.example.ezdrive.screens.admin.AdminEditCarScreen
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ezdrive.helper.DBHelper
import com.example.ezdrive.model.User


@Composable
fun AppNavigation(sessionManager: SessionManager) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val isLoggedIn = sessionManager.isLoggedIn()

//    val loggedInEmail = sessionManager.getUserEmail()
    val loggedInRole= sessionManager.getUserRole()
    val isAdmin = loggedInRole == "admin"

    val startDestination = if (!isLoggedIn) {
        "login"
    } else if (isAdmin) {
        AdminNavItem.Dashboard.route
    } else {
        NavItem.Home.route
    }

    val dbHelper = remember { DBHelper(context) }

    // 1. Siapkan state untuk menampung data user dari database
    var user by remember { mutableStateOf<User?>(null) }

    // 2. LaunchedEffect untuk mengambil data dari DB saat layar dibuka
    LaunchedEffect(Unit) {
        val email = sessionManager.getUserEmail()
        if (email != null) {
            // Panggil fungsi yang sudah kita buat di DBHelper
            user = dbHelper.getUserByEmail(email)
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: if (isLoggedIn) NavItem.Home.route else "login"

    Scaffold(
        bottomBar = {
            if (isLoggedIn) {
                // Tampilkan bottom bar berdasarkan status admin
                if (isAdmin) {
                    AdminBottomNavigationPane(
                        currentRoute = currentRoute,
                        onItemSelected = { route -> navController.navigate(route) }
                    )
                } else {
                    BottomNavigationPane(
                        currentRoute = currentRoute,
                        onItemSelected = { route -> navController.navigate(route) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(
                    onLogin = { email, password ->
                        handleLogin(context, email, password) { success, role ->
                            if (success) {
                                sessionManager.saveLogin(email, role)
                                Toast.makeText(context, "Login as $role", Toast.LENGTH_SHORT).show()

                                // ## BAGIAN YANG MEMPERBAIKI MASALAH ANDA ADA DI SINI ##
                                Log.d("LOGIN_DEBUG", "Role yang diterima dari handleLogin: '$role'")

                                // 1. Tentukan tujuan berdasarkan 'role'
                                val destination = if (role == "admin") {
                                    AdminNavItem.Dashboard.route // Jika admin, ke dashboard
                                } else {
                                    NavItem.Home.route // Jika bukan, ke home biasa
                                }
                                Log.d("LOGIN_DEBUG", "Tujuan navigasi yang dipilih: '$destination'")
                                // 2. Navigasi ke tujuan yang benar
                                navController.navigate(destination) {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    }
                )
            }
            composable("register") {
                RegisterScreen(
                    onRegister = { name, email, password ->
                        handleRegister(context, name, email, password) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (success) {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                )
            }

            composable(NavItem.Home.route) {
                CarRentalHomeScreen(
                    onNavigate = { navController.navigate(it) },
                    onCarClicked = { selectedCar ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("selected_car", selectedCar)
                        navController.navigate("car_detail")
                    },
                    onProfile = {
                        navController.navigate(NavItem.Profile.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(NavItem.Sewa.route) {
                SewaScreen(
                    currentRoute = currentRoute,
                    onNavigate = { navController.navigate(it) },
                    onBookingClick = { /* TODO */ }
                )
            }

            composable(NavItem.Profile.route) {
                ProfileScreen(
                    userName = user?.username ?: "...",
                    userEmail = user?.email ?: "-",
                    userRole = user?.role ?: "-",
                    userPhone = user?.no_hp,
                    onEdit   = { navController.navigate("edit_profile") },
                    onLogout = {
                        sessionManager.clearSession()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBack = {
                        navController.navigate(NavItem.Home.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable("map") {
                BranchMapScreen(
                    address = "Jln. Patih Jelantik No.102, Gianyar",
                    branchName = "Cabang Utama EZ Drive",
                    onBack = { navController.popBackStack() }
                )
            }

            composable(AdminNavItem.Dashboard.route) {
                AdminCarListScreen(
                    onAddCarClicked = {
                        navController.navigate("admin_add_car")
                    },
                    onEditCarClicked = { carId ->
                        navController.navigate("admin_edit_car/$carId")
                    }
                )
            }

            composable("admin_add_car") {
                AdminAddCarScreen(
                    onCarAdded = {
                        // Kembali ke layar daftar setelah berhasil menambah
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = "admin_edit_car/{carId}",
                arguments = listOf(navArgument("carId") { type = NavType.IntType })
            ) { backStackEntry ->
                val carId = backStackEntry.arguments?.getInt("carId") ?: 0
                AdminEditCarScreen(
                    carId = carId,
                    onCarUpdated = {
                        // Kembali ke layar daftar setelah berhasil update
                        navController.popBackStack()
                    }
                )
            }
            composable(AdminNavItem.Profile.route) {
                // Gunakan ProfileScreen yang sudah ada dengan logika logout yang sama
                ProfileScreen(
                    userName = user?.username ?: "...",
                    userEmail = user?.email ?: "-",
                    userRole = user?.role ?: "-",
                    userPhone = user?.no_hp,
                    onEdit   = { /* Mungkin halaman edit admin berbeda */ },
                    onLogout = {
                        sessionManager.clearSession()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBack = {
                        navController.navigate(AdminNavItem.Dashboard.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable("search") {
                fiturpencarian(
                    onBack = { navController.popBackStack() },
                    onCarClicked = { car ->
                        // set selected_car dan navigasi ke detail, misal:
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("selected_car", car)
                        navController.navigate("car_detail")
                    }
                )
            }


            composable("car_detail") {
                val car = navController.previousBackStackEntry
                    ?.savedStateHandle?.get<CarItem>("selected_car")

                car?.let {
                    CarDetailScreen(
                        car = it,
                        onBack = { navController.popBackStack() },
                        onRentNow = { selected ->
                            // Navigasi ke halaman pemesanan
                        }
                    )
                }
            }
        }
    }
}
