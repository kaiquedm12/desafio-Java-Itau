package com.kaique.transacao_api.infrastructure.persistence.jpa.repositories;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kaique.transacao_api.domain.model.TransacaoTipo;
import com.kaique.transacao_api.infrastructure.persistence.jpa.entities.TransacaoJpaEntity;

public interface TransacaoJpaRepository extends JpaRepository<TransacaoJpaEntity, UUID> {

    @Modifying
    @Query("delete from TransacaoJpaEntity t where t.tipo = :tipo")
    void deleteByTipo(@Param("tipo") TransacaoTipo tipo);

    interface StatsRow {
        Long getCount();

        java.math.BigDecimal getSum();

        java.math.BigDecimal getAvg();

        java.math.BigDecimal getMin();

        java.math.BigDecimal getMax();
    }

    @Query(value = """
            select
              count(*) as count,
              coalesce(sum(valor), 0) as sum,
              coalesce(avg(valor), 0) as avg,
              coalesce(min(valor), 0) as min,
              coalesce(max(valor), 0) as max
            from transacoes
            where tipo = :tipo
              and data_hora >= :inicio
              and data_hora <= :fim
            """, nativeQuery = true)
    StatsRow calcularStats(@Param("tipo") String tipo, @Param("inicio") OffsetDateTime inicio, @Param("fim") OffsetDateTime fim);

    @Query("""
            select t from TransacaoJpaEntity t
            where t.usuarioId = :usuarioId
              and t.dataHora >= :inicio
              and t.dataHora <= :fim
            order by t.dataHora desc
            """)
    List<TransacaoJpaEntity> buscarHistoricoUsuario(@Param("usuarioId") UUID usuarioId, @Param("inicio") OffsetDateTime inicio,
            @Param("fim") OffsetDateTime fim);
}

