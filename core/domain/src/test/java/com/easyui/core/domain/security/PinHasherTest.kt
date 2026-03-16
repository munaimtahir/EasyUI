package com.easyui.core.domain.security

import com.easyui.core.domain.model.PinCredential
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PinHasherTest {
    @Test
    fun `create produces non-plain-text hash material`() {
        val credential = PinHasher.create("1234")

        assertNotEquals("1234", credential.hashHex)
        assertNotEquals("1234", credential.saltHex)
    }

    @Test
    fun `verify matches correct pin only`() {
        val credential = PinHasher.create("1234")

        assertTrue(PinHasher.verify("1234", credential))
        assertFalse(PinHasher.verify("9999", credential))
    }
}
