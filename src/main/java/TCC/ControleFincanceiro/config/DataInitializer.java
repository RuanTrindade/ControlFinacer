package TCC.ControleFincanceiro.config;

import TCC.ControleFincanceiro.entity.*;
import TCC.ControleFincanceiro.entity.enumerated.*;
import TCC.ControleFincanceiro.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepo;
    private final CategoriaRepository categoriaRepo;
    private final TransacaoRepository transacaoRepo;
    private final ObjetivoRepository objetivoRepo;
    private final ObjetivoDepositoRepository objetivoDepositoRepo;
    private final InvestimentoRepository investimentoRepo;
    private final InvestimentoMovimentacaoRepository investimentoMovimentacaoRepo;
    private final PlanejamentoMensalRepository planejamentoMensalRepo;
    private final PlanejamentoCategoriaRepository planejamentoCategoriaRepo;
    private final ComprovanteRepository comprovanteRepo;



    public DataInitializer(
            UsuarioRepository usuarioRepo,
            CategoriaRepository categoriaRepo,
            TransacaoRepository transacaoRepo,
            ObjetivoRepository objetivoRepo,
            ObjetivoDepositoRepository objetivoDepositoRepo,
            InvestimentoRepository investimentoRepo,
            InvestimentoMovimentacaoRepository investimentoMovimentacaoRepo,
            PlanejamentoMensalRepository planejamentoMensalRepo,
            PlanejamentoCategoriaRepository planejamentoCategoriaRepo,
            ComprovanteRepository comprovanteRepo
    ) {

        this.usuarioRepo = usuarioRepo;
        this.categoriaRepo = categoriaRepo;
        this.transacaoRepo = transacaoRepo;
        this.objetivoRepo = objetivoRepo;
        this.objetivoDepositoRepo = objetivoDepositoRepo;
        this.investimentoRepo = investimentoRepo;
        this.investimentoMovimentacaoRepo = investimentoMovimentacaoRepo;
        this.planejamentoMensalRepo = planejamentoMensalRepo;
        this.planejamentoCategoriaRepo = planejamentoCategoriaRepo;
        this.comprovanteRepo = comprovanteRepo;
    }

    @Override
    public void run(String... args) {

        // =====================================
        // USUÁRIO
        // =====================================

        Usuario usuario = new Usuario();
        usuario.setNome("Ruan Teste");
        usuario.setEmail("ruan@email.com");
        usuario.setSenha("123456");
        usuario.setPlano(PlanoUsuario.START);
        usuario.setDataCadastro(LocalDateTime.now());

        usuarioRepo.save(usuario);

        // =====================================
        // CATEGORIAS
        // =====================================

        Categoria alimentacao = new Categoria();
        alimentacao.setNome("Alimentação");
        alimentacao.setIcone("BiRestaurant");
        alimentacao.setCor("#FF6B6B");
        alimentacao.setTipo(TipoTransacao.DESPESA);
        alimentacao.setPadraoSistema(true);

        categoriaRepo.save(alimentacao);

        Categoria transporte = new Categoria();
        transporte.setNome("Transporte");
        transporte.setIcone("BiCar");
        transporte.setCor("#4D96FF");
        transporte.setTipo(TipoTransacao.DESPESA);
        transporte.setPadraoSistema(true);

        categoriaRepo.save(transporte);

        Categoria salario = new Categoria();
        salario.setNome("Salário");
        salario.setIcone("BiWallet");
        salario.setCor("#00C897");
        salario.setTipo(TipoTransacao.RECEITA);
        salario.setPadraoSistema(true);

        categoriaRepo.save(salario);

        Categoria investimentos = new Categoria();
        investimentos.setNome("Investimentos");
        investimentos.setIcone("BiDollarCircle");
        investimentos.setCor("#8338EC");
        investimentos.setTipo(TipoTransacao.DESPESA);
        investimentos.setPadraoSistema(true);

        categoriaRepo.save(investimentos);

        Categoria resgateInvestimento = new Categoria();
        resgateInvestimento.setNome("Investimento ");
        resgateInvestimento.setIcone("BiTransfer");
        resgateInvestimento.setCor("#00C897");
        resgateInvestimento.setTipo(TipoTransacao.RECEITA);
        resgateInvestimento.setPadraoSistema(true);

        categoriaRepo.save(resgateInvestimento);

        Categoria objetivos = new Categoria();

        objetivos.setNome("Objetivos");
        objetivos.setIcone("BiDonateHeart");
        objetivos.setCor("#8b5cf6");
        objetivos.setTipo(TipoTransacao.DESPESA);
        objetivos.setPadraoSistema(true);
        objetivos.setInternaSistema(true);

        categoriaRepo.save(objetivos);


        Categoria resgateObjetivo =
                new Categoria();

        resgateObjetivo.setNome(
                "Resgate de Objetivo"
        );

        resgateObjetivo.setIcone(
                "BiTransfer"
        );

        resgateObjetivo.setCor(
                "#22c55e"
        );

        resgateObjetivo.setTipo(
                TipoTransacao.RECEITA
        );

        resgateObjetivo.setPadraoSistema(true);
        resgateObjetivo.setInternaSistema(true);

        categoriaRepo.save(
                resgateObjetivo
        );

        Categoria academia = new Categoria();
        academia.setNome("Academia");
        academia.setIcone("BiDumbbell");
        academia.setCor("#F72585");
        academia.setTipo(TipoTransacao.DESPESA);
        academia.setPadraoSistema(false);
        academia.setUsuario(usuario);

        categoriaRepo.save(academia);

        // =====================================
        // TRANSAÇÕES
        // =====================================


        Comprovante comprovantePix = new Comprovante();
        comprovantePix.setNomeArquivo("comprovante_picpay_PIX.pdf");
        comprovantePix.setUrlArquivo("uploads/1777396053857_comprovante_picpay_PIX_21042026181534.pdf");

        comprovanteRepo.save(comprovantePix);

        Transacao salarioTx = new Transacao();
        salarioTx.setDescricao("Salário Mensal");
        salarioTx.setValor(BigDecimal.valueOf(30000));
        salarioTx.setMetodoPagamento(MetodoPagamento.PIX);
        salarioTx.setStatus(StatusPagamento.PAGO);
        salarioTx.setData(LocalDate.of(2026, 2, 1));
        salarioTx.setCategoria(salario);
        salarioTx.setUsuario(usuario);

        transacaoRepo.save(salarioTx);

        Transacao mercadoTx = new Transacao();
        mercadoTx.setDescricao("Mercado");
        mercadoTx.setValor(BigDecimal.valueOf(450));
        mercadoTx.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO);
        mercadoTx.setStatus(StatusPagamento.PAGO);
        mercadoTx.setData(LocalDate.of(2026, 2, 5));
        mercadoTx.setCategoria(alimentacao);
        mercadoTx.setUsuario(usuario);
        mercadoTx.setComprovante(comprovantePix);

        transacaoRepo.save(mercadoTx);

        Transacao academiaTx = new Transacao();
        academiaTx.setDescricao("Academia");
        academiaTx.setValor(BigDecimal.valueOf(120));
        academiaTx.setMetodoPagamento(MetodoPagamento.DEBITO);
        academiaTx.setStatus(StatusPagamento.PENDENTE);
        academiaTx.setData(LocalDate.of(2026, 2, 10));
        academiaTx.setCategoria(academia);
        academiaTx.setUsuario(usuario);

        transacaoRepo.save(academiaTx);

        // =====================================
        // OBJETIVO
        // =====================================

        Objetivo objetivo = new Objetivo();
        objetivo.setNome("Viagem Europa");
        objetivo.setValorObjetivo(BigDecimal.valueOf(10000));
        objetivo.setDataFinal(LocalDate.of(2026, 12, 31));
        objetivo.setCor("#3A86FF");
        objetivo.setIcone("bi-airplane");
        objetivo.setUsuario(usuario);

        objetivoRepo.save(objetivo);

        // =====================================
        // DEPÓSITO OBJETIVO 1
        // =====================================

        Transacao depTx1 = new Transacao();
        depTx1.setDescricao("Objetivo: Viagem Europa");
        depTx1.setValor(BigDecimal.valueOf(1000));
        depTx1.setMetodoPagamento(MetodoPagamento.TRANSFERENCIA);
        depTx1.setStatus(StatusPagamento.PAGO);
        depTx1.setData(LocalDate.of(2026, 1, 10));
        depTx1.setCategoria(objetivos);
        depTx1.setUsuario(usuario);

        transacaoRepo.save(depTx1);

        ObjetivoDeposito dep1 = new ObjetivoDeposito();
        dep1.setValor(BigDecimal.valueOf(1000));
        dep1.setData(LocalDate.of(2026, 1, 10));
        dep1.setObjetivo(objetivo);
        dep1.setUsuario(usuario);
        dep1.setTransacao(depTx1);

        objetivoDepositoRepo.save(dep1);

        // =====================================
        // DEPÓSITO OBJETIVO 2
        // =====================================

        Transacao depTx2 = new Transacao();
        depTx2.setDescricao("Objetivo: Viagem Europa");
        depTx2.setValor(BigDecimal.valueOf(500));
        depTx2.setMetodoPagamento(MetodoPagamento.TRANSFERENCIA);
        depTx2.setStatus(StatusPagamento.PAGO);
        depTx2.setData(LocalDate.of(2026, 2, 10));
        depTx2.setCategoria(objetivos);
        depTx2.setUsuario(usuario);

        transacaoRepo.save(depTx2);

        ObjetivoDeposito dep2 = new ObjetivoDeposito();
        dep2.setValor(BigDecimal.valueOf(500));
        dep2.setData(LocalDate.of(2026, 2, 10));
        dep2.setObjetivo(objetivo);
        dep2.setUsuario(usuario);
        dep2.setTransacao(depTx2);

        objetivoDepositoRepo.save(dep2);

        // =====================================
