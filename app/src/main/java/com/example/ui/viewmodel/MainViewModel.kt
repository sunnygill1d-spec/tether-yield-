package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AdminTreasuryEntity
import com.example.data.local.InvestmentEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.ReferralMemberEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserEntity
import com.example.data.model.CryptoTicker
import com.example.data.model.InvestmentPlansData
import com.example.data.repository.MarketRepository
import com.example.data.repository.TetherRepository
import com.example.ui.theme.ThemeMode
import com.example.util.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    DASHBOARD,
    PLANS,
    WALLET,
    MARKETS,
    AFFILIATE,
    ADMIN,
    SETTINGS
}

data class UiMessage(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isError: Boolean = false
)

class MainViewModel(
    private val repository: TetherRepository,
    private val marketRepository: MarketRepository = MarketRepository()
) : ViewModel() {

    // Current User Session
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Screen Navigation
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Auth state
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Theme & Language
    private val _themeMode = MutableStateFlow(ThemeMode.DARK)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    // Dynamic Lists for Logged-In User
    private val _userInvestments = MutableStateFlow<List<InvestmentEntity>>(emptyList())
    val userInvestments: StateFlow<List<InvestmentEntity>> = _userInvestments.asStateFlow()

    private val _userTransactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val userTransactions: StateFlow<List<TransactionEntity>> = _userTransactions.asStateFlow()

    private val _userReferrals = MutableStateFlow<List<ReferralMemberEntity>>(emptyList())
    val userReferrals: StateFlow<List<ReferralMemberEntity>> = _userReferrals.asStateFlow()

    private val _userNotifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val userNotifications: StateFlow<List<NotificationEntity>> = _userNotifications.asStateFlow()

    // Admin Data
    val adminTreasury: StateFlow<AdminTreasuryEntity?> = repository.getAdminTreasury()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingWithdrawals: StateFlow<List<TransactionEntity>> = repository.getPendingWithdrawals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Market Analytics
    val marketTickers: StateFlow<List<CryptoTicker>> = marketRepository.getLiveMarketTickers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Banner / Snackbar Feedback
    private val _uiMessage = MutableStateFlow<UiMessage?>(null)
    val uiMessage: StateFlow<UiMessage?> = _uiMessage.asStateFlow()

    init {
        // Auto-login demo user for immediate instant app testing convenience
        viewModelScope.launch {
            val demo = repository.getUserByEmail("demo@investor.com")
            if (demo != null) {
                setCurrentUser(demo)
                _isLoggedIn.value = true
            }
        }
    }

    private fun setCurrentUser(user: UserEntity) {
        _currentUser.value = user
        // Observe reactive user data
        viewModelScope.launch {
            repository.getUserById(user.id).collect { updated ->
                if (updated != null) {
                    _currentUser.value = updated
                }
            }
        }
        viewModelScope.launch {
            repository.getInvestmentsForUser(user.id).collect {
                _userInvestments.value = it
            }
        }
        viewModelScope.launch {
            repository.getTransactionsForUser(user.id).collect {
                _userTransactions.value = it
            }
        }
        viewModelScope.launch {
            repository.getReferralsForUser(user.id).collect {
                _userReferrals.value = it
            }
        }
        viewModelScope.launch {
            repository.getNotificationsForUser(user.id).collect {
                _userNotifications.value = it
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun showMessage(text: String, isError: Boolean = false) {
        _uiMessage.value = UiMessage(text = text, isError = isError)
    }

    fun dismissMessage() {
        _uiMessage.value = null
    }

    // Auth actions
    fun login(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email.trim())
            if (user == null) {
                onResult(false, "No account found with this email.")
            } else if (user.passwordHash != pass) {
                onResult(false, "Incorrect password. Please try again.")
            } else {
                setCurrentUser(user)
                _isLoggedIn.value = true
                onResult(true, "Welcome back, ${user.fullName}!")
            }
        }
    }

    fun socialLogin(provider: String) {
        viewModelScope.launch {
            val email = "social_${provider.lowercase()}@tetheryield.io"
            var user = repository.getUserByEmail(email)
            if (user == null) {
                val res = repository.registerUser(
                    email = email,
                    fullName = "$provider Investor",
                    passwordHash = "socialpass",
                    referralCodeInput = null
                )
                user = res.getOrNull()
            }
            if (user != null) {
                setCurrentUser(user)
                _isLoggedIn.value = true
                showMessage("Successfully signed in with $provider!")
            }
        }
    }

    fun biometricLogin() {
        viewModelScope.launch {
            val demo = repository.getUserByEmail("demo@investor.com")
            if (demo != null) {
                setCurrentUser(demo)
                _isLoggedIn.value = true
                showMessage("Biometric authentication verified!")
            }
        }
    }

    fun register(
        email: String,
        fullName: String,
        pass: String,
        referralCode: String?,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registerUser(email, fullName, pass, referralCode)
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                setCurrentUser(user)
                _isLoggedIn.value = true
                onResult(true, "Registration successful! +25 USDT welcome gift added.")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun resetPassword(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val success = repository.resetPassword(email)
            if (success) {
                onResult(true, "Temporary recovery credentials dispatched to $email")
            } else {
                onResult(false, "Email address not found.")
            }
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentUser.value = null
        _currentScreen.value = AppScreen.DASHBOARD
    }

    // Staking / Investment
    fun subscribePlan(planId: Int) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.subscribeToPlan(user.id, planId)
            if (result.isSuccess) {
                val plan = InvestmentPlansData.ALL_PLANS.find { it.id == planId }
                showMessage("Plan ${plan?.name} activated! 0.5% daily return unlocked.")
            } else {
                showMessage(result.exceptionOrNull()?.message ?: "Staking failed", isError = true)
            }
        }
    }

    // Daily Harvest / Yield Distribution
    fun harvestYield() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val amount = repository.harvestDailyYields(user.id)
            if (amount > 0) {
                showMessage("Harvested +${String.format("%.3f", amount)} USDT daily return!")
            } else {
                showMessage("No active yield to harvest right now. Accrues every 24h.", isError = false)
            }
        }
    }

    // Wallet actions
    fun depositFaucet(amount: Double = 100.0, network: String = "TRC20") {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.depositFunds(user.id, amount, network)
            showMessage("+$amount USDT deposited successfully via $network!")
        }
    }

    fun requestWithdrawal(
        amount: Double,
        address: String,
        network: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.requestAutomatedWithdrawal(user.id, amount, address, network)
            if (result.isSuccess) {
                showMessage("Automated withdrawal of $amount USDT submitted!")
                onComplete(true, "Transaction Hash: ${result.getOrNull()}")
            } else {
                val err = result.exceptionOrNull()?.message ?: "Withdrawal error"
                showMessage(err, isError = true)
                onComplete(false, err)
            }
        }
    }

    // Admin console actions
    fun approveWithdrawal(txId: String) {
        viewModelScope.launch {
            repository.approveWithdrawal(txId)
            showMessage("Withdrawal transaction $txId approved and broadcasted!")
        }
    }

    fun rejectWithdrawal(txId: String, userId: String, amount: Double) {
        viewModelScope.launch {
            repository.rejectWithdrawal(txId, userId, amount)
            showMessage("Withdrawal rejected and $amount USDT refunded to user.")
        }
    }

    fun adminTreasuryTransfer(
        targetAddress: String,
        amount: Double,
        network: String,
        memo: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.executeAdminTreasuryTransfer(targetAddress, amount, network, memo)
            if (result.isSuccess) {
                showMessage("Treasury dispatched $amount USDT to $targetAddress")
                onComplete(true, "Dispatched TX: ${result.getOrNull()}")
            } else {
                val err = result.exceptionOrNull()?.message ?: "Transfer failed"
                showMessage(err, isError = true)
                onComplete(false, err)
            }
        }
    }

    // Settings
    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        val user = _currentUser.value
        if (user != null) {
            viewModelScope.launch {
                repository.updateLanguage(user.id, lang.code)
            }
        }
    }

    fun toggleBiometric(enabled: Boolean) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.updateBiometric(user.id, enabled)
            showMessage("Biometric unlock " + if (enabled) "enabled" else "disabled")
        }
    }

    fun toggle2FA(enabled: Boolean) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.updateTwoFactor(user.id, enabled)
            showMessage("Two-Factor Authentication " + if (enabled) "activated" else "deactivated")
        }
    }
}

class MainViewModelFactory(
    private val repository: TetherRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}
