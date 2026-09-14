package TCC.ControleFincanceiro.config;

import TCC.ControleFincanceiro.entity.*;
import TCC.ControleFincanceiro.entity.enumerated.*;
import TCC.ControleFincanceiro.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final String DEMO_EMAIL = "ruan@email.com";
    private static final String DEMO_PASSWORD = "Flux@2026";

    private final UsuarioRepository usuarioRepo;
    private final CategoriaRepository categoriaRepo;
    private final TransacaoRepository transacaoRepo;
    private final ObjetivoRepository objetivoRepo;
    private final ObjetivoDepositoRepository objetivoDepositoRepo;
    private final InvestimentoRepository investimentoRepo;
    private final InvestimentoMovimentacaoRepository investimentoMovimentacaoRepo;
    private final PlanejamentoMensalRepository planejamentoMensalRepo;
    private final PlanejamentoCategoriaRepository planejamentoCategoriaRepo;
    private final PasswordEncoder passwordEncoder;

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
            PasswordEncoder passwordEncoder
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
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        // Evita duplicar o cenário demo a cada inicialização do PostgreSQL.
        if (usuarioRepo.existsByEmail(DEMO_EMAIL)) {
            System.out.println(">>> Dados demo já existem. DataInitializer ignorado.");
            return;
        }

        // =========================================================
        // USUÁRIO DEMO
        // =========================================================
        Usuario usuario = new Usuario();
        usuario.setNome("Ruan");
        usuario.setEmail(DEMO_EMAIL);
        usuario.setSenha(passwordEncoder.encode(DEMO_PASSWORD));
        usuario.setPlano(PlanoUsuario.PREMIUM);
        usuario.setProvider(Provider.LOCAL);
        usuario.setDataCadastro(LocalDateTime.of(2026, 2, 1, 10, 0));
        usuario = usuarioRepo.save(usuario);

        // =========================================================
        // CATEGORIAS VISÍVEIS - RECEITAS
        // =========================================================
        Categoria salario = criarCategoria(
                "Salário", "BiWallet", "#22C55E",
                TipoTransacao.RECEITA, true, false, null
        );

        Categoria freelance = criarCategoria(
                "Freelance", "BiLaptop", "#16A34A",
                TipoTransacao.RECEITA, false, false, usuario
        );

        Categoria outrasReceitas = criarCategoria(
                "Outras Receitas", "BiDollarCircle", "#10B981",
                TipoTransacao.RECEITA, false, false, usuario
        );

        // =========================================================
        // CATEGORIAS VISÍVEIS - DESPESAS
        // =========================================================
        Categoria alimentacao = criarCategoria(
                "Alimentação", "BiRestaurant", "#FB7185",
                TipoTransacao.DESPESA, true, false, null
        );

        Categoria mercado = criarCategoria(
                "Mercado", "BiCart", "#22C55E",
                TipoTransacao.DESPESA, true, false, null
        );

        Categoria transporte = criarCategoria(
                "Transporte", "BiCar", "#3B82F6",
                TipoTransacao.DESPESA, true, false, null
        );

        Categoria lazer = criarCategoria(
                "Lazer", "BiJoystick", "#F59E0B",
                TipoTransacao.DESPESA, true, false, null
        );

        Categoria compras = criarCategoria(
                "Compras", "BiShoppingBag", "#EC4899",
                TipoTransacao.DESPESA, true, false, null
        );

        Categoria educacao = criarCategoria(
                "Educação", "BiBook", "#6366F1",
                TipoTransacao.DESPESA, true, false, null
        );

        Categoria restaurantes = criarCategoria(
                "Restaurantes", "BiRestaurant", "#F97316",
                TipoTransacao.DESPESA, true, false, null
        );

        Categoria contas = criarCategoria(
                "Contas", "BiReceipt", "#06B6D4",
                TipoTransacao.DESPESA, true, false, null
        );

        Categoria streaming = criarCategoria(
                "Streaming", "BiMoviePlay", "#8B5CF6",
                TipoTransacao.DESPESA, false, false, usuario
        );

        Categoria celular = criarCategoria(
                "Celular", "BiMobile", "#0EA5E9",
                TipoTransacao.DESPESA, false, false, usuario
        );

        Categoria jogos = criarCategoria(
                "Jogos", "BiJoystick", "#A855F7",
                TipoTransacao.DESPESA, false, false, usuario
        );

        Categoria academia = criarCategoria(
                "Academia", "BiDumbbell", "#F72585",
                TipoTransacao.DESPESA, false, false, usuario
        );

        // =========================================================
        // CATEGORIAS INTERNAS DO SISTEMA
        // Não aparecem na tela normal de categorias.
        // =========================================================
        Categoria investimentos = criarCategoria(
                "Investimentos", "BiDollarCircle", "#8338EC",
                TipoTransacao.DESPESA, true, true, null
        );

        Categoria resgateInvestimento = criarCategoria(
                "Resgate de Investimento", "BiTransfer", "#00C897",
                TipoTransacao.RECEITA, true, true, null
        );

        Categoria objetivos = criarCategoria(
                "Objetivos", "BiDonateHeart", "#8B5CF6",
                TipoTransacao.DESPESA, true, true, null
        );

        Categoria resgateObjetivo = criarCategoria(
                "Resgate de Objetivo", "BiTransfer", "#22C55E",
                TipoTransacao.RECEITA, true, true, null
        );

        // =========================================================
        // SALDO ANTERIOR
        // Fica em fevereiro e não interfere no gráfico Mar-Ago.
        // Serve apenas para deixar o saldo acumulado coerente com
        // os aportes, objetivos e despesas dos meses seguintes.
        // =========================================================
        criarTransacao(
                usuario, outrasReceitas,
                "Saldo anterior da conta",
                "3500.00", MetodoPagamento.TRANSFERENCIA,
                StatusPagamento.PAGO, LocalDate.of(2026, 2, 20), false
        );

        // =========================================================
        // TRANSAÇÕES - MARÇO A AGOSTO DE 2026
        // O Relatório selecionado em AGOSTO exibirá exatamente
        // seis meses: 03/2026 a 08/2026.
        // =========================================================

        // ------------------------- MARÇO -------------------------
        criarReceitaMensal(usuario, salario, 3, "2800.00");
        criarTransacao(usuario, freelance, "Freelance - identidade visual", "250.00",
                MetodoPagamento.PIX, StatusPagamento.PAGO, LocalDate.of(2026, 3, 18), false);
        criarDespesasMensais(usuario, 3, educacao, mercado, transporte, streaming,
                celular, academia, lazer, compras, restaurantes, jogos, contas,
                "340.00", "180.00", "210.00", "160.00", "120.00", "79.90");

        // ------------------------- ABRIL -------------------------
        criarReceitaMensal(usuario, salario, 4, "2800.00");
        criarDespesasMensais(usuario, 4, educacao, mercado, transporte, streaming,
                celular, academia, lazer, compras, restaurantes, jogos, contas,
                "380.00", "160.00", "280.00", "120.00", "145.00", null);

        // ------------------------- MAIO --------------------------
        criarReceitaMensal(usuario, salario, 5, "2800.00");
        criarTransacao(usuario, freelance, "Freelance - arte para redes sociais", "180.00",
                MetodoPagamento.PIX, StatusPagamento.PAGO, LocalDate.of(2026, 5, 20), false);
        criarDespesasMensais(usuario, 5, educacao, mercado, transporte, streaming,
                celular, academia, lazer, compras, restaurantes, jogos, contas,
                "420.00", "200.00", "180.00", "250.00", "130.00", "49.90");

        // ------------------------- JUNHO -------------------------
        criarReceitaMensal(usuario, salario, 6, "2800.00");
        criarTransacao(usuario, freelance, "Freelance - landing page", "350.00",
                MetodoPagamento.PIX, StatusPagamento.PAGO, LocalDate.of(2026, 6, 22), false);
        criarDespesasMensais(usuario, 6, educacao, mercado, transporte, streaming,
                celular, academia, lazer, compras, restaurantes, jogos, contas,
                "400.00", "190.00", "240.00", "180.00", "160.00", null);

        // ------------------------- JULHO -------------------------
        criarReceitaMensal(usuario, salario, 7, "2800.00");
        criarTransacao(usuario, freelance, "Freelance - manutenção de site", "150.00",
                MetodoPagamento.PIX, StatusPagamento.PAGO, LocalDate.of(2026, 7, 19), false);
        criarDespesasMensais(usuario, 7, educacao, mercado, transporte, streaming,
                celular, academia, lazer, compras, restaurantes, jogos, contas,
                "450.00", "220.00", "320.00", "350.00", "170.00", "129.90");

        // ------------------------- AGOSTO ------------------------
        criarReceitaMensal(usuario, salario, 8, "2800.00");
        criarTransacao(usuario, freelance, "Freelance - projeto React", "420.00",
                MetodoPagamento.PIX, StatusPagamento.PAGO, LocalDate.of(2026, 8, 21), false);
        criarDespesasMensais(usuario, 8, educacao, mercado, transporte, streaming,
                celular, academia, lazer, compras, restaurantes, jogos, contas,
                "390.00", "180.00", "150.00", "210.00", "140.00", "59.90");

        // =========================================================
        // SETEMBRO - SOMENTE DIAS 01 E 02
        // Deixa o dashboard atual com dados sem criar datas futuras.
        // =========================================================
        criarTransacao(usuario, salario, "Salário - Setembro", "2800.00",
                MetodoPagamento.TRANSFERENCIA, StatusPagamento.PAGO,
                LocalDate.of(2026, 9, 1), false);
        criarTransacao(usuario, streaming, "Netflix", "39.90",
                MetodoPagamento.CARTAO_CREDITO, StatusPagamento.PAGO,
                LocalDate.of(2026, 9, 1), false);
        criarTransacao(usuario, streaming, "Spotify", "21.90",
                MetodoPagamento.CARTAO_CREDITO, StatusPagamento.PAGO,
                LocalDate.of(2026, 9, 1), false);
        criarTransacao(usuario, streaming, "Prime Video", "19.90",
                MetodoPagamento.CARTAO_CREDITO, StatusPagamento.PAGO,
                LocalDate.of(2026, 9, 1), false);
        criarTransacao(usuario, celular, "Plano de celular", "59.90",
                MetodoPagamento.PIX, StatusPagamento.PAGO,
                LocalDate.of(2026, 9, 1), false);
        criarTransacao(usuario, contas, "Internet residencial", "99.90",
                MetodoPagamento.PIX, StatusPagamento.PAGO,
                LocalDate.of(2026, 9, 1), false);
        criarTransacao(usuario, mercado, "Mercado da semana", "180.00",
                MetodoPagamento.DEBITO, StatusPagamento.PAGO,
                LocalDate.of(2026, 9, 2), false);
        criarTransacao(usuario, transporte, "Combustível", "120.00",
                MetodoPagamento.PIX, StatusPagamento.PAGO,
                LocalDate.of(2026, 9, 2), false);
        criarTransacao(usuario, compras, "Fone bluetooth", "120.00",
                MetodoPagamento.CARTAO_CREDITO, StatusPagamento.PENDENTE,
                LocalDate.of(2026, 9, 2), false);

        // =========================================================
        // PLANEJAMENTO MENSAL - MARÇO A SETEMBRO
        // Há meses/categorias abaixo, no limite e acima do limite.
        // =========================================================
        PlanejamentoMensal pMar = criarPlanejamento(usuario, 3, "2800.00", "20.00");
        adicionarLimite(pMar, mercado, "400.00");
        adicionarLimite(pMar, transporte, "200.00");
        adicionarLimite(pMar, lazer, "200.00"); // gasto 210 -> estourou
        adicionarLimite(pMar, streaming, "100.00");
        adicionarLimite(pMar, compras, "200.00");
        adicionarLimite(pMar, celular, "80.00");
        adicionarLimite(pMar, educacao, "700.00");
        adicionarLimite(pMar, academia, "100.00");

        PlanejamentoMensal pAbr = criarPlanejamento(usuario, 4, "2800.00", "20.00");
        adicionarLimite(pAbr, mercado, "450.00"); // gasto 380 -> abaixo
        adicionarLimite(pAbr, transporte, "200.00");
        adicionarLimite(pAbr, lazer, "250.00");   // gasto 280 -> estourou
        adicionarLimite(pAbr, streaming, "100.00");
        adicionarLimite(pAbr, compras, "200.00");
        adicionarLimite(pAbr, celular, "80.00");
        adicionarLimite(pAbr, educacao, "700.00");
        adicionarLimite(pAbr, academia, "100.00");

        PlanejamentoMensal pMai = criarPlanejamento(usuario, 5, "2800.00", "20.00");
        adicionarLimite(pMai, mercado, "450.00");
        adicionarLimite(pMai, transporte, "200.00"); // gasto 200 -> exato
        adicionarLimite(pMai, lazer, "200.00");
        adicionarLimite(pMai, streaming, "100.00");
        adicionarLimite(pMai, compras, "250.00");    // gasto 250 -> exato
        adicionarLimite(pMai, celular, "80.00");
        adicionarLimite(pMai, educacao, "700.00");
        adicionarLimite(pMai, academia, "100.00");

        PlanejamentoMensal pJun = criarPlanejamento(usuario, 6, "2800.00", "20.00");
        adicionarLimite(pJun, mercado, "400.00");    // gasto 400 -> exato
        adicionarLimite(pJun, transporte, "180.00"); // gasto 190 -> estourou
        adicionarLimite(pJun, lazer, "250.00");
        adicionarLimite(pJun, streaming, "100.00");
        adicionarLimite(pJun, compras, "200.00");
        adicionarLimite(pJun, celular, "80.00");
        adicionarLimite(pJun, educacao, "700.00");
        adicionarLimite(pJun, academia, "100.00");

        PlanejamentoMensal pJul = criarPlanejamento(usuario, 7, "2800.00", "20.00");
        adicionarLimite(pJul, mercado, "450.00");    // gasto 450 -> exato
        adicionarLimite(pJul, transporte, "250.00");
        adicionarLimite(pJul, lazer, "300.00");      // gasto 320 -> estourou
        adicionarLimite(pJul, streaming, "100.00");
        adicionarLimite(pJul, compras, "350.00");    // gasto 350 -> exato
        adicionarLimite(pJul, celular, "80.00");
        adicionarLimite(pJul, educacao, "700.00");
        adicionarLimite(pJul, academia, "100.00");

        // Agosto foi pensado para ficar visualmente bom no Relatório:
        // Mercado = 100%, Lazer > 100%, demais com folga.
        PlanejamentoMensal pAgo = criarPlanejamento(usuario, 8, "2800.00", "20.00");
        adicionarLimite(pAgo, mercado, "390.00");     // 390 / 390 = 100%
        adicionarLimite(pAgo, transporte, "200.00"); // 180 / 200
        adicionarLimite(pAgo, lazer, "120.00");      // 150 / 120 = estourou
        adicionarLimite(pAgo, streaming, "100.00");  // 81,70 / 100
        adicionarLimite(pAgo, compras, "250.00");    // 210 / 250
        adicionarLimite(pAgo, celular, "80.00");     // 59,90 / 80
        adicionarLimite(pAgo, educacao, "700.00");   // 650 / 700
        adicionarLimite(pAgo, academia, "100.00");   // 99,90 / 100

        PlanejamentoMensal pSet = criarPlanejamento(usuario, 9, "2800.00", "20.00");
        adicionarLimite(pSet, mercado, "450.00");
        adicionarLimite(pSet, transporte, "220.00");
        adicionarLimite(pSet, lazer, "250.00");
        adicionarLimite(pSet, streaming, "100.00");
        adicionarLimite(pSet, compras, "300.00");
        adicionarLimite(pSet, celular, "80.00");
        adicionarLimite(pSet, educacao, "700.00");
        adicionarLimite(pSet, academia, "100.00");

        // =========================================================
        // OBJETIVOS
        // =========================================================

        // 1) Comprar um carro - ativo e com progresso gradual.
        Objetivo carro = criarObjetivo(
                usuario, "Comprar um carro", "20000.00",
                LocalDate.of(2027, 12, 31), "#16A34A", "BiCar"
        );
        movimentarObjetivo(carro, usuario, objetivos, resgateObjetivo,
                "500.00", LocalDate.of(2026, 3, 12), false);
        movimentarObjetivo(carro, usuario, objetivos, resgateObjetivo,
                "500.00", LocalDate.of(2026, 4, 12), false);
        movimentarObjetivo(carro, usuario, objetivos, resgateObjetivo,
                "600.00", LocalDate.of(2026, 5, 12), false);
        movimentarObjetivo(carro, usuario, objetivos, resgateObjetivo,
                "600.00", LocalDate.of(2026, 6, 12), false);
        movimentarObjetivo(carro, usuario, objetivos, resgateObjetivo,
                "700.00", LocalDate.of(2026, 7, 12), false);
        movimentarObjetivo(carro, usuario, objetivos, resgateObjetivo,
                "250.00", LocalDate.of(2026, 7, 25), true); // retirada
        movimentarObjetivo(carro, usuario, objetivos, resgateObjetivo,
                "800.00", LocalDate.of(2026, 8, 12), false);
        movimentarObjetivo(carro, usuario, objetivos, resgateObjetivo,
                "250.00", LocalDate.of(2026, 9, 2), false);

        // 2) Notebook novo - ativo.
        Objetivo notebook = criarObjetivo(
                usuario, "Notebook novo", "6000.00",
                LocalDate.of(2027, 6, 30), "#3B82F6", "BiLaptop"
        );
        movimentarObjetivo(notebook, usuario, objetivos, resgateObjetivo,
                "300.00", LocalDate.of(2026, 6, 20), false);
        movimentarObjetivo(notebook, usuario, objetivos, resgateObjetivo,
                "400.00", LocalDate.of(2026, 7, 20), false);
        movimentarObjetivo(notebook, usuario, objetivos, resgateObjetivo,
                "500.00", LocalDate.of(2026, 8, 20), false);

        // 3) Celular novo - objetivo finalizado para aparecer no histórico.
        Objetivo celularNovo = criarObjetivo(
                usuario, "Celular novo", "1800.00",
                LocalDate.of(2026, 6, 30), "#8B5CF6", "BiMobile"
        );
        movimentarObjetivo(celularNovo, usuario, objetivos, resgateObjetivo,
                "600.00", LocalDate.of(2026, 3, 22), false);
        movimentarObjetivo(celularNovo, usuario, objetivos, resgateObjetivo,
                "600.00", LocalDate.of(2026, 4, 22), false);
        movimentarObjetivo(celularNovo, usuario, objetivos, resgateObjetivo,
                "600.00", LocalDate.of(2026, 5, 22), false);
        concluirObjetivo(celularNovo, usuario, resgateObjetivo,
                "1800.00", LocalDate.of(2026, 5, 25));

        // =========================================================
        // INVESTIMENTOS
        // =========================================================

        // 1) CDB Nubank - ativo, com aportes, rendimentos e resgate parcial.
        Investimento cdb = criarInvestimento(
                usuario, "CDB Nubank", TipoAtivo.RENDA_FIXA,
                LocalDate.of(2026, 3, 1), false
        );
        movimentarInvestimento(cdb, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.APORTE, "300.00", LocalDateTime.of(2026, 3, 10, 10, 0));
        movimentarInvestimento(cdb, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.RENDIMENTO, "8.00", LocalDateTime.of(2026, 3, 31, 23, 0));

        movimentarInvestimento(cdb, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.APORTE, "300.00", LocalDateTime.of(2026, 4, 10, 10, 0));
        movimentarInvestimento(cdb, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.RENDIMENTO, "12.00", LocalDateTime.of(2026, 4, 30, 23, 0));

        movimentarInvestimento(cdb, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.APORTE, "350.00", LocalDateTime.of(2026, 5, 10, 10, 0));
        movimentarInvestimento(cdb, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.RENDIMENTO, "16.00", LocalDateTime.of(2026, 5, 31, 23, 0));

        movimentarInvestimento(cdb, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.APORTE, "350.00", LocalDateTime.of(2026, 6, 10, 10, 0));
        movimentarInvestimento(cdb, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.RENDIMENTO, "20.00", LocalDateTime.of(2026, 6, 30, 23, 0));

        movimentarInvestimento(cdb, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.APORTE, "400.00", LocalDateTime.of(2026, 7, 10, 10, 0));
        movimentarInvestimento(cdb, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.RENDIMENTO, "25.00", LocalDateTime.of(2026, 7, 31, 23, 0));

        movimentarInvestimento(cdb, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.APORTE, "450.00", LocalDateTime.of(2026, 8, 10, 10, 0));
        movimentarInvestimento(cdb, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.RENDIMENTO, "30.00", LocalDateTime.of(2026, 8, 31, 23, 0));
        movimentarInvestimento(cdb, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.RESGATE, "300.00", LocalDateTime.of(2026, 8, 28, 15, 0));

        // Setembro atual sem datas futuras.
        movimentarInvestimento(cdb, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.APORTE, "250.00", LocalDateTime.of(2026, 9, 2, 9, 0));

        // 2) Tesouro Selic - ativo.
        Investimento tesouro = criarInvestimento(
                usuario, "Tesouro Selic", TipoAtivo.RENDA_FIXA,
                LocalDate.of(2026, 5, 5), false
        );
        movimentarInvestimento(tesouro, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.APORTE, "200.00", LocalDateTime.of(2026, 5, 8, 11, 0));
        movimentarInvestimento(tesouro, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.RENDIMENTO, "2.00", LocalDateTime.of(2026, 5, 31, 23, 30));
        movimentarInvestimento(tesouro, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.RENDIMENTO, "4.00", LocalDateTime.of(2026, 6, 30, 23, 30));
        movimentarInvestimento(tesouro, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.APORTE, "250.00", LocalDateTime.of(2026, 7, 8, 11, 0));
        movimentarInvestimento(tesouro, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.RENDIMENTO, "6.00", LocalDateTime.of(2026, 7, 31, 23, 30));
        movimentarInvestimento(tesouro, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.APORTE, "250.00", LocalDateTime.of(2026, 8, 8, 11, 0));
        movimentarInvestimento(tesouro, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.RENDIMENTO, "8.00", LocalDateTime.of(2026, 8, 31, 23, 30));

        // 3) FII HGLG11 - encerrado, para demonstrar histórico de investimento.
        Investimento fii = criarInvestimento(
                usuario, "FII HGLG11", TipoAtivo.FII,
                LocalDate.of(2026, 4, 5), false
        );
        movimentarInvestimento(fii, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.APORTE, "300.00", LocalDateTime.of(2026, 4, 8, 14, 0));
        movimentarInvestimento(fii, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.RENDIMENTO, "5.00", LocalDateTime.of(2026, 4, 30, 22, 0));
        movimentarInvestimento(fii, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.RENDIMENTO, "7.00", LocalDateTime.of(2026, 5, 31, 22, 0));
        movimentarInvestimento(fii, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.RENDIMENTO, "10.00", LocalDateTime.of(2026, 6, 30, 22, 0));
        movimentarInvestimento(fii, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.RENDIMENTO, "15.00", LocalDateTime.of(2026, 7, 20, 22, 0));
        movimentarInvestimento(fii, usuario, investimentos, resgateInvestimento,
                TipoInvestimento.RESGATE, "337.00", LocalDateTime.of(2026, 7, 25, 16, 0));
        fii.setEncerrado(true);
        investimentoRepo.save(fii);

        System.out.println(">>> Cenário DEMO criado com sucesso!");
        System.out.println(">>> Usuário: " + DEMO_EMAIL + " | senha: " + DEMO_PASSWORD);
        System.out.println(">>> Para o print do Relatório, selecione AGOSTO/2026.");
    }

    // =====================================================================
    // HELPERS - CATEGORIAS
    // =====================================================================

    private Categoria criarCategoria(
            String nome,
            String icone,
            String cor,
            TipoTransacao tipo,
            boolean padraoSistema,
            boolean internaSistema,
            Usuario usuario
    ) {
        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        categoria.setIcone(icone);
        categoria.setCor(cor);
        categoria.setTipo(tipo);
        categoria.setPadraoSistema(padraoSistema);
        categoria.setInternaSistema(internaSistema);
        categoria.setUsuario(usuario);
        return categoriaRepo.save(categoria);
    }

    // =====================================================================
    // HELPERS - TRANSAÇÕES
    // =====================================================================

    private Transacao criarTransacao(
            Usuario usuario,
            Categoria categoria,
            String descricao,
            String valor,
            MetodoPagamento metodo,
            StatusPagamento status,
            LocalDate data,
            boolean bloqueadaSistema
    ) {
        Transacao transacao = new Transacao();
        transacao.setUsuario(usuario);
        transacao.setCategoria(categoria);
        transacao.setDescricao(descricao);
        transacao.setValor(bd(valor));
        transacao.setMetodoPagamento(metodo);
        transacao.setStatus(status);
        transacao.setData(data);
        transacao.setBloqueadaSistema(bloqueadaSistema);
        return transacaoRepo.save(transacao);
    }

    private void criarReceitaMensal(
            Usuario usuario,
            Categoria salario,
            int mes,
            String valor
    ) {
        criarTransacao(
                usuario, salario,
                "Salário - " + nomeMes(mes),
                valor,
                MetodoPagamento.TRANSFERENCIA,
                StatusPagamento.PAGO,
                LocalDate.of(2026, mes, 5),
                false
        );
    }

    private void criarDespesasMensais(
            Usuario usuario,
            int mes,
            Categoria educacao,
            Categoria mercado,
            Categoria transporte,
            Categoria streaming,
            Categoria celular,
            Categoria academia,
            Categoria lazer,
            Categoria compras,
            Categoria restaurantes,
            Categoria jogos,
            Categoria contas,
            String valorMercado,
            String valorTransporte,
            String valorLazer,
            String valorCompras,
            String valorRestaurante,
            String valorJogos
    ) {
        criarTransacao(usuario, educacao, "Mensalidade da faculdade", "650.00",
                MetodoPagamento.BOLETO, StatusPagamento.PAGO,
                LocalDate.of(2026, mes, 7), false);

        criarTransacao(usuario, mercado, "Compras do mês", valorMercado,
                MetodoPagamento.DEBITO, StatusPagamento.PAGO,
                LocalDate.of(2026, mes, 9), false);

        criarTransacao(usuario, transporte, "Combustível e transporte", valorTransporte,
                MetodoPagamento.PIX, StatusPagamento.PAGO,
                LocalDate.of(2026, mes, 11), false);

        criarTransacao(usuario, streaming, "Netflix", "39.90",
                MetodoPagamento.CARTAO_CREDITO, StatusPagamento.PAGO,
                LocalDate.of(2026, mes, 12), false);

        criarTransacao(usuario, streaming, "Spotify", "21.90",
                MetodoPagamento.CARTAO_CREDITO, StatusPagamento.PAGO,
                LocalDate.of(2026, mes, 12), false);

        criarTransacao(usuario, streaming, "Prime Video", "19.90",
                MetodoPagamento.CARTAO_CREDITO, StatusPagamento.PAGO,
                LocalDate.of(2026, mes, 12), false);

        criarTransacao(usuario, celular, "Plano de celular", "59.90",
                MetodoPagamento.PIX, StatusPagamento.PAGO,
                LocalDate.of(2026, mes, 13), false);

        criarTransacao(usuario, academia, "Mensalidade academia", "99.90",
                MetodoPagamento.DEBITO, StatusPagamento.PAGO,
                LocalDate.of(2026, mes, 14), false);

        criarTransacao(usuario, lazer, "Lazer do mês", valorLazer,
                MetodoPagamento.PIX, StatusPagamento.PAGO,
                LocalDate.of(2026, mes, 18), false);

        criarTransacao(usuario, compras, "Compras pessoais", valorCompras,
                MetodoPagamento.CARTAO_CREDITO, StatusPagamento.PAGO,
                LocalDate.of(2026, mes, 20), false);

        criarTransacao(usuario, restaurantes, "Restaurantes e lanches", valorRestaurante,
                MetodoPagamento.PIX, StatusPagamento.PAGO,
                LocalDate.of(2026, mes, 23), false);

        if (valorJogos != null) {
            criarTransacao(usuario, jogos, "Jogos e conteúdo digital", valorJogos,
                    MetodoPagamento.CARTAO_CREDITO, StatusPagamento.PAGO,
                    LocalDate.of(2026, mes, 25), false);
        }

        criarTransacao(usuario, contas, "Internet residencial", "99.90",
                MetodoPagamento.PIX, StatusPagamento.PAGO,
                LocalDate.of(2026, mes, 27), false);
    }

    // =====================================================================
    // HELPERS - PLANEJAMENTO
    // =====================================================================

    private PlanejamentoMensal criarPlanejamento(
            Usuario usuario,
            int mes,
            String rendaMensal,
            String percentualEconomia
    ) {
        BigDecimal renda = bd(rendaMensal);
        BigDecimal percentual = bd(percentualEconomia);

        BigDecimal metaEconomia = renda
                .multiply(percentual)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        PlanejamentoMensal planejamento = new PlanejamentoMensal();
        planejamento.setUsuario(usuario);
        planejamento.setReferencia(LocalDate.of(2026, mes, 1));
        planejamento.setRendaMensal(renda);
        planejamento.setPercentualEconomia(percentual);
        planejamento.setValorPlanejado(renda.subtract(metaEconomia));
        return planejamentoMensalRepo.save(planejamento);
    }

    private void adicionarLimite(
            PlanejamentoMensal planejamento,
            Categoria categoria,
            String limite
    ) {
        PlanejamentoCategoria item = new PlanejamentoCategoria();
        item.setPlanejamentoMensal(planejamento);
        item.setCategoria(categoria);
        item.setLimite(bd(limite));
        planejamentoCategoriaRepo.save(item);
    }

    // =====================================================================
    // HELPERS - OBJETIVOS
    // =====================================================================

    private Objetivo criarObjetivo(
            Usuario usuario,
            String nome,
            String valorObjetivo,
            LocalDate dataFinal,
            String cor,
            String icone
    ) {
        Objetivo objetivo = new Objetivo();
        objetivo.setUsuario(usuario);
        objetivo.setNome(nome);
        objetivo.setValorObjetivo(bd(valorObjetivo));
        objetivo.setDataFinal(dataFinal);
        objetivo.setCor(cor);
        objetivo.setIcone(icone);
        objetivo.setFinalizado(false);
        return objetivoRepo.save(objetivo);
    }

    private void movimentarObjetivo(
            Objetivo objetivo,
            Usuario usuario,
            Categoria categoriaDeposito,
            Categoria categoriaRetirada,
            String valor,
            LocalDate data,
            boolean retirada
    ) {
        BigDecimal valorAbs = bd(valor).abs();
        Categoria categoria = retirada ? categoriaRetirada : categoriaDeposito;

        String descricao = retirada
                ? "Retirada do objetivo: " + objetivo.getNome()
                : "Depósito no objetivo: " + objetivo.getNome();

        Transacao transacao = criarTransacao(
                usuario,
                categoria,
                descricao,
                valorAbs.toPlainString(),
                MetodoPagamento.TRANSFERENCIA,
                StatusPagamento.PAGO,
                data,
                true
        );

        ObjetivoDeposito deposito = new ObjetivoDeposito();
        deposito.setObjetivo(objetivo);
        deposito.setUsuario(usuario);
        deposito.setData(data);
        deposito.setValor(retirada ? valorAbs.negate() : valorAbs);
        deposito.setTransacao(transacao);
        objetivoDepositoRepo.save(deposito);
    }

    private void concluirObjetivo(
            Objetivo objetivo,
            Usuario usuario,
            Categoria resgateObjetivo,
            String valor,
            LocalDate data
    ) {
        criarTransacao(
                usuario,
                resgateObjetivo,
                "Objetivo concluído: " + objetivo.getNome(),
                valor,
                MetodoPagamento.TRANSFERENCIA,
                StatusPagamento.PAGO,
                data,
                true
        );

        objetivo.setFinalizado(true);
        objetivoRepo.save(objetivo);
    }

    // =====================================================================
    // HELPERS - INVESTIMENTOS
    // =====================================================================

    private Investimento criarInvestimento(
            Usuario usuario,
            String nome,
            TipoAtivo tipo,
            LocalDate dataCriacao,
            boolean encerrado
    ) {
        Investimento investimento = new Investimento();
        investimento.setUsuario(usuario);
        investimento.setNome(nome);
        investimento.setTipo(tipo);
        investimento.setDataCriacao(dataCriacao);
        investimento.setEncerrado(encerrado);
        return investimentoRepo.save(investimento);
    }

    private void movimentarInvestimento(
            Investimento investimento,
            Usuario usuario,
            Categoria investimentos,
            Categoria resgateInvestimento,
            TipoInvestimento tipo,
            String valor,
            LocalDateTime data
    ) {
        BigDecimal valorMov = bd(valor);
        Transacao transacao = null;

        if (tipo == TipoInvestimento.APORTE) {
            transacao = criarTransacao(
                    usuario,
                    investimentos,
                    "Aporte em investimento: " + investimento.getNome(),
                    valorMov.toPlainString(),
                    MetodoPagamento.TRANSFERENCIA,
                    StatusPagamento.PAGO,
                    data.toLocalDate(),
                    true
            );
        }

        if (tipo == TipoInvestimento.RESGATE) {
            transacao = criarTransacao(
                    usuario,
                    resgateInvestimento,
                    "Resgate de investimento: " + investimento.getNome(),
                    valorMov.toPlainString(),
                    MetodoPagamento.TRANSFERENCIA,
                    StatusPagamento.PAGO,
                    data.toLocalDate(),
                    true
            );
        }

        InvestimentoMovimentacao movimentacao = new InvestimentoMovimentacao();
        movimentacao.setInvestimento(investimento);
        movimentacao.setTipo(tipo);
        movimentacao.setValor(valorMov);
        movimentacao.setData(data);
        movimentacao.setTransacao(transacao);
        investimentoMovimentacaoRepo.save(movimentacao);
    }

    // =====================================================================
    // UTILITÁRIOS
    // =====================================================================

    private BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }

    private String nomeMes(int mes) {
        return switch (mes) {
            case 1 -> "Janeiro";
            case 2 -> "Fevereiro";
            case 3 -> "Março";
            case 4 -> "Abril";
            case 5 -> "Maio";
            case 6 -> "Junho";
            case 7 -> "Julho";
            case 8 -> "Agosto";
            case 9 -> "Setembro";
            case 10 -> "Outubro";
            case 11 -> "Novembro";
            case 12 -> "Dezembro";
            default -> "Mês";
        };
    }
}