// INVESTIMENTO
// =====================================

        /*
         * Garante que as categorias internas
         * de investimento fiquem identificadas
         * corretamente no sistema.
         */
        investimentos.setInternaSistema(true);
        categoriaRepo.save(investimentos);

        resgateInvestimento.setNome(
                "Resgate de Investimento"
        );

        resgateInvestimento.setInternaSistema(true);
        categoriaRepo.save(resgateInvestimento);


        /*
         * Cria o investimento.
         */
        Investimento investimento =
                new Investimento();

        investimento.setNome(
                "CDB Nubank"
        );

        investimento.setTipo(
                TipoAtivo.RENDA_FIXA
        );

        investimento.setUsuario(
                usuario
        );

        investimento.setDataCriacao(
                LocalDate.of(
                        2026,
                        1,
                        1
                )
        );

        investimento.setEncerrado(
                false
        );

        investimentoRepo.save(
                investimento
        );


// =====================================
// APORTE NO INVESTIMENTO
// =====================================

        /*
         * O aporte gera uma transação de DESPESA
         * na conta geral do usuário.
         */
        Transacao aporteTx =
                new Transacao();

        aporteTx.setDescricao(
                "Aporte em investimento: CDB Nubank"
        );

        aporteTx.setValor(
                BigDecimal.valueOf(1000)
        );

        aporteTx.setMetodoPagamento(
                MetodoPagamento.TRANSFERENCIA
        );

        aporteTx.setStatus(
                StatusPagamento.PAGO
        );

        aporteTx.setData(
                LocalDate.of(
                        2026,
                        1,
                        1
                )
        );

        aporteTx.setCategoria(
                investimentos
        );

        aporteTx.setUsuario(
                usuario
        );

        /*
         * Como essa transação pertence
         * ao módulo de investimentos,
         * ela não poderá ser editada/excluída
         * diretamente pela tela de Transações.
         */
        aporteTx.setBloqueadaSistema(
                true
        );

        transacaoRepo.save(
                aporteTx
        );


        /*
         * Registra a movimentação interna
         * relacionada ao aporte.
         */
        InvestimentoMovimentacao movAporte =
                new InvestimentoMovimentacao();

        movAporte.setValor(
                BigDecimal.valueOf(1000)
        );

        movAporte.setData(
                LocalDateTime.of(
                        2026,
                        1,
                        1,
                        10,
                        0
                )
        );

        movAporte.setTipo(
                TipoInvestimento.APORTE
        );

        movAporte.setInvestimento(
                investimento
        );

        movAporte.setTransacao(
                aporteTx
        );

        investimentoMovimentacaoRepo.save(
                movAporte
        );





