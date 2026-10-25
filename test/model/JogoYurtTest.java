package model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes da ação extra CONSTRUIR YURTS de {@link Jogo} (manual, pág. 11).
 * Cenário: AZUL ativa a coluna 0 e vai de Karakorum até A.
 */
public class JogoYurtTest {
	private static final int TIMEOUT = 2000;

	private Jogo jogo;
	private Jogador azul;

	@Before
	public void setUp() {
		jogo = MapaTeste.novaPartida();
		azul = jogo.getJogador(CorJogador.AZUL);
		jogo.ativarColuna(0);
		jogo.mover(List.of("A"));
	}

	/** Constrói-se o yurt numa parada visitada no turno. */
	@Test(timeout = TIMEOUT)
	public void testConstruirYurtEmParadaVisitada() {
		azul.adicionarTributo(TipoTributo.YURT, 1);
		jogo.construirYurt("A");
		assertTrue("A deveria ter um yurt de AZUL", jogo.getTabuleiro().getParada("A").temYurtDe(CorJogador.AZUL));
	}

	/** Construir gasta 1 tributo de yurt. */
	@Test(timeout = TIMEOUT)
	public void testConstruirYurtGastaTributo() {
		azul.adicionarTributo(TipoTributo.YURT, 1);
		jogo.construirYurt("A");
		assertEquals("O tributo de yurt foi gasto", 0, azul.getTributos(TipoTributo.YURT));
	}

	/** Construir gasta 1 peça de yurt da reserva. */
	@Test(timeout = TIMEOUT)
	public void testConstruirYurtGastaPecaDaReserva() {
		azul.adicionarTributo(TipoTributo.YURT, 1);
		jogo.construirYurt("A");
		assertEquals("12 - 1 yurts", 11, azul.getYurtsNaReserva());
	}

	/** Uma parada simples só comporta 1 yurt. */
	@Test(timeout = TIMEOUT)
	public void testParadaSimplesNaoAceitaSegundoYurt() {
		azul.adicionarTributo(TipoTributo.YURT, 2);
		jogo.construirYurt("A");
		assertFalse("A já tem um yurt", jogo.construirYurt("A"));
	}

	/** Karakorum não aceita yurts. */
	@Test(timeout = TIMEOUT)
	public void testKarakorumNaoAceitaYurt() {
		azul.adicionarTributo(TipoTributo.YURT, 1);
		assertFalse("Karakorum não comporta yurts", jogo.construirYurt("KARAKORUM"));
	}

	/** Não se constrói em parada não visitada no turno. */
	@Test(timeout = TIMEOUT)
	public void testConstruirYurtEmParadaNaoVisitadaFalha() {
		azul.adicionarTributo(TipoTributo.YURT, 1);
		assertFalse("AZUL não passou por B", jogo.construirYurt("B"));
	}

	/** Sem tributo de yurt não se constrói. */
	@Test(timeout = TIMEOUT)
	public void testConstruirYurtSemTributoFalha() {
		assertFalse("AZUL não tem tributo de yurt", jogo.construirYurt("A"));
	}

	/** Pode-se construir numa parada onde está o peão de outro jogador. */
	@Test(timeout = TIMEOUT)
	public void testPodeConstruirOndeEstaOPeaoDeOutroJogador() {
		azul.adicionarTributo(TipoTributo.YURT, 1);
		jogo.getJogador(CorJogador.VERMELHO).setPosicao(jogo.getTabuleiro().getParada("B"));
		jogo.getTurno().adicionarMovimentos(2);
		jogo.mover(List.of("B", "D"));   // passa por B, onde está VERMELHO
		assertTrue("B foi visitada; o peão de VERMELHO não impede", jogo.construirYurt("B"));
	}
}
