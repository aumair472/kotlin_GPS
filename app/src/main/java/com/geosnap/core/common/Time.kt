package com.geosnap.core.common

import java.time.Clock
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/** Injectable time source so capture/report instants are deterministic in tests. */
interface TimeSource {
    fun now(): Instant
    fun clock(): Clock
}

@Singleton
class SystemTimeSource @Inject constructor() : TimeSource {
    override fun now(): Instant = Instant.now()
    override fun clock(): Clock = Clock.systemDefaultZone()
}
