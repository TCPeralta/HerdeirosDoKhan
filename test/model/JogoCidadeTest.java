package model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes da ação extra ATACAR CIDADES de {@link Jogo} (manual, pág. 10).
 * Cenário: AZUL ativa a coluna 0 e vai até A, que é vizinha de todas as
 * cidades; o alvo é a 1a cidade disponível (4 tesouros).
 */
public class JogoCidadeTest {
	private static final int TIMEOUT = 2000;

	private Jogo jogo;
	private Jogador azul;
	private Cidade alvo;

	@Before
	public void setUp() {
		jogo = MapaTeste.novaPartida();
		azul = jogo.getJogador(CorJogador.AZUL);
		alvo = jogo.getCidadesDisponiveis().get(0);
		jogo.ativarColuna(0);
		jogo.mover(List.of("A"));
	}

	/** O 1o tesouro de uma cidade no turno custa 1 espada. */
	@Test(timeout = TIMEOUT)
	public void testPrimeiroTesouroCustaUmaEspada() {
		azul.adicionarTributo(TipoTributo.ESPADA, 3);
		atacar();
		assertEquals("Sobram 2 espadas", 2, azul.getTributos(TipoTributo.ESPADA));
	}

	/** O 2o tesouro da mesma cidade, no mesmo turno, custa 2 espadas. */
	@Test(timeout = TIMEOUT)
	public void testSegundoTesouroDaMesmaCidadeCustaDuasEspadas() {
		azul.adicionarTributo(TipoTributo.ESPADA, 3);
		atacar();
		atacar();
		assertEquals("1 + 2 = 3 espadas gastas", 0, azul.getTributos(TipoTributo.ESPADA));
	}

	/** O tesouro tomado vai para o jogador. */
	@Test(timeout = TIMEOUT)
	public void testTesouroTomadoVaiParaOJogador() {
		azul.adicionarTributo(TipoTributo.ESPADA, 1);
		atacar();
		assertEquals("AZUL deveria ter 1 tesouro", 1, azul.getTotalTesouros());
	}

	/** O tesouro tomado sai da cidade. */
	@Test(timeout = TIMEOUT)
	public void testTesouroTomadoSaiDaCidade() {
		azul.adicionarTributo(TipoTributo.ESPADA, 1);
		atacar();
		assertEquals("Restam 3 tesouros na cidade", 3, alvo.getTesouros().size());
	}

	/** Sem espadas não se ataca. */
	@Test(timeout = TIMEOUT)
	public void testAtaqueSemEspadasFalha() {
		assertFalse("AZUL não tem espadas", atacar());
	}

	/** Com 1 espada sobrando, não dá para tomar o 2o tesouro (custa 2). */
	@Test(timeout = TIMEOUT)
	public void testSegundoAtaqueSemEspadasSuficientesFalha() {
		azul.adicionarTributo(TipoTributo.ESPADA, 2);
		atacar();
		assertFalse("O 2o tesouro custa 2 e só sobrou 1", atacar());
	}

	/** Não se ataca cidade longe das paradas visitadas. */
	@Test(timeout = TIMEOUT)
	public void testAtacarCidadeNaoAdjacenteFalha() {
		jogo.encerrarTurno();                       // vez do VERMELHO, em Karakorum
		jogo.ativarColuna(0);
		jogo.getJogadorAtual().adicionarTributo(TipoTributo.ESPADA, 3);
		assertFalse("Karakorum não é vizinha de nenhuma cidade", atacar());
	}

	/** A espada virtual (melhoria) é gasta antes da espada real. */
	@Test(timeout = TIMEOUT)
	public void testEspadaVirtualEhGastaAntesDaReal() {
		azul.adicionarTributo(TipoTributo.ESPADA, 1);
		jogo.getTurno().adicionarVirtual(TipoTributo.ESPADA, 1);
		atacar();
		assertEquals("A espada real deveria ser preservada", 1, azul.getTributos(TipoTributo.ESPADA));
	}

	/** Quem toma o último tesouro conquista a cidade. */
	@Test(timeout = TIMEOUT)
	public void testTomarUltimoTesouroConquistaACidade() {
		tomarTodosOsTesouros();
		assertEquals("AZUL conquistou a cidade", CorJogador.AZUL, alvo.getConquistador());
	}

	/** A conquista usa 1 yurt da reserva do jogador. */
	@Test(timeout = TIMEOUT)
	public void testConquistaGastaUmYurtDaReserva() {
		tomarTodosOsTesouros();
		assertEquals("12 - 1 yurts", 11, azul.getYurtsNaReserva());
	}

	/** A cidade conquistada deixa de estar disponível. */
	@Test(timeout = TIMEOUT)
	public void testCidadeConquistadaSaiDasDisponiveis() {
		tomarTodosOsTesouros();
		assertFalse("A cidade conquistada não pode mais ser atacada", jogo.getCidadesDisponiveis().contains(alvo));
	}

	/** Uma nova cidade é revelada no lugar da conquistada. */
	@Test(timeout = TIMEOUT)
	public void testConquistaRevelaNovaCidade() {
		tomarTodosOsTesouros();
		assertEquals("Continuam 3 cidades disponíveis", 3, jogo.getCidadesDisponiveis().size());
	}

	// ------------------------- auxiliares -------------------------

	private boolean atacar() {
		return jogo.atacarCidade(alvo.getNome(), alvo.getTesouros().get(0));
	}

	/** 4 tesouros custam 1 + 2 + 2 + 2 = 7 espadas. */
	private void tomarTodosOsTesouros() {
		azul.adicionarTributo(TipoTributo.ESPADA, 7);
		for (TipoTesouro t : new ArrayList<>(alvo.getTesouros())) jogo.atacarCidade(alvo.getNome(), t);
	}
}
