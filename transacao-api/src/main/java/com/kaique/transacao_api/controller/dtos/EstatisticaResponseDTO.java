package com.kaique.transacao_api.controller.dtos;

public record EstatisticaResponseDTO(
    long count,
    double sum,
    double avg,
    double min,
    double max
) {}