// =====================================
// NOVO APORTE APÓS ATUALIZAÇÃO
// =====================================

        /*
         * Este segundo aporte serve também
         * para testar nossa regra:
         *
         * última atualização = R$ 1.010
         * + novo aporte = R$ 500
         *
         * saldo atual esperado = R$ 1.510
         */
        Transacao segundoAporteTx =
                new Transacao();

        segundoAporteTx.setDescricao(
                "Aporte em investimento: CDB Nubank"
        );

        segundoAporteTx.setValor(
                BigDecimal.valueOf(500)
        );

        segundoAporteTx.setMetodoPagamento(
                MetodoPagamento.TRANSFERENCIA
        );

        segundoAporteTx.setStatus(
                StatusPagamento.PAGO
        );

        segundoAporteTx.setData(
                LocalDate.of(
                        2026,
                        2,
                        10
                )
        );

        segundoAporteTx.setCategoria(
                investimentos
        );

        segundoAporteTx.setUsuario(
                usuario
        );

        segundoAporteTx.setBloqueadaSistema(
                true
        );

        transacaoRepo.save(
                segundoAporteTx
        );


        InvestimentoMovimentacao movSegundoAporte =
                new InvestimentoMovimentacao();

        movSegundoAporte.setValor(
                BigDecimal.valueOf(500)
        );

        movSegundoAporte.setData(
                LocalDateTime.of(
                        2026,
                        2,
                        10,
                        10,
                        0
                )
        );

        movSegundoAporte.setTipo(
                TipoInvestimento.APORTE
        );

        movSegundoAporte.setInvestimento(
                investimento
        );

        movSegundoAporte.setTransacao(
                segundoAporteTx
        );

        investimentoMovimentacaoRepo.save(
                movSegundoAporte
        );


