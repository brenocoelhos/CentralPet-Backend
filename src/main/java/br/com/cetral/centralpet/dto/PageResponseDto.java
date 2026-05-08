package br.com.cetral.centralpet.dto;

import java.util.List;

/**
 * Wrapper de resposta paginada — mais limpo que expor Page<T> do Spring Data.
 * Resposta JSON:
 * {
 *   "content": [...],
 *   "page": 0,
 *   "size": 20,
 *   "totalElements": 100,
 *   "totalPages": 5,
 *   "hasNext": true
 * }
 */
public record PageResponseDto<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {
}

