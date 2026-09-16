package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("منظومة جعفر بدران", appName)
  }

  @Test
  fun `verify forensic md5 and sha256 hashing calculations`() {
    val sampleText = "JaffarBadranForensicTest2026"
    val md5 = com.example.ui.components.ForensicCrypto.calculateMd5(sampleText)
    val sha256 = com.example.ui.components.ForensicCrypto.calculateSha256(sampleText)

    assertEquals(32, md5.length)
    assertEquals(64, sha256.length)
  }
}
