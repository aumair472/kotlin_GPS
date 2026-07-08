package com.geosnap.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.geosnap.core.model.TemplateStyle
import com.google.common.truth.Truth.assertThat
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Executes real DataStore persistence (FIX-02 / verification item 9: selection survives app
 * restart). A second store instance on the same file simulates a process restart.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class GeoSnapPreferencesTest {

    private lateinit var file: File

    @Before
    fun setUp() {
        file = File.createTempFile("geosnap_test", ".preferences_pb").also { it.delete() }
    }

    @After
    fun tearDown() {
        file.delete()
    }

    private fun store(scope: CoroutineScope): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(scope = scope) { file }

    @Test
    fun defaultsToMinimalWhenUnset_andPersistsSelectionAcrossRestart() = runTest {
        val scope1 = CoroutineScope(UnconfinedTestDispatcher(testScheduler) + Job())
        val prefs1 = GeoSnapPreferences(store(scope1))

        // No preference yet → Minimal default.
        assertThat(prefs1.preferences.first().selectedTemplateId).isEqualTo(TemplateStyle.MINIMAL.id)

        // User picks Detailed (stored by stable id, not a localized label).
        prefs1.setSelectedTemplate(TemplateStyle.DETAILED.id)
        assertThat(prefs1.preferences.first().selectedTemplateId).isEqualTo(TemplateStyle.DETAILED.id)
        scope1.cancel() // release file → simulate process death

        // "Restart": fresh store + prefs on the same file.
        val scope2 = CoroutineScope(UnconfinedTestDispatcher(testScheduler) + Job())
        val prefs2 = GeoSnapPreferences(store(scope2))
        assertThat(prefs2.preferences.first().selectedTemplateId).isEqualTo(TemplateStyle.DETAILED.id)
        scope2.cancel()
    }
}
