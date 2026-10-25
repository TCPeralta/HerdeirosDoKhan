package model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes das cartas de conselheiro aberto e secreto criadas pela
 * {@link FabricaCartas} (manual, págs. 18 e 19).
 */
public class FabricaCartasTest {
	private static final int TIMEOUT = 2000;

	private Jogo jogo;
	private Jogador azul;

	@Before
	public void setUp() {
		jogo = MapaTeste.novaPartida();
		azul = jogo.getJogador(CorJogador.AZUL);
	}

	/** O jogo tem 14 cartas de conselheiro aberto. */
	@Test(timeout = TIMEOUT)
	public void testExistemQuatorzeCartasAbertas() {
		assertEquals("Número de cartas abertas", 14, FabricaCartas.cartasAbertas().size());
	}

	/** O jogo tem 10 cartas de conselheiro secreto. */
	@Test(timeout = TIMEOUT)
	public void testExistemDezCartasSecretas() {
		assertEquals("Número de cartas secretas", 10, FabricaCartas.cartasSecretas().size());
	}

	/** "Ter 4 espadas" é cumprida por quem tem 4 espadas. */
	@Test(timeout = TIMEOUT)
	public void testCartaQuatroEspadasCumpridaComQuatro() {
		azul.adicionarTributo(TipoTributo.ESPADA, 4);
		CartaConselheiroAberto carta = FabricaCartas.cartaAberta("Ter 4 espadas");
		assertTrue("4 espadas cumprem a carta", carta.cumprida(azul, jogo));
	}

	/** "Ter 4 espadas" não é cumprida com 3. */
	@Test(timeout = TIMEOUT)
	public void testCartaQuatroEspadasNaoCumpridaComTres() {
		azul.adicionarTributo(TipoTributo.ESPADA, 3);
		CartaConselheiroAberto carta = FabricaCartas.cartaAberta("Ter 4 espadas");
		assertFalse("3 espadas não cumprem a carta", carta.cumprida(azul, jogo));
	}

	/** "1 yurt em cada região" não é cumprida com yurts em só 2 regiões. */
	@Test(timeout = TIMEOUT)
	public void testCartaYurtEmCadaRegiaoFalhaComDuasRegioes() {
		jogo.getTabuleiro().getParada("A").adicionarYurt(CorJogador.AZUL); // Rússia
		jogo.getTabuleiro().getParada("E").adicionarYurt(CorJogador.AZUL); // Pérsia
		CartaConselheiroAberto carta = FabricaCartas.cartaAberta("Ter 1 yurt em cada região");
		assertFalse("Falta yurt na China", carta.cumprida(azul, jogo));
	}

	/** Soldado (4-7-10 melhorias): 7 melhorias valem 2 votos. */
	@Test(timeout = TIMEOUT)
	public void testSoldadoComSeteMelhoriasDaDoisVotos() {
		adicionarMelhorias(Regiao.CHINA, TipoMelhoria.YURT_VIRTUAL, 4);
		adicionarMelhorias(Regiao.PERSIA, TipoMelhoria.YURT_VIRTUAL, 3);
		CartaConselheiroSecreto soldado = FabricaCartas.cartaSecreta("Soldado");
		assertEquals("7 melhorias = 2 patamares", 2, soldado.votos(azul, jogo));
	}

	/** Soldado: abaixo do 1o patamar não dá votos. */
	@Test(timeout = TIMEOUT)
	public void testSoldadoComTresMelhoriasNaoDaVotos() {
		adicionarMelhorias(Regiao.CHINA, TipoMelhoria.YURT_VIRTUAL, 3);
		CartaConselheiroSecreto soldado = FabricaCartas.cartaSecreta("Soldado");
		assertEquals("3 melhorias não atingem o 1o patamar", 0, soldado.votos(azul, jogo));
	}

	/** Sacerdote conta pares de símbolos iguais: 4 iguais = 2 pares. */
	@Test(timeout = TIMEOUT)
	public void testSacerdoteContaParesDeMelhoriasIguais() {
		adicionarMelhorias(Regiao.CHINA, TipoMelhoria.MOEDA_VIRTUAL, 4);
		CartaConselheiroSecreto sacerdote = FabricaCartas.cartaSecreta("Sacerdote");
		assertEquals("4 moedas virtuais = 2 pares", 2, sacerdote.medir(azul, jogo));
	}

	/** Colecionador: tesouros virtuais de tipos diferentes contam como símbolos diferentes. */
	@Test(timeout = TIMEOUT)
	public void testColecionadorDiferenciaTesourosVirtuais() {
		ColunaJogador china = azul.getTabuleiro().getColunaDaRegiao(Regiao.CHINA);
		china.adicionarMelhoria(new Melhoria(TipoMelhoria.TESOURO_VIRTUAL, Regiao.CHINA, TipoTesouro.PELE));
		china.adicionarMelhoria(new Melhoria(TipoMelhoria.TESOURO_VIRTUAL, Regiao.CHINA, TipoTesouro.FERRO));
		CartaConselheiroSecreto colecionador = FabricaCartas.cartaSecreta("Colecionador");
		assertEquals("Pele virtual e ferro virtual são 2 símbolos", 2, colecionador.medir(azul, jogo));
	}

	/** Cavaleiro conta só melhorias com cavalo ou punho. */
	@Test(timeout = TIMEOUT)
	public void testCavaleiroContaSoCavaloEPunho() {
		adicionarMelhorias(Regiao.CHINA, TipoMelhoria.MOVIMENTO_DUPLO, 1);
		adicionarMelhorias(Regiao.RUSSIA, TipoMelhoria.PEGAR_TRIBUTO, 1);
		adicionarMelhorias(Regiao.PERSIA, TipoMelhoria.MOEDA_VIRTUAL, 1);
		CartaConselheiroSecreto cavaleiro = FabricaCartas.cartaSecreta("Cavaleiro");
		assertEquals("Só cavalo e punho contam", 2, cavaleiro.medir(azul, jogo));
	}

	private void adicionarMelhorias(Regiao r, TipoMelhoria tipo, int quantidade) {
		for (int i = 0; i < quantidade; i++)
			azul.getTabuleiro().getColunaDaRegiao(r).adicionarMelhoria(new Melhoria(tipo, r));
	}
}
