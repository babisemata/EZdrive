package com.example.ezdrive.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ezdrive.detailscreen.CarDetailScreen
import com.example.ezdrive.helper.DBHelper
import com.example.ezdrive.homescreen.AdminCarListScreen
import com.example.ezdrive.homescreen.CarRentalHomeScreen
import com.example.ezdrive.loginpage.LoginScreen
import com.example.ezdrive.loginpage.RegisterScreen
import com.example.ezdrive.maps.BranchMapScreen
import com.example.ezdrive.model.User
import com.example.ezdrive.navigationbar.AdminBottomNavigationPane
import com.example.ezdrive.navigationbar.AdminNavItem
import com.example.ezdrive.navigationbar.BottomNavigationPane
import com.example.ezdrive.navigationbar.NavItem
import com.example.ezdrive.profile.AdminProfileScreen
import com.example.ezdrive.profile.ProfileScreen
import com.example.ezdrive.screens.AdminBookingScreen
import com.example.ezdrive.screens.PaymentScreen
import com.example.ezdrive.screens.SewaScreen
import com.example.ezdrive.screens.admin.AdminAddCarScreen
import com.example.ezdrive.screens.admin.AdminEditCarScreen
import com.example.ezdrive.screens.booking.BookingScreen
import com.example.ezdrive.search.fiturpencarian
import com.example.ezdrive.service.SessionManager
import com.example.ezdrive.service.handleLogin
import com.example.ezdrive.service.handleRegister

@Composable
fun AppNavigation(sessionManager: SessionManager) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val isLoggedIn = sessionManager.isLoggedIn()
    val loggedInRole = sessionManager.getUserRole()
    val isAdmin = loggedInRole == "admin"

    val startDestination = if (!isLoggedIn) {
        "login"
    } else if (isAdmin) {
        AdminNavItem.Dashboard.route
    } else {
        NavItem.Home.route
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: startDestination

    Scaffold(
        bottomBar = {
            if (isLoggedIn) {
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
            // --- LOGIN & REGISTER ---
            composable("login") {
                LoginScreen(
                    onLogin = { email, password ->
                        handleLogin(context, email, password) { success, role ->
                            if (success) {
                                sessionManager.saveLogin(email, role)
                                val destination = if (role == "admin") AdminNavItem.Dashboard.route else NavItem.Home.route
                                navController.navigate(destination) {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onNavigateToRegister = { navController.navigate("register") }
                )
            }
            composable("register") {
                RegisterScreen(
                    onRegister = { name, email, password ->
                        handleRegister(context, name, email, password) { success, message ->
                            if (success) {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            // --- USER FLOW ---
            composable(NavItem.Home.route) {
                CarRentalHomeScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onCarClicked = { selectedCar ->
                        navController.navigate("car_detail/${selectedCar.carFromDb.carid}")
                    },
                    onProfile = { navController.navigate(NavItem.Profile.route) }
                )
            }

            composable(NavItem.Sewa.route) {
                SewaScreen(
                    onPayClicked = { bookingId ->
                        navController.navigate("payment/$bookingId")
                    }
                )
            }

            composable(AdminNavItem.ManageBookings.route) {
                AdminBookingScreen()
            }

            composable(
                route = "payment/{bookingId}",
                arguments = listOf(navArgument("bookingId") { type = NavType.IntType })
            ) { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getInt("bookingId") ?: 0
                PaymentScreen(
                    bookingId = bookingId,
                    onPaymentSuccess = {
                        navController.popBackStack()
                    }
                )
            }

            composable(NavItem.Profile.route) {
                val dbHelper = remember { DBHelper(context) }
                var user by remember { mutableStateOf<User?>(null) }
                LaunchedEffect(Unit) {
                    sessionManager.getUserEmail()?.let { email ->
                        user = dbHelper.getUserByEmail(email)
                    }
                }
                ProfileScreen(
                    userName = user?.username ?: "...",
                    userEmail = user?.email ?: "-",
                    userRole = user?.role ?: "-",
                    userPhone = user?.no_hp,
                    onEdit   = { /* navController.navigate("edit_profile") */ },
                    onLogout = {
                        sessionManager.clearSession()
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("search") {
                fiturpencarian(
                    onBack = { navController.popBackStack() },
                    onCarClicked = { car ->
                        navController.navigate("car_detail/${car.carFromDb.carid}")
                    }
                )
            }

            // --- ADMIN FLOW ---
            composable(AdminNavItem.Dashboard.route) {
                AdminCarListScreen(
                    onAddCarClicked = { navController.navigate("admin_add_car") },
                    onEditCarClicked = { carId -> navController.navigate("admin_edit_car/$carId") }
                )
            }

            composable("admin_add_car") {
                AdminAddCarScreen(onCarAdded = { navController.popBackStack() })
            }

            composable("admin_edit_car/{carId}", arguments = listOf(navArgument("carId") { type = NavType.IntType })) { backStackEntry ->
                val carId = backStackEntry.arguments?.getInt("carId") ?: 0
                AdminEditCarScreen(
                    carId = carId,
                    onCarUpdated = { navController.popBackStack() }
                )
            }

            composable(AdminNavItem.Profile.route) {
                // 1. Inisialisasi DBHelper
                val dbHelper = remember { DBHelper(context) }
                var adminUser by remember { mutableStateOf<User?>(null) }

                // 2. Ambil data saat layar pertama kali dibuka
                LaunchedEffect(Unit) {
                    sessionManager.getUserEmail()?.let { email ->
                        // Ambil data lengkap admin dari database berdasarkan email
                        adminUser = dbHelper.getUserByEmail(email)
                    }
                }

                // 3. Tampilkan layar profil dengan data yang sudah diambil
                AdminProfileScreen(
                    // Gunakan data dari state, beri nilai default "..." saat masih loading
                    adminEmail = adminUser?.email ?: "...",

                    onBack = {
                        navController.navigate(AdminNavItem.Dashboard.route) {
                            popUpTo(AdminNavItem.Dashboard.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },

                    onLogout = {
                        sessionManager.clearSession()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // --- COMMON SCREENS (Detail, Booking, Map) ---
            composable(
                route = "car_detail/{carId}",
                arguments = listOf(navArgument("carId") { type = NavType.IntType })
            ) { backStackEntry ->
                val carId = backStackEntry.arguments?.getInt("carId") ?: 0
                CarDetailScreen(
                    carId = carId,
                    onBack = { navController.popBackStack() },
                    onRentNow = { carItem ->
                        navController.navigate("booking/${carItem.carFromDb.carid}")
                    }
                )
            }

            composable(
                route = "booking/{carId}",
                arguments = listOf(navArgument("carId") { type = NavType.IntType })
            ) { backStackEntry ->
                val carId = backStackEntry.arguments?.getInt("carId") ?: 0
                BookingScreen (
                    carId = carId,
                    onBack = { navController.popBackStack() }, // Tambahkan ini
                    onBookingSuccess = {
                        navController.navigate(NavItem.Sewa.route) {
                            popUpTo(NavItem.Home.route)
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
        }
    }
}