# GeoSnap R8 rules.
# Room, Hilt, WorkManager, Compose, CameraX, Media3 ship consumer rules; keep app specifics minimal.

# Keep typed Room entities' no-arg needs handled by Room itself. Keep enum valueOf used via Room TypeConverters.
-keepclassmembers enum * { public static **[] values(); public static ** valueOf(java.lang.String); }

# Coroutines debug metadata not needed in release.
-dontwarn kotlinx.coroutines.debug.**

# Media3 Transformer reflective effect classes.
-dontwarn androidx.media3.**
