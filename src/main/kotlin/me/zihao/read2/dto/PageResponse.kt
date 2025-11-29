package me.zihao.read2.dto

import org.springframework.data.domain.Sort

data class PageResponse<T>(
    val content: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val sort: Sort
)