package model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes da ação PEGAR TRIBUTO de {@link Jogo} (manual, pág. 8).
 * Províncias: PA (espada) junto de A; PB (moeda) junto de B; KHAN2 (espada)
 * junto de Karakorum. Cada uma começa com 1 tributo.
 */
public class JogoTributoTest {
	private static final int TIMEOUT = 2000;

	private Jogo jogo;
	private Jogador azul;

	@Before
	public void setUp() {
		jogo = MapaTeste.novaPartida();
		azul = jogo.getJogador(CorJogador.AZUL);
	}

	/** Pode-se pegar tributo de província junto de uma parada por onde se passou. */
	@Test(timeout = TIMEOUT)
	public void testPegarTributoJuntoDeParadaVisitadaDaOTributo() {
		jogo.ativarColuna(1);
		jogo.mover(List.of("A", "B"));
		jogo.pegarTributo("PA");
		assertEquals("AZUL deveria ter 1 espada", 1, azul.getTributos(TipoTributo.ESPADA));
	}

	/** O tributo pego sai da província. */
	@Test(timeout = TIMEOUT)
	public void testPegarTributoEsvaziaProvincia() {
		jogo.ativarColuna(1);
		jogo.mover(List.of("A"));
		jogo.pegarTributo("PA");
		assertEquals("PA deveria ficar vazia", 0, jogo.getTabuleiro().getProvincia("PA").getTributos());
	}

	/** A parada onde o turno começou também conta ("antes do movimento"). */
	@Test(timeout = TIMEOUT)
	public void testParadaInicialPermitePegarTributo() {
		jogo.ativarColuna(0);
		assertTrue("KHAN2 é vizinha de Karakorum", jogo.pegarTributo("KHAN2"));
	}

	/** Não se pode pegar tributo de província longe das paradas visitadas. */
	@Test(timeout = TIMEOUT)
	public void testPegarTributoDeProvinciaNaoAdjacenteFalha() {
		jogo.ativarColuna(0);
		assertFalse("PB só é vizinha de B", jogo.pegarTributo("PB"));
	}

	/** Sem ação de tributo disponível, a ação é recusada. */
	@Test(timeout = TIMEOUT)
	public void testPegarTributoSemAcaoDisponivelFalha() {
		jogo.ativarColuna(2); // movimento + Khan, sem punho
		assertFalse("A coluna 2 não dá ação de tributo", jogo.pegarTributo("KHAN2"));
	}

	/** Não se pode pegar tributo de uma província vazia. */
	@Test(timeout = TIMEOUT)
	public void testPegarTributoDeProvinciaVaziaFalha() {
		jogo.ativarColuna(0);
		jogo.getTurno().adicionarAcoesTributo(1);
		jogo.pegarTributo("KHAN2");
		assertFalse("KHAN2 ficou vazia", jogo.pegarTributo("KHAN2"));
	}

	/** Com 2 ações, pode-se pegar 2 tributos da mesma província. */
	@Test(timeout = TIMEOUT)
	public void testPodePegarMaisDeUmTributoDaMesmaProvincia() {
		jogo.getTabuleiro().getProvincia("KHAN2").adicionarTributo();
		jogo.ativarColuna(0);
		jogo.getTurno().adicionarAcoesTributo(1);
		jogo.pegarTributo("KHAN2");
		jogo.pegarTributo("KHAN2");
		assertEquals("AZUL deveria ter 2 espadas", 2, azul.getTributos(TipoTributo.ESPADA));
	}
}
