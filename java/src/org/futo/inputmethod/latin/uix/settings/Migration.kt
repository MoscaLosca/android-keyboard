@file:Suppress("HardCodedStringLiteral")

package org.futo.inputmethod.latin.uix.settings

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.SettingsKey
import org.futo.inputmethod.latin.uix.findActivity

/** Package names of the official FUTO Keyboard releases (standalone, nightly, Play Store). */
val OFFICIAL_FUTO_PACKAGES = listOf(
    "org.futo.inputmethod.latin",
    "org.futo.inputmethod.latin.unstable",
    "org.futo.inputmethod.latin.playstore",
)

/** True when any official FUTO Keyboard release is installed on this device. */
fun Context.isOfficialFutoInstalled(): Boolean {
    return OFFICIAL_FUTO_PACKAGES.any { packageName ->
        try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }
}

/** Set when the user dismissed the setup migration prompt ("Not now") — never show it again. */
val MigrationPromptDismissed = SettingsKey(
    booleanPreferencesKey("official_migration_prompt_dismissed"),
    false
)

/** Set once a settings backup has been imported from the official FUTO Keyboard. */
val MigrationCompleted = SettingsKey(
    booleanPreferencesKey("official_migration_completed"),
    false
)

/** Result code for the migration import flow (non-destructive). */
const val IMPORT_SETTINGS_MERGE_REQUEST = 53218641

/** Lets the user pick a `.backup` file exported from the official FUTO Keyboard; the picked
 *  file is imported non-destructively by SettingsActivity.onActivityResult. */
fun triggerImportSettingsMerge(context: Context) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "application/octet-stream"
    }

    findActivity(context)!!.startActivityForResult(intent, IMPORT_SETTINGS_MERGE_REQUEST)
}

/** The setup migration step shows only while the official app is installed and the user
 *  has neither imported a backup nor dismissed the prompt. */
@Composable
fun needsToShowMigrationPrompt(): Boolean {
    val context = LocalContext.current
    val officialInstalled = remember { context.isOfficialFutoInstalled() }
    if(!officialInstalled) return false

    return !useDataStoreValue(MigrationPromptDismissed) && !useDataStoreValue(MigrationCompleted)
}

@Composable
fun SetupImporting() {
    SetupContainer {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.setup_migration_importing),
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SetupImportSuccess(onContinue: () -> Unit) {
    SetupContainer {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.setup_migration_success_title),
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                stringResource(R.string.setup_migration_success_body),
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.setup_migration_continue))
            }
        }
    }
}

@Composable
fun SetupImportError(message: String?, onContinue: () -> Unit) {
    SetupContainer {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.setup_migration_error_title),
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                message ?: stringResource(R.string.setup_migration_error_body),
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.setup_migration_continue))
            }
        }
    }
}
