package bes.max.passman

import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import bes.max.features.main.domain.repositories.SettingsRepository
import bes.max.passman.navigation.NavigationGraph
import bes.max.passman.ui.theme.PassManTheme
import bes.max.passman.util.PermissionHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var cancellationSignal: CancellationSignal? = null

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            val darkTheme by settingsRepository.isNightModeActive()
                .collectAsState(initial = isSystemInDarkTheme())

            PassManTheme(
                darkTheme = darkTheme
            ) {
                val navController = rememberNavController()
                Scaffold { paddingValues ->
                    Box(
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        NavigationGraph(
                            navController,
                            { onSuccess: () -> Unit, onFail: () -> Unit ->
                                launchBiometric(
                                    { onSuccess() },
                                    { onFail() }
                                )
                            })
                    }
                }
            }
        }
    }

    private fun authenticationCallback(
        onSuccess: () -> Unit,
        onFail: () -> Unit,
    ): BiometricPrompt.AuthenticationCallback =
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            @RequiresApi(Build.VERSION_CODES.R)
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == 11) {
                    // Fingerprint is not added
                    launchKeyAuth(onSuccess, onFail)
                } else {
                    onFail()
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.prompt_info_no_credentials),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.prompt_info_no_credentials),
                    Toast.LENGTH_LONG
                ).show()
                onFail()
            }

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                super.onAuthenticationHelp(helpCode, helpString)
            }
        }

    private fun checkBiometricSupport(): Boolean {
        val keyGuardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keyGuardManager.isDeviceSecure) {
            return true
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        return packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun launchBiometric(
        onSuccess: () -> Unit,
        onFail: () -> Unit,
    ) {
        if (checkBiometricSupport()) {
            val biometricPrompt = BiometricPrompt.Builder(this)
                .apply {
                    setTitle(getString(R.string.prompt_title))
                    setDescription(getString(R.string.prompt_description))
                    setConfirmationRequired(false)
                    setNegativeButton(
                        getString(R.string.prompt_info_use_app_password),
                        mainExecutor,
                        { _, _ ->
                            launchKeyAuth(onSuccess, onFail)
                        })
                }.build()

            biometricPrompt.authenticate(
                getCancellationSignal(),
                mainExecutor,
                authenticationCallback(onSuccess, onFail)
            )
        } else {
            launchKeyAuth(onSuccess, onFail)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun launchKeyAuth(
        onSuccess: () -> Unit,
        onFail: () -> Unit,
    ) {
        if (checkBiometricSupport()) {
            val biometricPrompt = BiometricPrompt.Builder(this)
                .apply {
                    setTitle(getString(R.string.prompt_title))
                    setDescription(getString(R.string.prompt_description))
                    setConfirmationRequired(false)
                    setAllowedAuthenticators(DEVICE_CREDENTIAL)
                }.build()

            biometricPrompt.authenticate(
                getCancellationSignal(),
                mainExecutor,
                authenticationCallback(onSuccess, onFail)
            )
        }
    }

    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            Toast.makeText(this, getString(R.string.prompt_info_auth_cancelled), Toast.LENGTH_SHORT)
                .show()
        }

        return cancellationSignal as CancellationSignal
    }
}