// =====================================
// RESGATE PARCIAL
// =====================================

        /*
         * Saldo esperado antes do resgate:
         *
         * R$ 1.010
         * + R$ 500
         * = R$ 1.510
         *
         * Resgate:
         * R$ 300
         *
         * Saldo esperado depois:
         * R$ 1.210
         */
        Transacao resgateTx =
                new Transacao();

        resgateTx.setDescricao(
                "Resgate de investimento: CDB Nubank"
        );

        resgateTx.setValor(
                BigDecimal.valueOf(300)
        );

        resgateTx.setMetodoPagamento(
                MetodoPagamento.TRANSFERENCIA
        );

        resgateTx.setStatus(
                StatusPagamento.PAGO
        );

        resgateTx.setData(
                LocalDate.of(
                        2026,
                        2,
                        15
                )
        );

        resgateTx.setCategoria(
                resgateInvestimento
        );

        resgateTx.setUsuario(
                usuario
        );

        resgateTx.setBloqueadaSistema(
                true
        );

        transacaoRepo.save(
                resgateTx
        );


        InvestimentoMovimentacao movResgate =
                new InvestimentoMovimentacao();

        movResgate.setValor(
                BigDecimal.valueOf(300)
        );

        movResgate.setData(
                LocalDateTime.of(
                        2026,
                        2,
                        15,
                        10,
                        0
                )
        );

        movResgate.setTipo(
                TipoInvestimento.RESGATE
        );

        movResgate.setInvestimento(
                investimento
        );

        movResgate.setTransacao(
                resgateTx
        );

        investimentoMovimentacaoRepo.save(
                movResgate
        );




        // =====================================
