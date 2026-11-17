@file:Suppress("HardCodedStringLiteral")

package org.futo.inputmethod.latin.uix.settings

import android.content.Context
import android.content.pm.PackageManager

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
