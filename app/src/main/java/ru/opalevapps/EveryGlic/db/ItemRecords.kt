package ru.opalevapps.EveryGlic.db

// blood sugar measurement item
class ItemRecords(
    val id: Int,
    val measurementSugar: Float,
    private val timeInSeconds: Long,
    private val comment: String
) {
    val timeInMillis: Long
        get() = timeInSeconds * 1000L
}