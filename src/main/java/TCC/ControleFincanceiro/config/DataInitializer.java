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

    public DataInitializer(
            UsuarioRepository usuarioRepo,
            CategoriaRepository categoriaRepo,
            TransacaoRepository transacaoRepo,
            ObjetivoRepository objetivoRepo,
            ObjetivoDepositoRepository objetivoDepositoRepo,
            InvestimentoRepository investimentoRepo,
            InvestimentoMovimentacaoRepository investimentoMovimentacaoRepo,
            PlanejamentoMensalRepository planejamentoMensalRepo,
            PlanejamentoCategoriaRepository planejamentoCategoriaRepo
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
        alimentacao.setIcone("bi-basket");
        alimentacao.setCor("#FF6B6B");
        alimentacao.setTipo(TipoTransacao.DESPESA);
        alimentacao.setPadraoSistema(true);

        categoriaRepo.save(alimentacao);

        Categoria transporte = new Categoria();
        transporte.setNome("Transporte");
        transporte.setIcone("bi-car-front");
        transporte.setCor("#4D96FF");
        transporte.setTipo(TipoTransacao.DESPESA);
        transporte.setPadraoSistema(true);

        categoriaRepo.save(transporte);

        Categoria salario = new Categoria();
        salario.setNome("Salário");
        salario.setIcone("bi-wallet");
        salario.setCor("#00C897");
        salario.setTipo(TipoTransacao.RECEITA);
        salario.setPadraoSistema(true);

        categoriaRepo.save(salario);

        Categoria investimentos = new Categoria();
        investimentos.setNome("Investimentos");
        investimentos.setIcone("bi-graph-up");
        investimentos.setCor("#8338EC");
        investimentos.setTipo(TipoTransacao.DESPESA);
        investimentos.setPadraoSistema(true);

        categoriaRepo.save(investimentos);

        Categoria resgateInvestimento = new Categoria();
        resgateInvestimento.setNome("Resgate de Investimento");
        resgateInvestimento.setIcone("bi-cash");
        resgateInvestimento.setCor("#00C897");
        resgateInvestimento.setTipo(TipoTransacao.RECEITA);
        resgateInvestimento.setPadraoSistema(true);

        categoriaRepo.save(resgateInvestimento);

        Categoria objetivos = new Categoria();
        objetivos.setNome("Objetivos");
        objetivos.setTipo(TipoTransacao.DESPESA);
        objetivos.setPadraoSistema(true);

        categoriaRepo.save(objetivos);

        Categoria resgateObjetivo = new Categoria();
        resgateObjetivo.setNome("Resgate de Objetivo");
        resgateObjetivo.setTipo(TipoTransacao.RECEITA);
        resgateObjetivo.setPadraoSistema(true);

        categoriaRepo.save(resgateObjetivo);

        Categoria academia = new Categoria();
        academia.setNome("Academia");
        academia.setIcone("bi-heart-pulse");
        academia.setCor("#F72585");
        academia.setTipo(TipoTransacao.DESPESA);
        academia.setPadraoSistema(false);
        academia.setUsuario(usuario);

        categoriaRepo.save(academia);

        // =====================================
        // TRANSAÇÕES
        // =====================================

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

        transacaoRepo.save(mercadoTx);

        Transacao academiaTx = new Transacao();
        academiaTx.setDescricao("Academia");
        academiaTx.setValor(BigDecimal.valueOf(120));
        academiaTx.setMetodoPagamento(MetodoPagamento.DEBITO);
        academiaTx.setStatus(StatusPagamento.PAGO);
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
        depTx1.setDescricao("Depósito no objetivo: Viagem Europa");
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
        depTx2.setDescricao("Depósito no objetivo: Viagem Europa");
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

        Investimento investimento = new Investimento();
        investimento.setNome("CDB Nubank");
        investimento.setTaxaAtual(BigDecimal.valueOf(1));
        investimento.setTipo(TipoAtivo.RENDA_FIXA);
        investimento.setUsuario(usuario);

        investimentoRepo.save(investimento);

        // =====================================
        // APORTE
        // =====================================

        Transacao aporteTx = new Transacao();
        aporteTx.setDescricao("Aporte em investimento: CDB Nubank");
        aporteTx.setValor(BigDecimal.valueOf(1000));
        aporteTx.setMetodoPagamento(MetodoPagamento.TRANSFERENCIA);
        aporteTx.setStatus(StatusPagamento.PAGO);
        aporteTx.setData(LocalDate.of(2026, 1, 1));
        aporteTx.setCategoria(investimentos);
        aporteTx.setUsuario(usuario);

        transacaoRepo.save(aporteTx);

        InvestimentoMovimentacao mov1 = new InvestimentoMovimentacao();
        mov1.setValor(BigDecimal.valueOf(1000));
        mov1.setData(LocalDate.of(2026, 1, 1));
        mov1.setTipo(TipoInvestimento.APORTE);
        mov1.setInvestimento(investimento);
        mov1.setTransacao(aporteTx);

        investimentoMovimentacaoRepo.save(mov1);

        // =====================================
        // RENDIMENTO
        // =====================================

        Transacao rendimentoTx = new Transacao();
        rendimentoTx.setDescricao("Rendimento investimento: CDB Nubank");
        rendimentoTx.setValor(BigDecimal.valueOf(10));
        rendimentoTx.setMetodoPagamento(MetodoPagamento.TRANSFERENCIA);
        rendimentoTx.setStatus(StatusPagamento.PAGO);
        rendimentoTx.setData(LocalDate.of(2026, 2, 1));
        rendimentoTx.setCategoria(investimentos);
        rendimentoTx.setUsuario(usuario);

        transacaoRepo.save(rendimentoTx);

        InvestimentoMovimentacao mov2 = new InvestimentoMovimentacao();
        mov2.setValor(BigDecimal.valueOf(10));
        mov2.setData(LocalDate.of(2026, 2, 1));
        mov2.setTipo(TipoInvestimento.RENDIMENTO);
        mov2.setInvestimento(investimento);
        mov2.setTransacao(rendimentoTx);

        investimentoMovimentacaoRepo.save(mov2);

        // =====================================
        // PLANEJAMENTO
        // =====================================

        PlanejamentoMensal planejamento = new PlanejamentoMensal();
        planejamento.setReferencia(LocalDate.of(2026, 2, 1));
        planejamento.setRendaMensal(BigDecimal.valueOf(3000));
        planejamento.setPercentualEconomia(BigDecimal.valueOf(20));
        planejamento.setUsuario(usuario);

        planejamentoMensalRepo.save(planejamento);

        PlanejamentoCategoria pc = new PlanejamentoCategoria();
        pc.setLimite(BigDecimal.valueOf(600));
        pc.setPlanejamentoMensal(planejamento);
        pc.setCategoria(alimentacao);

        planejamentoCategoriaRepo.save(pc);

        System.out.println(">>> Dados iniciais populados com sucesso!");
    }
}