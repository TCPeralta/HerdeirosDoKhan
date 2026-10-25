package model;

import static org.junit.Assert.*;

import org.junit.Test;

/** Testes unitários da classe {@link TrilhaKurultai} (manual, pág. 14). */
public class TrilhaKurultaiTest {
	private static final int TIMEOUT = 2000;

	/** Com 2 jogadores o fim é disparado aos 10 votos. */
	@Test(timeout = TIMEOUT)
	public void testLimiteParaDoisJogadoresEhDez() {
		assertEquals("Limite para 2 jogadores", 10, TrilhaKurultai.limiteParaJogadores(2));
	}

	/** Com 3 jogadores o fim é disparado aos 14 votos. */
	@Test(timeout = TIMEOUT)
	public void testLimiteParaTresJogadoresEhQuatorze() {
		assertEquals("Limite para 3 jogadores", 14, TrilhaKurultai.limiteParaJogadores(3));
	}

	/** Com 5 jogadores o fim é disparado aos 17 votos. */
	@Test(timeout = TIMEOUT)
	public void testLimiteParaCincoJogadoresEhDezessete() {
		assertEquals("Limite para 5 jogadores", 17, TrilhaKurultai.limiteParaJogadores(5));
	}

	/** O jogo não aceita 6 jogadores. */
	@Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
	public void testSeisJogadoresLancaExcecao() {
		new TrilhaKurultai(6);
	}

	/** A ficha neutra avança com os votos de todos os jogadores. */
	@Test(timeout = TIMEOUT)
	public void testFichaNeutraSomaVotosDeTodosOsJogadores() {
		TrilhaKurultai trilha = new TrilhaKurultai(3);
		trilha.adicionarVotos(CorJogador.AZUL, 2);
		trilha.adicionarVotos(CorJogador.VERMELHO, 3);
		assertEquals("Ficha neutra = 2 + 3", 5, trilha.getFichaNeutra());
	}

	/** Os votos de cada jogador são contados separadamente. */
	@Test(timeout = TIMEOUT)
	public void testVotosDoJogadorSaoIndividuais() {
		TrilhaKurultai trilha = new TrilhaKurultai(3);
		trilha.adicionarVotos(CorJogador.AZUL, 2);
		trilha.adicionarVotos(CorJogador.VERMELHO, 3);
		assertEquals("AZUL deveria ter só os seus 2 votos", 2, trilha.getVotos(CorJogador.AZUL));
	}

	/** Chegar ao limite dispara o fim do jogo. */
	@Test(timeout = TIMEOUT)
	public void testChegarAoLimiteDisparaFim() {
		TrilhaKurultai trilha = new TrilhaKurultai(2);
		assertTrue("10 votos com 2 jogadores deveriam disparar o fim", trilha.adicionarVotos(CorJogador.AZUL, 10));
	}

	/** Um voto abaixo do limite não dispara o fim. */
	@Test(timeout = TIMEOUT)
	public void testAbaixoDoLimiteNaoDisparaFim() {
		TrilhaKurultai trilha = new TrilhaKurultai(2);
		assertFalse("9 votos com 2 jogadores não deveriam disparar o fim", trilha.adicionarVotos(CorJogador.AZUL, 9));
	}
}
