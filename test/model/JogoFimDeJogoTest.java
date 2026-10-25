package model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes do fim do jogo e da contagem final de votos em {@link Jogo}
 * (manual, págs. 14 a 16). Partida de 2 jogadores: o fim é disparado aos 10 votos.
 */
public class JogoFimDeJogoTest {
	private static final int TIMEOUT = 2000;

	private Jogo jogo;
	private Jogador azul;
	private Jogador vermelho;
	private Tabuleiro mapa;

	@Before
	public void setUp() {
		jogo = MapaTeste.novaPartida();
		azul = jogo.getJogador(CorJogador.AZUL);
		vermelho = jogo.getJogador(CorJogador.VERMELHO);
		mapa = jogo.getTabuleiro();
	}

	// ----------------------- disparo e rodada final -----------------------

	/** A ficha neutra chegando a 10 dispara o fim. */
	@Test(timeout = TIMEOUT)
	public void testDezVotosDisparamOFim() {
		jogo.ganharVotos(azul, 10);
		assertTrue("O fim deveria ter sido disparado", jogo.isFimDisparado());
	}

	/** Disparado o fim, a rodada atual termina e começa a rodada final. */
	@Test(timeout = TIMEOUT)
	public void testDepoisDaRodadaAtualComecaARodadaFinal() {
		jogo.ganharVotos(azul, 10);
		jogarTurno(0); // AZUL
		jogarTurno(0); // VERMELHO fecha a rodada
		assertTrue("Deveria estar na rodada final", jogo.isRodadaFinal());
	}

	/** Durante a rodada final a partida ainda não terminou. */
	@Test(timeout = TIMEOUT)
	public void testPartidaNaoTerminaNoMeioDaRodadaFinal() {
		jogo.ganharVotos(azul, 10);
		jogarTurno(0);
		jogarTurno(0);
		jogarTurno(1); // AZUL na rodada final
		assertFalse("VERMELHO ainda joga a rodada final", jogo.isTerminado());
	}

	/** Depois da rodada final, a partida termina. */
	@Test(timeout = TIMEOUT)
	public void testPartidaTerminaAposARodadaFinal() {
		terminarPartida();
		assertTrue("A partida deveria ter terminado", jogo.isTerminado());
	}

	/** Terminada a partida, nenhuma ação é aceita. */
	@Test(timeout = TIMEOUT)
	public void testNenhumaAcaoAposOFim() {
		terminarPartida();
		assertFalse("Não há mais jogadas", jogo.ativarColuna(2));
	}

	// ----------------------- influência nas regiões -----------------------

	/** Rússia: AZUL com mais yurts e 0 melhorias na coluna ganha 1 voto. */
	@Test(timeout = TIMEOUT)
	public void testMaisYurtsSemMelhoriasDaUmVoto() {
		mapa.getParada("A").adicionarYurt(CorJogador.AZUL);
		assertEquals("Tabela 'mais' com 0 melhorias", 1, jogo.votosDeInfluencia(azul));
	}

	/** Rússia: AZUL em 1o com 4 melhorias na coluna da Rússia ganha 3 votos. */
	@Test(timeout = TIMEOUT)
	public void testMaisYurtsComQuatroMelhoriasDaTresVotos() {
		mapa.getParada("A").adicionarYurt(CorJogador.AZUL);
		adicionarMelhorias(azul, Regiao.RUSSIA, 4);
		assertEquals("Tabela 'mais' com 4 melhorias", 3, jogo.votosDeInfluencia(azul));
	}

	/** Rússia: VERMELHO em 2o com 2 melhorias na coluna ganha 1 voto. */
	@Test(timeout = TIMEOUT)
	public void testSegundoLugarComDuasMelhoriasDaUmVoto() {
		mapa.getParada("A").adicionarYurt(CorJogador.AZUL);
		mapa.getCidade("C1").conquistar(CorJogador.AZUL);
		mapa.getParada("B").adicionarYurt(CorJogador.VERMELHO);
		adicionarMelhorias(vermelho, Regiao.RUSSIA, 2);
		assertEquals("Tabela '2o mais' com 2 melhorias", 1, jogo.votosDeInfluencia(vermelho));
	}

