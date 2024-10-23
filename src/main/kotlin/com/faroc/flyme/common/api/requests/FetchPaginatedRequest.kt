package com.faroc.flyme.common.api.requests

data class FetchPaginatedRequest(
    val pageNumber: Int = 1,
    val pageSize: Int = 5,
) {
    val offset: Int
        get() = (pageNumber - 1) * pageSize
}