// DADOS DE TESTE - JULHO E AGOSTO
// =====================================


        /*
         * ===================================================
         * CDB NUBANK
         * ===================================================
         *
         * Vamos preparar uma sequência que permita testar
         * corretamente julho e agosto.
         *
         * Saldo que tínhamos após fevereiro:
         *
         * atualização fevereiro = 1.010
         * + aporte fevereiro     = 500
         * - resgate fevereiro    = 300
         *
         * saldo = 1.210
         */




// =====================================
// APORTE - JULHO
// =====================================

        Transacao aporteJulhoTx =
                new Transacao();

        aporteJulhoTx.setDescricao(
                "Aporte em investimento: CDB Nubank"
        );

        aporteJulhoTx.setValor(
                BigDecimal.valueOf(400)
        );

        aporteJulhoTx.setMetodoPagamento(
                MetodoPagamento.TRANSFERENCIA
        );

        aporteJulhoTx.setStatus(
                StatusPagamento.PAGO
        );

        aporteJulhoTx.setData(
                LocalDate.of(
                        2026,
                        7,
                        10
                )
        );

        aporteJulhoTx.setCategoria(
                investimentos
        );

        aporteJulhoTx.setUsuario(
                usuario
        );

        aporteJulhoTx.setBloqueadaSistema(
                true
        );

        transacaoRepo.save(
                aporteJulhoTx
        );


        InvestimentoMovimentacao movAporteJulho =
                new InvestimentoMovimentacao();

        movAporteJulho.setValor(
                BigDecimal.valueOf(400)
        );

        movAporteJulho.setData(
                LocalDateTime.of(
                        2026,
                        7,
                        10,
                        10,
                        0
                )
        );

        movAporteJulho.setTipo(
                TipoInvestimento.APORTE
        );

        movAporteJulho.setInvestimento(
                investimento
        );

        movAporteJulho.setTransacao(
                aporteJulhoTx
        );

        investimentoMovimentacaoRepo.save(
                movAporteJulho
        );


// =====================================
// RESGATE - JULHO
// =====================================

        Transacao resgateJulhoTx =
                new Transacao();

        resgateJulhoTx.setDescricao(
                "Resgate de investimento: CDB Nubank"
        );

        resgateJulhoTx.setValor(
                BigDecimal.valueOf(100)
        );

        resgateJulhoTx.setMetodoPagamento(
                MetodoPagamento.TRANSFERENCIA
        );

        resgateJulhoTx.setStatus(
                StatusPagamento.PAGO
        );

        resgateJulhoTx.setData(
                LocalDate.of(
                        2026,
                        7,
                        20
                )
        );

        resgateJulhoTx.setCategoria(
                resgateInvestimento
        );

        resgateJulhoTx.setUsuario(
                usuario
        );

        resgateJulhoTx.setBloqueadaSistema(
                true
        );

        transacaoRepo.save(
                resgateJulhoTx
        );


        InvestimentoMovimentacao movResgateJulho =
                new InvestimentoMovimentacao();

        movResgateJulho.setValor(
                BigDecimal.valueOf(100)
        );

        movResgateJulho.setData(
                LocalDateTime.of(
                        2026,
                        7,
                        20,
                        14,
                        0
                )
        );

        movResgateJulho.setTipo(
                TipoInvestimento.RESGATE
        );

        movResgateJulho.setInvestimento(
                investimento
        );

        movResgateJulho.setTransacao(
                resgateJulhoTx
        );

        investimentoMovimentacaoRepo.save(
                movResgateJulho
        );