	/** Empate no 1o lugar: os empatados recebem os votos de 1o lugar. */
	@Test(timeout = TIMEOUT)
	public void testEmpateNoPrimeiroLugarDaVotosDePrimeiro() {
		mapa.getParada("D").adicionarYurt(CorJogador.AZUL);
		mapa.getParada("D").adicionarYurt(CorJogador.VERMELHO);
		adicionarMelhorias(vermelho, Regiao.CHINA, 3);
		assertEquals("Tabela 'mais' com 3 melhorias", 2, jogo.votosDeInfluencia(vermelho));
	}

	/** Sem yurts numa região, o jogador não ganha votos nela. */
	@Test(timeout = TIMEOUT)
	public void testSemYurtsNaoGanhaInfluencia() {
		adicionarMelhorias(azul, Regiao.RUSSIA, 4);
		assertEquals("Sem yurts, sem votos", 0, jogo.votosDeInfluencia(azul));
	}

	// ----------------------- votos finais e vencedor -----------------------

	/** Os votos finais somam trilha + carta secreta (sem yurts, sem influência). */
	@Test(timeout = TIMEOUT)
	public void testVotosFinaisSomamTrilhaECartaSecreta() {
		azul.receberCartaSecreta(FabricaCartas.cartaSecreta("Soldado"));
		azul.escolherCartaSecreta(2);                 // as 2 primeiras vieram na preparação
		adicionarMelhorias(azul, Regiao.CHINA, 4);   // Soldado: 4 melhorias = 1 voto
		jogo.ganharVotos(azul, 3);
		assertEquals("3 da trilha + 1 do Soldado", 4, (int) jogo.calcularVotosFinais().get(CorJogador.AZUL));
	}

	/** Vence quem tem mais votos. */
	@Test(timeout = TIMEOUT)
	public void testVenceQuemTemMaisVotos() {
		jogo.ganharVotos(azul, 5);
		jogo.ganharVotos(vermelho, 4);
		assertEquals("AZUL tem mais votos", List.of(CorJogador.AZUL), jogo.vencedores());
	}

	/** Empate em votos: vence quem tem mais tesouros não entregues. */
	@Test(timeout = TIMEOUT)
	public void testDesempatePorTesourosNaoEntregues() {
		jogo.ganharVotos(azul, 4);
		jogo.ganharVotos(vermelho, 4);
		vermelho.adicionarTesouro(TipoTesouro.SEDA);
		assertEquals("VERMELHO tem mais tesouros", List.of(CorJogador.VERMELHO), jogo.vencedores());
	}

	/** Empate em votos e tesouros: vence o peão mais próximo de Karakorum. */
	@Test(timeout = TIMEOUT)
	public void testDesempatePorDistanciaAKarakorum() {
		azul.setPosicao(mapa.getParada("B"));      // distância 2
		vermelho.setPosicao(mapa.getParada("A"));  // distância 1
		assertEquals("VERMELHO está mais perto", List.of(CorJogador.VERMELHO), jogo.vencedores());
	}

	/** Empate total: os empatados compartilham a vitória. */
	@Test(timeout = TIMEOUT)
	public void testEmpateTotalTodosVencem() {
		assertEquals("Ambos em Karakorum, sem votos nem tesouros", 2, jogo.vencedores().size());
	}

	// ------------------------- auxiliares -------------------------

	private void jogarTurno(int coluna) {
		jogo.ativarColuna(coluna);
		jogo.encerrarTurno();
	}

	/** Dispara o fim e joga a rodada atual e a rodada final. */
	private void terminarPartida() {
		jogo.ganharVotos(azul, 10);
		jogarTurno(0);
		jogarTurno(0);
		jogarTurno(1);
		jogarTurno(1);
	}

	private static void adicionarMelhorias(Jogador j, Regiao r, int quantidade) {
		for (int i = 0; i < quantidade; i++)
			j.getTabuleiro().getColunaDaRegiao(r).adicionarMelhoria(new Melhoria(TipoMelhoria.YURT_VIRTUAL, r));
	}
}
