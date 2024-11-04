package com.faroc.flyme.common.api.responses

class PaginatedResponse<T> private constructor(
    val pageNumber: Int,
    val pageSize: Int,
    val totalItems: Long,
    val totalPages: Long,
    val items: List<T>,
) {
    companion object {
        fun <T> create(pageNumber: Int, requestedPageSize: Int, totalItems: Long, items: List<T>) : PaginatedResponse<T> {
            val pageSize = items.size
            val totalPages = calculateTotalPages(totalItems, requestedPageSize)

            return PaginatedResponse(pageNumber, pageSize, totalItems, totalPages, items)
        }

        private fun calculateTotalPages(totalItems: Long, requestedPageSize: Int): Long {
            return if ((totalItems % requestedPageSize) == 0L)
                totalItems / requestedPageSize
            else
                totalItems / requestedPageSize + 1
        }

    }
}