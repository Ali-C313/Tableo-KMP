package com.example.tableo.data.enums

import androidx.compose.runtime.Composable
import com.example.tableo.resources.Res
import com.example.tableo.resources.role_bar
import com.example.tableo.resources.role_cook
import com.example.tableo.resources.role_driver
import com.example.tableo.resources.role_kitchen_help
import com.example.tableo.resources.role_lead
import com.example.tableo.resources.role_none
import com.example.tableo.resources.role_waiter
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** Aufgabe eines Mitarbeiters im Restaurant, wird im Team-Reiter vergeben. */
enum class StaffRole(val labelRes: StringResource) {
    OHNE(Res.string.role_none),
    LEITUNG(Res.string.role_lead),
    KELLNER(Res.string.role_waiter),
    KOCH(Res.string.role_cook),
    KUECHENHILFE(Res.string.role_kitchen_help),
    BAR(Res.string.role_bar),
    LIEFERANT(Res.string.role_driver)
}

/** Name der Rolle in der eingestellten Sprache. */
val StaffRole.label: String
    @Composable get() = stringResource(labelRes)
