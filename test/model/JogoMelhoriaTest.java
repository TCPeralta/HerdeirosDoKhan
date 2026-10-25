package model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes da ação extra COMPRAR MELHORIAS de {@link Jogo} (manual, págs. 11, 12 e 16).
 * AZUL começa com 1 moeda. Colunas: 0 = China, 1 = Rússia (2 movimentos), 2 = Pérsia, 3 = branca.
 */
public class JogoMelhoriaTest {
	private static final int TIMEOUT = 2000;

	private Jogo jogo;
	private Jogador azul;
	private TabuleiroJogador tab;

	@Before
	public void setUp() {
		jogo = MapaTeste.novaPartida();
		azul = jogo.getJogador(CorJogador.AZUL);
		tab = azul.getTabuleiro();
	}

	/** Comprar uma melhoria custa 2 moedas. */
	@Test(timeout = TIMEOUT)
	public void testComprarMelhoriaCustaDuasMoedas() {
		azul.adicionarTributo(TipoTributo.MOEDA, 1);
		jogo.comprarMelhoria(4);
		assertEquals("As 2 moedas foram gastas", 0, azul.getTributos(TipoTributo.MOEDA));
	}

	/** A melhoria comprada vai para a coluna da sua cor. */
	@Test(timeout = TIMEOUT)
	public void testMelhoriaVaiParaColunaDaSuaCor() {
		azul.adicionarTributo(TipoTributo.MOEDA, 1);
		Melhoria m = jogo.getMercado().verEspaco(4);
		jogo.comprarMelhoria(4);
		assertTrue("A melhoria deveria estar na coluna " + m.getCor(),
				tab.getColunaDaRegiao(m.getCor()).getMelhorias().contains(m));
	}

	/** Pode-se comprar a melhoria do topo da pilha (espaço -1). */
	@Test(timeout = TIMEOUT)
	public void testComprarDoTopoDaPilha() {
		azul.adicionarTributo(TipoTributo.MOEDA, 1);
		assertTrue("Compra do topo da pilha", jogo.comprarMelhoria(-1));
	}

	/** Sem 2 moedas não se compra. */
	@Test(timeout = TIMEOUT)
	public void testComprarSemMoedasSuficientesFalha() {
		assertFalse("AZUL só tem 1 moeda", jogo.comprarMelhoria(0));
	}

	/** Não se compra melhoria cuja coluna está cheia. */
	@Test(timeout = TIMEOUT)
	public void testComprarComColunaCheiaFalha() {
		Melhoria m = jogo.getMercado().verEspaco(4);
		preencher(m.getCor(), 4);
		azul.adicionarTributo(TipoTributo.MOEDA, 1);
		assertFalse("A coluna " + m.getCor() + " está cheia", jogo.comprarMelhoria(4));
	}

	/** A moeda virtual de uma melhoria da coluna ativada ajuda a pagar. */
	@Test(timeout = TIMEOUT)
	public void testMoedaVirtualAjudaAPagar() {
		Melhoria moeda = new Melhoria(TipoMelhoria.MOEDA_VIRTUAL, Regiao.CHINA);
		moeda.setTurnoCompra(0);
		tab.getColunaDaRegiao(Regiao.CHINA).adicionarMelhoria(moeda);
		jogo.ativarColuna(0);
		assertTrue("1 moeda real + 1 virtual = 2", jogo.comprarMelhoria(4));
	}

	/** Uma melhoria comprada em turno anterior vale ao ativar a coluna. */
	@Test(timeout = TIMEOUT)
	public void testMelhoriaDeTurnoAnteriorVale() {
		adicionarMovimentoDuplo(0);
		jogo.ativarColuna(1);
		assertEquals("2 da base + 2 da melhoria", 4, jogo.getTurno().getMovimentos());
	}

	/** Uma melhoria comprada neste turno não vale neste turno. */
	@Test(timeout = TIMEOUT)
	public void testMelhoriaNaoValeNoTurnoDaCompra() {
		adicionarMovimentoDuplo(jogo.getNumeroTurno());
		jogo.ativarColuna(1);
		assertEquals("Só os 2 movimentos da base", 2, jogo.getTurno().getMovimentos());
	}

	/** Completar uma linha dá direito a 1 melhoria gratuita. */
	@Test(timeout = TIMEOUT)
	public void testCompletarLinhaDaMelhoriaGratuita() {
		comprarCompletandoALinha();
		assertEquals("1 melhoria gratuita pendente", 1, jogo.getTurno().getMelhoriasGratuitasPendentes());
	}

	/** O turno não termina enquanto a melhoria gratuita não for escolhida. */
	@Test(timeout = TIMEOUT)
	public void testTurnoNaoTerminaComMelhoriaGratuitaPendente() {
		comprarCompletandoALinha();
		assertFalse("Falta escolher a melhoria gratuita", jogo.encerrarTurno());
	}

	/** A melhoria gratuita vai para a coluna branca. */
	@Test(timeout = TIMEOUT)
	public void testMelhoriaGratuitaVaiParaColunaBranca() {
		comprarCompletandoALinha();
		jogo.pegarMelhoriaGratuita(-1);
		assertEquals("A coluna branca recebeu 1 melhoria", 1, tab.getColunaBranca().getQuantidadeMelhorias());
	}

	/** Sem linha completa, não há melhoria gratuita. */
	@Test(timeout = TIMEOUT)
	public void testPegarMelhoriaGratuitaSemDireitoFalha() {
		assertFalse("Nenhuma linha foi completada", jogo.pegarMelhoriaGratuita(-1));
	}

	// ------------------------- auxiliares -------------------------

	private void preencher(Regiao r, int quantidade) {
		for (int i = 0; i < quantidade; i++)
			tab.getColunaDaRegiao(r).adicionarMelhoria(new Melhoria(TipoMelhoria.MOEDA_VIRTUAL, r));
	}

	private void adicionarMovimentoDuplo(int turnoDaCompra) {
		Melhoria m = new Melhoria(TipoMelhoria.MOVIMENTO_DUPLO, Regiao.RUSSIA);
		m.setTurnoCompra(turnoDaCompra);
		tab.getColunaDaRegiao(Regiao.RUSSIA).adicionarMelhoria(m);
	}

	/** Deixa 1 melhoria nas 2 colunas de cor diferente da do espaço 4 e compra a do espaço 4. */
	private void comprarCompletandoALinha() {
		Regiao corComprada = jogo.getMercado().verEspaco(4).getCor();
		for (Regiao r : Regiao.values())
			if (r != corComprada) preencher(r, 1);
		azul.adicionarTributo(TipoTributo.MOEDA, 1);
		jogo.ativarColuna(0);
		jogo.comprarMelhoria(4);
	}
}
