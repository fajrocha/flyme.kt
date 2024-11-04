package com.faroc.flyme.common.domain

import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun Int.secondsToFormattedDuration(): String {
    val duration = this.toDuration(DurationUnit.SECONDS)

    val hours = duration.inWholeHours
    val minutes = duration.inWholeMinutes % 60
    val seconds = duration.inWholeSeconds % 60

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}