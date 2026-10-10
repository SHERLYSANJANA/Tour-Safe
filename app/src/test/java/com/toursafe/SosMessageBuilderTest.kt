package com.toursafe

import com.toursafe.data.models.EmergencyContact
import com.toursafe.viewmodels.SosHandOffState
import com.toursafe.viewmodels.VoiceSosViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SosMessageBuilderTest {

    @Test
    fun prepareSosHandOff_formatsMessageAndContactsCorrectly() {
        val viewModel = VoiceSosViewModel()
        viewModel.updateSpokenText("Help needed at hotel room 302")

        val contacts = listOf(
            EmergencyContact("1", "Alice", "Spouse", "+1 (555) 019-2834", isPrimary = true),
            EmergencyContact("2", "Bob", "Friend", "+1 555-234-5678", isPrimary = false)
        )

        viewModel.prepareSosHandOff(contacts)
        val state = viewModel.handOffState.value

        assertTrue(state is SosHandOffState.Prepared)
        val prepared = state as SosHandOffState.Prepared
        assertEquals("+15550192834,+15552345678", prepared.smsNumbers)
        assertTrue(prepared.message.contains("Help needed at hotel room 302"))
    }

    @Test
    fun prepareSosHandOff_withNoContacts_neverAddressesAFakeNumber() {
        val viewModel = VoiceSosViewModel()
        viewModel.updateSpokenText("Need help")

        viewModel.prepareSosHandOff(emptyList())
        val prepared = viewModel.handOffState.value as SosHandOffState.Prepared

        // No saved contacts -> no recipient. The user chooses one in their SMS app.
        assertEquals("", prepared.smsNumbers)
    }
}