// =====================================
// APORTE - AGOSTO
// =====================================

        Transacao aporteAgostoTx =
                new Transacao();

        aporteAgostoTx.setDescricao(
                "Aporte em investimento: CDB Nubank"
        );

        aporteAgostoTx.setValor(
                BigDecimal.valueOf(500)
        );

        aporteAgostoTx.setMetodoPagamento(
                MetodoPagamento.TRANSFERENCIA
        );

        aporteAgostoTx.setStatus(
                StatusPagamento.PAGO
        );

        aporteAgostoTx.setData(
                LocalDate.of(
                        2026,
                        8,
                        5
                )
        );

        aporteAgostoTx.setCategoria(
                investimentos
        );

        aporteAgostoTx.setUsuario(
                usuario
        );

        aporteAgostoTx.setBloqueadaSistema(
                true
        );

        transacaoRepo.save(
                aporteAgostoTx
        );


        InvestimentoMovimentacao movAporteAgosto =
                new InvestimentoMovimentacao();

        movAporteAgosto.setValor(
                BigDecimal.valueOf(500)
        );

        movAporteAgosto.setData(
                LocalDateTime.of(
                        2026,
                        8,
                        5,
                        10,
                        0
                )
        );

        movAporteAgosto.setTipo(
                TipoInvestimento.APORTE
        );

        movAporteAgosto.setInvestimento(
                investimento
        );

        movAporteAgosto.setTransacao(
                aporteAgostoTx
        );

        investimentoMovimentacaoRepo.save(
                movAporteAgosto
        );


// =====================================
// RESGATE - AGOSTO
// =====================================

        Transacao resgateAgostoTx =
                new Transacao();

        resgateAgostoTx.setDescricao(
                "Resgate de investimento: CDB Nubank"
        );

        resgateAgostoTx.setValor(
                BigDecimal.valueOf(200)
        );

        resgateAgostoTx.setMetodoPagamento(
                MetodoPagamento.TRANSFERENCIA
        );

        resgateAgostoTx.setStatus(
                StatusPagamento.PAGO
        );

        resgateAgostoTx.setData(
                LocalDate.of(
                        2026,
                        8,
                        12
                )
        );

        resgateAgostoTx.setCategoria(
                resgateInvestimento
        );

        resgateAgostoTx.setUsuario(
                usuario
        );

        resgateAgostoTx.setBloqueadaSistema(
                true
        );

        transacaoRepo.save(
                resgateAgostoTx
        );


        InvestimentoMovimentacao movResgateAgosto =
                new InvestimentoMovimentacao();

        movResgateAgosto.setValor(
                BigDecimal.valueOf(200)
        );

        movResgateAgosto.setData(
                LocalDateTime.of(
                        2026,
                        8,
                        12,
                        14,
                        0
                )
        );

        movResgateAgosto.setTipo(
                TipoInvestimento.RESGATE
        );

        movResgateAgosto.setInvestimento(
                investimento
        );

        movResgateAgosto.setTransacao(
                resgateAgostoTx
        );

        investimentoMovimentacaoRepo.save(
                movResgateAgosto
        );


// =====================================
// ATUALIZAÇÃO - AGOSTO
// =====================================

//        InvestimentoValorAtualizacao atualizacaoAgosto =
//                new InvestimentoValorAtualizacao();
//
//        atualizacaoAgosto.setInvestimento(
//                investimento
//        );
//
//        atualizacaoAgosto.setValorAtual(
//                BigDecimal.valueOf(2000)
//        );
//
//        atualizacaoAgosto.setReferencia(
//                LocalDate.of(
//                        2026,
//                        8,
//                        1
//                )
//        );
//
//        atualizacaoAgosto.setDataAtualizacao(
//                LocalDateTime.of(
//                        2026,
//                        8,
//                        31,
//                        18,
//                        0
//                )
//        );
//
//        investimentoValorAtualizacaoRepo.save(
//                atualizacaoAgosto
//        );


