package model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes da ativação de colunas e da passagem de turno em {@link Jogo}
 * (manual, págs. 6 e 9). AZUL joga, depois VERMELHO.
 */
public class JogoColunasTest {
	private static final int TIMEOUT = 2000;

	private Jogo jogo;

	@Before
	public void setUp() {
		jogo = MapaTeste.novaPartida();
	}

	/** Ativar uma coluna libera as ações da base dela. */
	@Test(timeout = TIMEOUT)
	public void testAtivarColunaLiberaMovimentos() {
		jogo.ativarColuna(1);
		assertEquals("A coluna 1 dá 2 movimentos", 2, jogo.getTurno().getMovimentos());
	}

	/** Só se ativa uma coluna por turno. */
	@Test(timeout = TIMEOUT)
	public void testSoUmaColunaPorTurno() {
		jogo.ativarColuna(0);
		assertFalse("Já foi ativada uma coluna neste turno", jogo.ativarColuna(1));
	}

	/** O turno não termina sem ativar uma coluna. */
	@Test(timeout = TIMEOUT)
	public void testTurnoNaoTerminaSemAtivarColuna() {
		assertFalse("Nenhuma coluna foi ativada", jogo.encerrarTurno());
	}

	/** Ao encerrar o turno, a vez passa ao próximo jogador. */
	@Test(timeout = TIMEOUT)
	public void testEncerrarTurnoPassaAVez() {
		jogo.ativarColuna(0);
		jogo.encerrarTurno();
		assertEquals("Vez do VERMELHO", CorJogador.VERMELHO, jogo.getJogadorAtual().getCor());
	}

	/** Uma coluna com peça de ativação não pode ser usada de novo. */
	@Test(timeout = TIMEOUT)
	public void testColunaJaAtivadaNaoPodeSerUsadaDeNovo() {
		jogarTurno(0); // AZUL
		jogarTurno(0); // VERMELHO
		assertFalse("A coluna 0 de AZUL está ocupada", jogo.ativarColuna(0));
	}

	/** A coluna branca recupera todas as peças de ativação. */
	@Test(timeout = TIMEOUT)
	public void testColunaBrancaRecuperaAsColunas() {
		jogarTurno(0);             // AZUL usa a coluna 0
		jogarTurno(0);             // VERMELHO
		jogo.ativarColuna(3);      // AZUL usa a branca (tem Khan)
		jogo.moverKhan("KHAN2");
		jogo.encerrarTurno();
		jogarTurno(1);             // VERMELHO
		assertTrue("A coluna 0 de AZUL voltou a ficar livre", jogo.ativarColuna(0));
	}

	/** Coluna inexistente é recusada. */
	@Test(timeout = TIMEOUT)
	public void testAtivarColunaInexistenteFalha() {
		assertFalse("Só existem as colunas 0 a 3", jogo.ativarColuna(4));
	}

	private void jogarTurno(int coluna) {
		jogo.ativarColuna(coluna);
		jogo.encerrarTurno();
	}
}
