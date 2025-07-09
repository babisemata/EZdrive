package com.example.ezdrive.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
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
import com.example.ezdrive.profile.EditProfileScreen
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
import com.example.ezdrive.utils.uriToByteArray


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
                                val destination =
                                    if (role == "admin") AdminNavItem.Dashboard.route else NavItem.Home.route
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
                val dbHelper = remember { DBHelper(context) }
                var user by remember { mutableStateOf<User?>(null) }

                // LaunchedEffect ini akan berjalan setiap kali Anda kembali ke Home
                LaunchedEffect(navController.currentBackStackEntry) {
                    // Ambil data user yang sedang login
                    sessionManager.getUserEmail()?.let { email ->
                        user = dbHelper.getUserByEmail(email)
                    }
                }

                CarRentalHomeScreen(
                    // Kirim data gambar ke UI
                    profilePicture = user?.profilePicture,

                    onNavigate = { route -> navController.navigate(route) },
                    onCarClicked = { selectedCar ->
                        // Anda perlu cara untuk mengirim objek Car ke detail,
                        // SavedStateHandle adalah cara terbaik
                        navController.currentBackStackEntry
                            ?.savedStateHandle?.set("car", selectedCar.carFromDb)
                        navController.navigate("car_detail")
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

            // Di dalam NavHost di AppNavigation.kt
            composable(NavItem.Profile.route) {
                val dbHelper = remember { DBHelper(context) }
                var user by remember { mutableStateOf<User?>(null) }

                // TANGKAP SINYAL DARI HALAMAN SEBELUMNYA
                val profileUpdated = navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.getLiveData<Boolean>("profile_updated")
                    ?.observeAsState()

                // GUNAKAN SINYAL SEBAGAI PEMICU
                LaunchedEffect(profileUpdated?.value) {
                    sessionManager.getUserEmail()?.let { email ->
                        user = dbHelper.getUserByEmail(email)
                    }
                    // Hapus sinyal setelah digunakan
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.remove<Boolean>("profile_updated")
                }

                if (user == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    ProfileScreen(
                        userName = user!!.username ?: "Tanpa Nama",
                        userEmail = user!!.email,
                        userRole = user!!.role,
                        userPhone = user!!.no_hp,
                        userLocation = "Indonesia",
                        profilePictureData = user!!.profilePicture,
                        onEdit = {
                            navController.navigate("edit_profile")
                        },
                        onBack = {
                            navController.popBackStack()
                        },
                        onLogout = {
                            sessionManager.clearSession()
                            navController.navigate("login") { popUpTo(0) { inclusive = true } }
                        }
                    )
                }
            }

            composable("edit_profile") {
                val context = LocalContext.current
                val dbHelper = remember { DBHelper(context) }
                var currentUser by remember { mutableStateOf<User?>(null) }

                LaunchedEffect(Unit) {
                    sessionManager.getUserEmail()?.let { email ->
                        currentUser = dbHelper.getUserByEmail(email)
                    }
                }

                EditProfileScreen(
                    currentUser = currentUser,
                    onBack = { navController.popBackStack() },
                    onSave = { username, phone, newImageUri ->
                        val email = currentUser?.email ?: return@EditProfileScreen

                        // Update data teks
                        dbHelper.updateUserProfile(email, username, phone)

                        // Update gambar jika ada
                        if (newImageUri != null) {
                            val imageBytes = uriToByteArray(context, newImageUri)
                            if (imageBytes != null) {
                                dbHelper.updateUserProfilePicture(email, imageBytes)
                            }
                        }

                        // KIRIM SINYAL BAHWA UPDATE BERHASIL
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("profile_updated", true) // Kuncinya di sini

                        Toast.makeText(context, "Profil berhasil diperbarui", Toast.LENGTH_SHORT)
                            .show()
                        navController.popBackStack()
                    }
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

            composable(
                "admin_edit_car/{carId}",
                arguments = listOf(navArgument("carId") { type = NavType.IntType })
            ) { backStackEntry ->
                val carId = backStackEntry.arguments?.getInt("carId") ?: 0
                AdminEditCarScreen(
                    carId = carId,
                    onCarUpdated = { navController.popBackStack() }
                )
            }

            // Ganti composable AdminNavItem.Profile.route Anda dengan ini
            composable(AdminNavItem.Profile.route) {
                val dbHelper = remember { DBHelper(context) }
                var adminUser by remember { mutableStateOf<User?>(null) }

                // Menangkap sinyal dari halaman edit untuk refresh
                val profileUpdated = navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.getLiveData<Boolean>("profile_updated")
                    ?.observeAsState()

                // LaunchedEffect akan mengambil data terbaru saat kembali dari halaman edit
                LaunchedEffect(profileUpdated?.value) {
                    sessionManager.getUserEmail()?.let { email ->
                        adminUser = dbHelper.getUserByEmail(email)
                    }
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.remove<Boolean>("profile_updated")
                }

                if (adminUser == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    AdminProfileScreen(
                        adminEmail = adminUser!!.email,
                        // INI BAGIAN PENTING: Kirim data gambar ke UI
                        profilePicture = adminUser!!.profilePicture,
                        onBack = {
                            navController.navigate(AdminNavItem.Dashboard.route) {
                                popUpTo(AdminNavItem.Dashboard.route) { inclusive = true }
                            }
                        },
                        onEdit = {
                            // Arahkan ke halaman edit yang sudah disatukan
                            navController.navigate("edit_profile")
                        },
                        onLogout = {
                            sessionManager.clearSession()
                            navController.navigate("login") { popUpTo(0) { inclusive = true } }
                        }
                    )
                }
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