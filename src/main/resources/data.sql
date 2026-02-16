-- Só para testar com esses dados

-- USUARIO
INSERT INTO usuario (id, nome, email, senha, plano, data_cadastro)
VALUES (1, 'Ruan Teste', 'ruan@email.com', '123456', 'BASICO', NOW());


-- CATEGORIA
INSERT INTO categoria (id, nome, icone, cor, tipo, padrao_sistema, usuario_id)
VALUES (1, 'Alimentação', 'bi-basket', '#FF6B6B', 'DESPESA', true, NULL);

INSERT INTO categoria (id, nome, icone, cor, tipo, padrao_sistema, usuario_id)
VALUES (2, 'Transporte', 'bi-car-front', '#4D96FF', 'DESPESA', true, NULL);

INSERT INTO categoria (id, nome, icone, cor, tipo, padrao_sistema, usuario_id)
VALUES (3, 'Salário', 'bi-wallet', '#00C897', 'RECEITA', true, NULL);

INSERT INTO categoria (id, nome, icone, cor, tipo, padrao_sistema, usuario_id)
VALUES (4, 'Investimentos', 'bi-graph-up', '#8338EC', 'DESPESA', true, NULL);


-- CATEGORIA PERSONALIZADA
INSERT INTO categoria (id, nome, icone, cor, tipo, padrao_sistema, usuario_id)
VALUES (5, 'Academia', 'bi-heart-pulse', '#F72585', 'DESPESA', false, 1);


-- TRANSAÇÕES
INSERT INTO transacao
(id, descricao, valor, metodo_pagamento, status, data, categoria_id, usuario_id, url_anexo)
VALUES
    (1, 'Salário Mensal', 3000.00, 'PIX', 'PAGO', '2026-02-01', 3, 1, NULL);

INSERT INTO transacao
(id, descricao, valor, metodo_pagamento, status, data, categoria_id, usuario_id, url_anexo)
VALUES
    (2, 'Mercado', 450.00, 'CARTAO_CREDITO', 'PAGO', '2026-02-05', 1, 1, NULL);

INSERT INTO transacao
(id, descricao, valor, metodo_pagamento, status, data, categoria_id, usuario_id, url_anexo)
VALUES
    (3, 'Academia', 120.00, 'DEBITO', 'PAGO', '2026-02-10', 5, 1, NULL);



-- OBJETIVO
INSERT INTO objetivo
(id, nome, valor_objetivo, data_final, cor, icone, usuario_id)
VALUES
    (1, 'Viagem Europa', 10000.00, '2026-12-31', '#3A86FF', 'bi-airplane', 1);


-- OBJETIVO DEPOSITO
INSERT INTO objetivo_deposito
(id, valor, data, objetivo_id, usuario_id)
VALUES
    (1, 1000.00, '2026-01-10', 1, 1);

INSERT INTO objetivo_deposito
(id, valor, data, objetivo_id, usuario_id)
VALUES
    (2, 500.00, '2026-02-10', 1, 1);



-- INVESTIMENTO
INSERT INTO investimento
(id, nome, taxa_atual, tipo, usuario_id)
VALUES
    (1, 'CDB Nubank', 1.00, 'RENDA_FIXA', 1);



-- INVESTIMENTO MOVIMENTAÇÕES
INSERT INTO investimento_movimentacao
(id, valor, data, tipo, investimento_id)
VALUES
    (1, 1000.00, '2026-01-01', 'APORTE', 1);

INSERT INTO investimento_movimentacao
(id, valor, data, tipo, investimento_id)
VALUES
    (2, 10.00, '2026-02-01', 'RENDIMENTO', 1);



-- PLANEJAMENTO MENSAL
INSERT INTO planejamento_mensal
(id, referencia, renda_mensal, percentual_economia, usuario_id)
VALUES
    (1, '2026-02-01', 3000.00, 20.00, 1);



-- PLANEJAMENTO CATEGORIA
INSERT INTO planejamento_categoria
(id, limite, planejamento_id, categoria_id)
VALUES
    (1, 600.00, 1, 1);
