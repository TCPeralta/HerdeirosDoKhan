package model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes unitários das classes {@link TabuleiroJogador} e {@link ColunaJogador}.
 * Colunas: 0 = China, 1 = Rússia, 2 = Pérsia, 3 = branca.
 */
public class TabuleiroJogadorTest {
	private static final int TIMEOUT = 2000;

	private TabuleiroJogador tab;

	@Before
	public void setUp() {
		tab = new TabuleiroJogador(Herdeiro.TOLUI);
	}

	/** O tabuleiro tem 3 colunas coloridas e a branca. */
	@Test(timeout = TIMEOUT)
	public void testTabuleiroTemQuatroColunas() {
		assertEquals("Deveria haver 4 colunas", 4, tab.getColunas().size());
	}

	/** A coluna branca não pertence a nenhuma região. */
	@Test(timeout = TIMEOUT)
	public void testColunaBrancaNaoTemCor() {
		assertNull("A coluna branca não tem cor", tab.getColunaBranca().getCor());
	}

	/** Com melhorias em só 2 das 3 colunas coloridas, nenhuma linha está completa. */
	@Test(timeout = TIMEOUT)
	public void testLinhaIncompletaComDuasColunas() {
		preencher(Regiao.CHINA, 1);
		preencher(Regiao.RUSSIA, 1);
		assertEquals("Faltou a coluna da Pérsia", 0, tab.linhasCompletas());
	}

	/** Uma melhoria em cada coluna colorida completa a 1a linha. */
	@Test(timeout = TIMEOUT)
	public void testLinhaCompletaComUmaMelhoriaEmCadaCor() {
		preencherLinha();
		assertEquals("A 1a linha deveria estar completa", 1, tab.linhasCompletas());
	}

	/** Uma linha completa só dá direito a uma melhoria gratuita uma vez. */
	@Test(timeout = TIMEOUT)
	public void testLinhaCompletaSoEhPremiadaUmaVez() {
		preencherLinha();
		tab.registrarNovasLinhasCompletas();
		assertEquals("A mesma linha não pode ser premiada de novo", 0, tab.registrarNovasLinhasCompletas());
	}

	/** Jochi já tem o 1o espaço da coluna branca ocupado: a 1a linha não dá melhoria. */
	@Test(timeout = TIMEOUT)
	public void testJochiNaoGanhaMelhoriaPelaPrimeiraLinha() {
		tab = new TabuleiroJogador(Herdeiro.JOCHI);
		preencherLinha();
		assertEquals("Jochi não ganha melhoria pela 1a linha", 0, tab.registrarNovasLinhasCompletas());
	}

	/** A coluna branca recupera todas as peças de ativação. */
	@Test(timeout = TIMEOUT)
	public void testRecuperarTodasLiberaAsColunas() {
		tab.getColuna(0).ativar();
		tab.getColuna(3).ativar();
		tab.recuperarTodas();
		assertFalse("A coluna 0 deveria estar livre", tab.getColuna(0).isAtivada());
	}

	/** Uma coluna tem só 4 espaços: a 5a melhoria é um erro. */
	@Test(timeout = TIMEOUT, expected = IllegalStateException.class)
	public void testQuintaMelhoriaNaColunaLancaExcecao() {
		preencher(Regiao.CHINA, 5);
	}

	/** Uma coluna com 4 melhorias não pode receber outra da mesma cor. */
	@Test(timeout = TIMEOUT)
	public void testColunaCheiaNaoPodeReceberMelhoria() {
		preencher(Regiao.CHINA, 4);
		assertFalse("Coluna da China cheia", tab.podeReceber(new Melhoria(TipoMelhoria.MOEDA_VIRTUAL, Regiao.CHINA)));
	}

	private void preencher(Regiao r, int quantidade) {
		for (int i = 0; i < quantidade; i++)
			tab.getColunaDaRegiao(r).adicionarMelhoria(new Melhoria(TipoMelhoria.MOEDA_VIRTUAL, r));
	}

	private void preencherLinha() {
		preencher(Regiao.CHINA, 1);
		preencher(Regiao.RUSSIA, 1);
		preencher(Regiao.PERSIA, 1);
	}
}
