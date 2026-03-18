CREATE TABLE IF NOT EXISTS usuarios (
  id UUID PRIMARY KEY,
  nome VARCHAR(120) NOT NULL,
  email VARCHAR(254) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  saldo NUMERIC(19, 2) NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS transferencias (
  id UUID PRIMARY KEY,
  origem_id UUID NOT NULL REFERENCES usuarios(id),
  destino_id UUID NOT NULL REFERENCES usuarios(id),
  valor NUMERIC(19, 2) NOT NULL,
  data_hora TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS transacoes (
  id UUID PRIMARY KEY,
  tipo VARCHAR(40) NOT NULL,
  usuario_id UUID NULL REFERENCES usuarios(id),
  transferencia_id UUID NULL REFERENCES transferencias(id),
  valor NUMERIC(19, 4) NOT NULL,
  data_hora TIMESTAMPTZ NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_transacoes_tipo_data_hora ON transacoes (tipo, data_hora);
CREATE INDEX IF NOT EXISTS idx_transacoes_usuario_data_hora ON transacoes (usuario_id, data_hora);
CREATE INDEX IF NOT EXISTS idx_transferencias_origem_data_hora ON transferencias (origem_id, data_hora);
CREATE INDEX IF NOT EXISTS idx_transferencias_destino_data_hora ON transferencias (destino_id, data_hora);
