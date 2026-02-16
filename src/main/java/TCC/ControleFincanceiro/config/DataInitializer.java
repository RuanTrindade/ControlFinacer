package TCC.ControleFincanceiro.config;

import TCC.ControleFincanceiro.entity.*;
import TCC.ControleFincanceiro.entity.enumerated.investimento.TipoInvestimento;
import TCC.ControleFincanceiro.entity.enumerated.transacao.MetodoPagamento;
import TCC.ControleFincanceiro.entity.enumerated.transacao.StatusPagamento;
import TCC.ControleFincanceiro.entity.enumerated.transacao.TipoTransacao;
import TCC.ControleFincanceiro.entity.enumerated.usuario.PlanoUsuario;
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

    public DataInitializer(UsuarioRepository usuarioRepo,
                           CategoriaRepository categoriaRepo,
                           TransacaoRepository transacaoRepo,
                           ObjetivoRepository objetivoRepo,
                           ObjetivoDepositoRepository objetivoDepositoRepo,
                           InvestimentoRepository investimentoRepo,
                           InvestimentoMovimentacaoRepository investimentoMovimentacaoRepo,
                           PlanejamentoMensalRepository planejamentoMensalRepo,
                           PlanejamentoCategoriaRepository planejamentoCategoriaRepo) {
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
    public void run(String... args) throws Exception {

        // --- USUÁRIO ---
        Usuario usuario = new Usuario();
        usuario.setNome("Ruan Teste");
        usuario.setEmail("ruan@email.com");
        usuario.setSenha("123456");
        usuario.setPlano(PlanoUsuario.valueOf("START"));
        usuario.setDataCadastro(LocalDateTime.now());
        usuarioRepo.save(usuario);

        // --- CATEGORIAS ---
        Categoria cat1 = new Categoria();
        cat1.setNome("Alimentação");
        cat1.setIcone("bi-basket");
        cat1.setCor("#FF6B6B");
        cat1.setTipo(TipoTransacao.valueOf("DESPESA"));
        cat1.setPadraoSistema(true);
        cat1.setUsuario(null); // categoria de sistema
        categoriaRepo.save(cat1);

        Categoria cat2 = new Categoria();
        cat2.setNome("Transporte");
        cat2.setIcone("bi-car-front");
        cat2.setCor("#4D96FF");
        cat2.setTipo(TipoTransacao.valueOf("DESPESA"));
        cat2.setPadraoSistema(true);
        cat2.setUsuario(null);
        categoriaRepo.save(cat2);

        Categoria cat3 = new Categoria();
        cat3.setNome("Salário");
        cat3.setIcone("bi-wallet");
        cat3.setCor("#00C897");
        cat3.setTipo(TipoTransacao.valueOf("RECEITA"));
        cat3.setPadraoSistema(true);
        cat3.setUsuario(null);
        categoriaRepo.save(cat3);

        Categoria cat4 = new Categoria();
        cat4.setNome("Investimentos");
        cat4.setIcone("bi-graph-up");
        cat4.setCor("#8338EC");
        cat4.setTipo(TipoTransacao.valueOf("DESPESA"));
        cat4.setPadraoSistema(true);
        cat4.setUsuario(null);
        categoriaRepo.save(cat4);

        Categoria cat5 = new Categoria();
        cat5.setNome("Academia");
        cat5.setIcone("bi-heart-pulse");
        cat5.setCor("#F72585");
        cat5.setTipo(TipoTransacao.valueOf("DESPESA"));
        cat5.setPadraoSistema(false);
        cat5.setUsuario(usuario); // categoria personalizada do usuário
        categoriaRepo.save(cat5);

        // --- TRANSAÇÕES ---
        Transacao t1 = new Transacao();
        t1.setDescricao("Salário Mensal");
        t1.setValor(BigDecimal.valueOf(3000.00));
        t1.setMetodoPagamento(MetodoPagamento.valueOf("PIX"));
        t1.setStatus(StatusPagamento.valueOf("PAGO"));
        t1.setData(LocalDate.of(2026, 2, 1));
        t1.setCategoria(cat3);
        t1.setUsuario(usuario);
        transacaoRepo.save(t1);

        Transacao t2 = new Transacao();
        t2.setDescricao("Mercado");
        t2.setValor(BigDecimal.valueOf(450.00));
        t2.setMetodoPagamento(MetodoPagamento.valueOf("CARTAO_CREDITO"));
        t2.setStatus(StatusPagamento.valueOf("PAGO"));
        t2.setData(LocalDate.of(2026, 2, 5));
        t2.setCategoria(cat1);
        t2.setUsuario(usuario);
        transacaoRepo.save(t2);

        Transacao t3 = new Transacao();
        t3.setDescricao("Academia");
        t3.setValor(BigDecimal.valueOf(120.00));
        t3.setMetodoPagamento(MetodoPagamento.valueOf("DEBITO"));
        t3.setStatus(StatusPagamento.valueOf("PAGO"));
        t3.setData(LocalDate.of(2026, 2, 10));
        t3.setCategoria(cat5);
        t3.setUsuario(usuario);
        transacaoRepo.save(t3);

        // --- OBJETIVO ---
        Objetivo objetivo = new Objetivo();
        objetivo.setNome("Viagem Europa");
        objetivo.setValorObjetivo(BigDecimal.valueOf(10000.00));
        objetivo.setDataFinal(LocalDate.of(2026, 12, 31));
        objetivo.setCor("#3A86FF");
        objetivo.setIcone("bi-airplane");
        objetivo.setUsuario(usuario);
        objetivoRepo.save(objetivo);

        // --- OBJETIVO DEPOSITO ---
        ObjetivoDeposito dep1 = new ObjetivoDeposito();
        dep1.setValor(BigDecimal.valueOf(1000.00));
        dep1.setData(LocalDate.of(2026, 1, 10));
        dep1.setObjetivo(objetivo);
        dep1.setUsuario(usuario);
        objetivoDepositoRepo.save(dep1);

        ObjetivoDeposito dep2 = new ObjetivoDeposito();
        dep2.setValor(BigDecimal.valueOf(500.00));
        dep2.setData(LocalDate.of(2026, 2, 10));
        dep2.setObjetivo(objetivo);
        dep2.setUsuario(usuario);
        objetivoDepositoRepo.save(dep2);

        // --- INVESTIMENTO ---
        Investimento invest = new Investimento();
        invest.setNome("CDB Nubank");
        invest.setTaxaAtual(BigDecimal.valueOf(1.00));
        invest.setTipo("RENDA_FIXA");
        invest.setUsuario(usuario);
        investimentoRepo.save(invest);

        // --- INVESTIMENTO MOVIMENTAÇÕES ---
        InvestimentoMovimentacao mov1 = new InvestimentoMovimentacao();
        mov1.setValor(BigDecimal.valueOf(1000.00));
        mov1.setData(LocalDate.of(2026, 1, 1));
        mov1.setTipo(TipoInvestimento.valueOf("APORTE"));
        mov1.setInvestimento(invest);
        investimentoMovimentacaoRepo.save(mov1);

        InvestimentoMovimentacao mov2 = new InvestimentoMovimentacao();
        mov2.setValor(BigDecimal.valueOf(10.00));
        mov2.setData(LocalDate.of(2026, 2, 1));
        mov2.setTipo(TipoInvestimento.valueOf("RENDIMENTO"));
        mov2.setInvestimento(invest);
        investimentoMovimentacaoRepo.save(mov2);

        // --- PLANEJAMENTO MENSAL ---
        PlanejamentoMensal pm = new PlanejamentoMensal();
        pm.setReferencia(LocalDate.of(2026, 2, 1));
        pm.setRendaMensal(BigDecimal.valueOf(3000.00));
        pm.setPercentualEconomia(BigDecimal.valueOf(20.00));
        pm.setUsuario(usuario);
        planejamentoMensalRepo.save(pm);

        // --- PLANEJAMENTO CATEGORIA ---
        PlanejamentoCategoria pc = new PlanejamentoCategoria();
        pc.setLimite(BigDecimal.valueOf(600.00));
        pc.setPlanejamentoMensal(pm);
        pc.setCategoria(cat1);
        planejamentoCategoriaRepo.save(pc);

        System.out.println(">>> Dados iniciais populados no H2 com sucesso!");
    }
}