// ===================================================
// SEGUNDO INVESTIMENTO - TESOURO SELIC
// ===================================================

        Investimento tesouro =
                new Investimento();

        tesouro.setNome(
                "Tesouro Selic"
        );

        tesouro.setTipo(
                TipoAtivo.RENDA_FIXA
        );

        tesouro.setUsuario(
                usuario
        );

        tesouro.setDataCriacao(
                LocalDate.of(
                        2026,
                        7,
                        1
                )
        );

        tesouro.setEncerrado(
                false
        );

        investimentoRepo.save(
                tesouro
        );


// =====================================
// APORTE INICIAL TESOURO - JULHO
// =====================================

        Transacao aporteTesouroJulhoTx =
                new Transacao();

        aporteTesouroJulhoTx.setDescricao(
                "Aporte em investimento: Tesouro Selic"
        );

        aporteTesouroJulhoTx.setValor(
                BigDecimal.valueOf(2000)
        );

        aporteTesouroJulhoTx.setMetodoPagamento(
                MetodoPagamento.TRANSFERENCIA
        );

        aporteTesouroJulhoTx.setStatus(
                StatusPagamento.PAGO
        );

        aporteTesouroJulhoTx.setData(
                LocalDate.of(
                        2026,
                        7,
                        5
                )
        );

        aporteTesouroJulhoTx.setCategoria(
                investimentos
        );

        aporteTesouroJulhoTx.setUsuario(
                usuario
        );

        aporteTesouroJulhoTx.setBloqueadaSistema(
                true
        );

        transacaoRepo.save(
                aporteTesouroJulhoTx
        );


        InvestimentoMovimentacao movTesouroJulho =
                new InvestimentoMovimentacao();

        movTesouroJulho.setValor(
                BigDecimal.valueOf(2000)
        );

        movTesouroJulho.setData(
                LocalDateTime.of(
                        2026,
                        7,
                        5,
                        11,
                        0
                )
        );

        movTesouroJulho.setTipo(
                TipoInvestimento.APORTE
        );

        movTesouroJulho.setInvestimento(
                tesouro
        );

        movTesouroJulho.setTransacao(
                aporteTesouroJulhoTx
        );

        investimentoMovimentacaoRepo.save(
                movTesouroJulho
        );




// =====================================
// APORTE TESOURO - AGOSTO
// =====================================

        Transacao aporteTesouroAgostoTx =
                new Transacao();

        aporteTesouroAgostoTx.setDescricao(
                "Aporte em investimento: Tesouro Selic"
        );

        aporteTesouroAgostoTx.setValor(
                BigDecimal.valueOf(300)
        );

        aporteTesouroAgostoTx.setMetodoPagamento(
                MetodoPagamento.TRANSFERENCIA
        );

        aporteTesouroAgostoTx.setStatus(
                StatusPagamento.PAGO
        );

        aporteTesouroAgostoTx.setData(
                LocalDate.of(
                        2026,
                        8,
                        8
                )
        );

        aporteTesouroAgostoTx.setCategoria(
                investimentos
        );

        aporteTesouroAgostoTx.setUsuario(
                usuario
        );

        aporteTesouroAgostoTx.setBloqueadaSistema(
                true
        );

        transacaoRepo.save(
                aporteTesouroAgostoTx
        );


        InvestimentoMovimentacao movTesouroAgosto =
                new InvestimentoMovimentacao();

        movTesouroAgosto.setValor(
                BigDecimal.valueOf(300)
        );

        movTesouroAgosto.setData(
                LocalDateTime.of(
                        2026,
                        8,
                        8,
                        11,
                        0
                )
        );

        movTesouroAgosto.setTipo(
                TipoInvestimento.APORTE
        );

        movTesouroAgosto.setInvestimento(
                tesouro
        );

        movTesouroAgosto.setTransacao(
                aporteTesouroAgostoTx
        );

        investimentoMovimentacaoRepo.save(
                movTesouroAgosto
        );



        // =====================================
        // PLANEJAMENTO
        // =====================================



        System.out.println(">>> Dados iniciais populados com sucesso!");
    }
}