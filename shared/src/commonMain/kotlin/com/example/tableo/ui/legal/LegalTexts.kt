package com.example.tableo.ui.legal

import androidx.compose.runtime.Composable
import com.example.tableo.data.enums.LegalPage
import com.example.tableo.resources.Res
import com.example.tableo.resources.legal_imprint_contact_heading
import com.example.tableo.resources.legal_imprint_contact_text
import com.example.tableo.resources.legal_imprint_dispute_heading
import com.example.tableo.resources.legal_imprint_dispute_text
import com.example.tableo.resources.legal_imprint_info_heading
import com.example.tableo.resources.legal_imprint_info_text
import com.example.tableo.resources.legal_imprint_note_heading
import com.example.tableo.resources.legal_imprint_note_text
import com.example.tableo.resources.legal_imprint_responsible_heading
import com.example.tableo.resources.legal_imprint_responsible_text
import com.example.tableo.resources.legal_imprint_title
import com.example.tableo.resources.legal_privacy_controller_heading
import com.example.tableo.resources.legal_privacy_controller_text
import com.example.tableo.resources.legal_privacy_data_heading
import com.example.tableo.resources.legal_privacy_data_text
import com.example.tableo.resources.legal_privacy_deletion_heading
import com.example.tableo.resources.legal_privacy_deletion_text
import com.example.tableo.resources.legal_privacy_device_heading
import com.example.tableo.resources.legal_privacy_device_text
import com.example.tableo.resources.legal_privacy_location_heading
import com.example.tableo.resources.legal_privacy_location_text
import com.example.tableo.resources.legal_privacy_notifications_heading
import com.example.tableo.resources.legal_privacy_notifications_text
import com.example.tableo.resources.legal_privacy_rights_heading
import com.example.tableo.resources.legal_privacy_rights_text
import com.example.tableo.resources.legal_privacy_services_heading
import com.example.tableo.resources.legal_privacy_services_text
import com.example.tableo.resources.legal_privacy_staff_heading
import com.example.tableo.resources.legal_privacy_staff_text
import com.example.tableo.resources.legal_privacy_title
import com.example.tableo.resources.legal_terms_account_heading
import com.example.tableo.resources.legal_terms_account_text
import com.example.tableo.resources.legal_terms_delivery_heading
import com.example.tableo.resources.legal_terms_delivery_text
import com.example.tableo.resources.legal_terms_final_heading
import com.example.tableo.resources.legal_terms_final_text
import com.example.tableo.resources.legal_terms_liability_heading
import com.example.tableo.resources.legal_terms_liability_text
import com.example.tableo.resources.legal_terms_order_heading
import com.example.tableo.resources.legal_terms_order_text
import com.example.tableo.resources.legal_terms_parties_heading
import com.example.tableo.resources.legal_terms_parties_text
import com.example.tableo.resources.legal_terms_prices_heading
import com.example.tableo.resources.legal_terms_prices_text
import com.example.tableo.resources.legal_terms_scope_heading
import com.example.tableo.resources.legal_terms_scope_text
import com.example.tableo.resources.legal_terms_title
import org.jetbrains.compose.resources.stringResource

/** Texte für Impressum, Datenschutz und AGB. Platzhalter in [eckigen Klammern] vor der Veröffentlichung ersetzen. */
@Composable
fun legalContent(page: LegalPage): LegalContent = when (page) {
    LegalPage.IMPRESSUM -> LegalContent(
        title = stringResource(Res.string.legal_imprint_title),
        sections = listOf(
            stringResource(Res.string.legal_imprint_info_heading) to stringResource(Res.string.legal_imprint_info_text),
            stringResource(Res.string.legal_imprint_contact_heading) to stringResource(Res.string.legal_imprint_contact_text),
            stringResource(Res.string.legal_imprint_responsible_heading) to stringResource(Res.string.legal_imprint_responsible_text),
            stringResource(Res.string.legal_imprint_note_heading) to stringResource(Res.string.legal_imprint_note_text),
            stringResource(Res.string.legal_imprint_dispute_heading) to stringResource(Res.string.legal_imprint_dispute_text)
        )
    )

    LegalPage.DATENSCHUTZ -> LegalContent(
        title = stringResource(Res.string.legal_privacy_title),
        sections = listOf(
            stringResource(Res.string.legal_privacy_controller_heading) to stringResource(Res.string.legal_privacy_controller_text),
            stringResource(Res.string.legal_privacy_data_heading) to stringResource(Res.string.legal_privacy_data_text),
            stringResource(Res.string.legal_privacy_location_heading) to stringResource(Res.string.legal_privacy_location_text),
            stringResource(Res.string.legal_privacy_staff_heading) to stringResource(Res.string.legal_privacy_staff_text),
            stringResource(Res.string.legal_privacy_services_heading) to stringResource(Res.string.legal_privacy_services_text),
            stringResource(Res.string.legal_privacy_device_heading) to stringResource(Res.string.legal_privacy_device_text),
            stringResource(Res.string.legal_privacy_notifications_heading) to stringResource(Res.string.legal_privacy_notifications_text),
            stringResource(Res.string.legal_privacy_deletion_heading) to stringResource(Res.string.legal_privacy_deletion_text),
            stringResource(Res.string.legal_privacy_rights_heading) to stringResource(Res.string.legal_privacy_rights_text)
        )
    )

    LegalPage.AGB -> LegalContent(
        title = stringResource(Res.string.legal_terms_title),
        sections = listOf(
            stringResource(Res.string.legal_terms_scope_heading) to stringResource(Res.string.legal_terms_scope_text),
            stringResource(Res.string.legal_terms_parties_heading) to stringResource(Res.string.legal_terms_parties_text),
            stringResource(Res.string.legal_terms_order_heading) to stringResource(Res.string.legal_terms_order_text),
            stringResource(Res.string.legal_terms_prices_heading) to stringResource(Res.string.legal_terms_prices_text),
            stringResource(Res.string.legal_terms_delivery_heading) to stringResource(Res.string.legal_terms_delivery_text),
            stringResource(Res.string.legal_terms_account_heading) to stringResource(Res.string.legal_terms_account_text),
            stringResource(Res.string.legal_terms_liability_heading) to stringResource(Res.string.legal_terms_liability_text),
            stringResource(Res.string.legal_terms_final_heading) to stringResource(Res.string.legal_terms_final_text)
        )
    )
}
