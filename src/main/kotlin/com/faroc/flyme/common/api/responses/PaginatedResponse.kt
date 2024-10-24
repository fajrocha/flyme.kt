package com.faroc.flyme.common.api.responses

data class PaginatedResponse<T>(
    val items: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalItems: Long,
) {
    val totalPages: Long
        get() = if ((totalItems % pageSize) == 0L) totalItems/pageSize else totalItems/pageSize + 1
}
