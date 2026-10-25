package model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes da ação MOVER de {@link Jogo} (manual, pág. 7).
 * Mapa: KARAKORUM - A - B - D(dupla) - E - KARAKORUM. AZUL joga primeiro.
 * Coluna 0 dá 1 movimento; coluna 1 dá 2 movimentos.
 */
public class JogoMovimentoTest {
	private static final int TIMEOUT = 2000;

	private Jogo jogo;
	private Jogador azul;
	private Jogador vermelho;

	@Before
	public void setUp() {
		jogo = MapaTeste.novaPartida();
		azul = jogo.getJogador(CorJogador.AZUL);
		vermelho = jogo.getJogador(CorJogador.VERMELHO);
	}

	/** O peão vai para uma parada vizinha. */
	@Test(timeout = TIMEOUT)
	public void testMoverParaParadaVizinhaMudaPosicao() {
		jogo.ativarColuna(0);
		jogo.mover(List.of("A"));
		assertEquals("AZUL deveria estar em A", "A", azul.getPosicao().getId());
	}

	/** Cada parada do caminho gasta 1 movimento. */
	@Test(timeout = TIMEOUT)
	public void testCadaParadaGastaUmMovimento() {
		jogo.ativarColuna(1);
		jogo.mover(List.of("A", "B"));
		assertEquals("2 paradas gastam os 2 movimentos", 0, jogo.getTurno().getMovimentos());
	}

	/** As paradas por onde o peão passou contam como visitadas no turno. */
	@Test(timeout = TIMEOUT)
	public void testParadaIntermediariaFicaVisitada() {
		jogo.ativarColuna(1);
		jogo.mover(List.of("A", "B"));
		assertTrue("A deveria constar como visitada", jogo.getTurno().visitou("A"));
	}

	/** Não se pode ir para uma parada que não é ligada por rota. */
	@Test(timeout = TIMEOUT)
	public void testMoverParaParadaNaoVizinhaFalha() {
		jogo.ativarColuna(1);
		assertFalse("Karakorum não liga com B", jogo.mover(List.of("B")));
	}

	/** Um movimento recusado não muda a posição do peão. */
	@Test(timeout = TIMEOUT)
	public void testMovimentoRecusadoNaoMoveOPeao() {
		jogo.ativarColuna(1);
		jogo.mover(List.of("B"));
		assertEquals("AZUL deveria continuar em Karakorum", "KARAKORUM", azul.getPosicao().getId());
	}

	/** Não se pode terminar o movimento numa parada simples ocupada. */
	@Test(timeout = TIMEOUT)
	public void testNaoPodeTerminarEmParadaSimplesOcupada() {
		vermelho.setPosicao(jogo.getTabuleiro().getParada("A"));
		jogo.ativarColuna(1);
		assertFalse("A está ocupada por VERMELHO", jogo.mover(List.of("A")));
	}

	/** Pode-se passar por uma parada simples ocupada. */
	@Test(timeout = TIMEOUT)
	public void testPodePassarPorParadaSimplesOcupada() {
		vermelho.setPosicao(jogo.getTabuleiro().getParada("A"));
		jogo.ativarColuna(1);
		assertTrue("Passar por A ocupada é permitido", jogo.mover(List.of("A", "B")));
	}

	/** Uma parada dupla comporta 2 peões. */
	@Test(timeout = TIMEOUT)
	public void testParadaDuplaComportaDoisPeoes() {
		vermelho.setPosicao(jogo.getTabuleiro().getParada("D"));
		azul.setPosicao(jogo.getTabuleiro().getParada("B"));
		jogo.ativarColuna(0);
		assertTrue("D é dupla e tem vaga", jogo.mover(List.of("D")));
	}

	/** Karakorum comporta todos os peões. */
	@Test(timeout = TIMEOUT)
	public void testKarakorumComportaTodosOsPeoes() {
		azul.setPosicao(jogo.getTabuleiro().getParada("A"));
		jogo.ativarColuna(0);
		assertTrue("Karakorum aceita qualquer número de peões", jogo.mover(List.of("KARAKORUM")));
	}

	/** Pular o próprio yurt não gasta movimento. */
	@Test(timeout = TIMEOUT)
	public void testPularProprioYurtNaoGastaMovimento() {
		jogo.getTabuleiro().getParada("A").adicionarYurt(CorJogador.AZUL);
		jogo.ativarColuna(0);
		assertTrue("Com 1 movimento, AZUL chega a B pulando seu yurt em A", jogo.mover(List.of("A", "B")));
	}

	/** A parada pulada não conta como visitada. */
	@Test(timeout = TIMEOUT)
	public void testParadaPuladaNaoContaComoVisitada() {
		jogo.getTabuleiro().getParada("A").adicionarYurt(CorJogador.AZUL);
		jogo.ativarColuna(0);
		jogo.mover(List.of("A", "B"));
		assertFalse("A foi pulada", jogo.getTurno().visitou("A"));
	}

	/** O yurt de outro jogador não pode ser pulado. */
	@Test(timeout = TIMEOUT)
	public void testYurtDeOutroJogadorNaoPodeSerPulado() {
		jogo.getTabuleiro().getParada("A").adicionarYurt(CorJogador.VERMELHO);
		jogo.ativarColuna(0);
		assertFalse("Com 1 movimento não dá para chegar a B", jogo.mover(List.of("A", "B")));
	}

	/** Não se pode gastar mais movimentos do que se tem. */
	@Test(timeout = TIMEOUT)
	public void testMovimentosInsuficientesFalha() {
		jogo.ativarColuna(0);
		assertFalse("1 movimento não basta para 2 paradas", jogo.mover(List.of("A", "B")));
	}

	/** Um caminho vazio é recusado. */
	@Test(timeout = TIMEOUT)
	public void testCaminhoVazioFalha() {
		jogo.ativarColuna(0);
		assertFalse("Caminho vazio", jogo.mover(List.of()));
	}

	/** Um movimento recusado informa o motivo. */
	@Test(timeout = TIMEOUT)
	public void testMovimentoRecusadoInformaMotivo() {
		jogo.ativarColuna(0);
		jogo.mover(List.of("B"));
		assertNotNull("Deveria haver mensagem de erro", jogo.getUltimoErro());
	}
}
