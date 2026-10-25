package model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/** Testes da preparação da partida feita pelo construtor de {@link Jogo} (manual, págs. 3 e 4). */
public class JogoPreparacaoTest {
	private static final int TIMEOUT = 2000;

	private Jogo jogo;

	@Before
	public void setUp() {
		jogo = MapaTeste.novaPartida(CorJogador.AZUL, CorJogador.VERMELHO, CorJogador.VERDE);
	}

	/** O 1o jogador começa com 1 moeda. */
	@Test(timeout = TIMEOUT)
	public void testPrimeiroJogadorComecaComUmaMoeda() {
		assertEquals("Moedas do 1o jogador", 1, jogo.getJogador(CorJogador.AZUL).getTributos(TipoTributo.MOEDA));
	}

	/** Do 3o jogador em diante, cada um começa com 2 moedas. */
	@Test(timeout = TIMEOUT)
	public void testTerceiroJogadorComecaComDuasMoedas() {
		assertEquals("Moedas do 3o jogador", 2, jogo.getJogador(CorJogador.VERDE).getTributos(TipoTributo.MOEDA));
	}

	/** Todos os peões começam em Karakorum. */
	@Test(timeout = TIMEOUT)
	public void testPeoesComecamEmKarakorum() {
		assertTrue("O peão deveria estar em Karakorum", jogo.getJogador(CorJogador.VERDE).getPosicao().isKarakorum());
	}

	/** Cada jogador começa com 12 yurts. */
	@Test(timeout = TIMEOUT)
	public void testJogadorComecaComDozeYurts() {
		assertEquals("Yurts iniciais", 12, jogo.getJogador(CorJogador.AZUL).getYurtsNaReserva());
	}

	/** São reveladas 3 cidades. */
	@Test(timeout = TIMEOUT)
	public void testTresCidadesSaoReveladas() {
		assertEquals("Cidades disponíveis para ataque", 3, jogo.getCidadesDisponiveis().size());
	}

	/** Cada cidade revelada recebe 4 tesouros. */
	@Test(timeout = TIMEOUT)
	public void testCidadeReveladaTemQuatroTesouros() {
		assertEquals("Tesouros na cidade", 4, jogo.getCidadesDisponiveis().get(0).getTesouros().size());
	}

	/** 40 tesouros - 3 cidades x 4 = 28 tesouros ficam na pilha. */
	@Test(timeout = TIMEOUT)
	public void testSobramVinteEOitoTesourosNaPilha() {
		assertEquals("Tesouros restantes na pilha", 28, jogo.getTesourosNaPilha());
	}

	/** Cada província começa com 1 tributo. */
	@Test(timeout = TIMEOUT)
	public void testProvinciaComecaComUmTributo() {
		assertEquals("Tributos iniciais em PA", 1, jogo.getTabuleiro().getProvincia("PA").getTributos());
	}

	/** Cada jogador recebe 2 cartas de conselheiro secreto para escolher. */
	@Test(timeout = TIMEOUT)
	public void testJogadorRecebeDuasCartasSecretas() {
		assertEquals("Cartas secretas recebidas", 2,
				jogo.getJogador(CorJogador.AZUL).getCartasSecretasRecebidas().size());
	}

	/** Depois de escolher, o jogador fica com 1 carta secreta. */
	@Test(timeout = TIMEOUT)
	public void testEscolherCartaSecretaGuardaAEscolhida() {
		jogo.escolherCartaSecreta(CorJogador.AZUL, 1);
		assertNotNull("O jogador deveria ter uma carta secreta", jogo.getJogador(CorJogador.AZUL).getCartaSecreta());
	}

	/** Cada conselheiro recebe uma peça de bônus. */
	@Test(timeout = TIMEOUT)
	public void testConselheiroRecebePecaDeBonus() {
		assertNotNull("O conselheiro de D deveria ter bônus", jogo.getTabuleiro().getParada("D").getConselheiro().getBonus());
	}

	/** O Khan começa fora do tabuleiro. */
	@Test(timeout = TIMEOUT)
	public void testKhanComecaForaDoTabuleiro() {
		assertNull("O Khan ainda não foi colocado", jogo.getPosicaoKhan());
	}

	/** A partida começa pelo 1o jogador da lista. */
	@Test(timeout = TIMEOUT)
	public void testPartidaComecaPeloPrimeiroJogador() {
		assertEquals("Jogador da vez", CorJogador.AZUL, jogo.getJogadorAtual().getCor());
	}

	/** O jogo é para 2 a 5 jogadores: 1 jogador é recusado. */
	@Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
	public void testPartidaComUmJogadorLancaExcecao() {
		MapaTeste.novaPartida(CorJogador.AZUL);
	}

	/** Dois jogadores não podem ter a mesma cor. */
	@Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
	public void testCoresRepetidasLancaExcecao() {
		MapaTeste.novaPartida(CorJogador.AZUL, CorJogador.AZUL);
	}
}
