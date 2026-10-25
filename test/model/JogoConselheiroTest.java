package model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes de entrega de tesouros aos conselheiros, peças de bônus e cartas de
 * conselheiro aberto em {@link Jogo} (manual, págs. 12, 13, 17 e 18).
 * Cenário: o conselheiro da parada dupla D pede 2 PELES e guarda o bônus
 * "2 espadas". AZUL tem 2 peles e vai de B até D.
 */
public class JogoConselheiroTest {
	private static final int TIMEOUT = 2000;
	private static final List<TipoTesouro> DUAS_PELES = List.of(TipoTesouro.PELE, TipoTesouro.PELE);

	private Jogo jogo;
	private Jogador azul;
	private Conselheiro conselheiroD;

	@Before
	public void setUp() {
		jogo = MapaTeste.novaPartida();
		azul = jogo.getJogador(CorJogador.AZUL);
		conselheiroD = jogo.getTabuleiro().getParada("D").getConselheiro();
		conselheiroD.setBonus(new PecaBonus(TipoBonus.DUAS_ESPADAS));
		irAteD(azul);
	}

	/** Uma entrega válida dá 1 voto. */
	@Test(timeout = TIMEOUT)
	public void testEntregaDaUmVoto() {
		azul.adicionarTesouro(TipoTesouro.PELE);
		azul.adicionarTesouro(TipoTesouro.PELE);
		jogo.entregarTesouros("D", DUAS_PELES);
		assertEquals("AZUL deveria ter 1 voto", 1, jogo.getTrilha().getVotos(CorJogador.AZUL));
	}

	/** Os tesouros entregues saem da mão do jogador. */
	@Test(timeout = TIMEOUT)
	public void testEntregaRemoveOsTesouros() {
		azul.adicionarTesouro(TipoTesouro.PELE);
		azul.adicionarTesouro(TipoTesouro.PELE);
		jogo.entregarTesouros("D", DUAS_PELES);
		assertEquals("As peles foram entregues", 0, azul.getTesouros(TipoTesouro.PELE));
	}

	/** O primeiro a entregar recebe a peça de bônus. */
	@Test(timeout = TIMEOUT)
	public void testPrimeiroAEntregarRecebeBonus() {
		azul.adicionarTesouro(TipoTesouro.PELE);
		azul.adicionarTesouro(TipoTesouro.PELE);
		jogo.entregarTesouros("D", DUAS_PELES);
		assertEquals("AZUL deveria ter 1 peça de bônus", 1, azul.getBonus().size());
	}

	/** O segundo a entregar no mesmo conselheiro não recebe bônus. */
	@Test(timeout = TIMEOUT)
	public void testSegundoAEntregarNaoRecebeBonus() {
		azul.adicionarTesouro(TipoTesouro.PELE);
		azul.adicionarTesouro(TipoTesouro.PELE);
		jogo.entregarTesouros("D", DUAS_PELES);
		jogo.encerrarTurno();
		Jogador vermelho = jogo.getJogador(CorJogador.VERMELHO);
		vermelho.adicionarTesouro(TipoTesouro.PELE);
		vermelho.adicionarTesouro(TipoTesouro.PELE);
		irAteD(vermelho);
		jogo.entregarTesouros("D", DUAS_PELES);
		assertTrue("O bônus já foi recolhido por AZUL", vermelho.getBonus().isEmpty());
	}

	/** Usar o bônus "2 espadas" dá 2 tributos de espada. */
	@Test(timeout = TIMEOUT)
	public void testUsarBonusDuasEspadas() {
		azul.adicionarBonus(new PecaBonus(TipoBonus.DUAS_ESPADAS));
		jogo.usarBonus(TipoBonus.DUAS_ESPADAS);
		assertEquals("2 espadas do bônus", 2, azul.getTributos(TipoTributo.ESPADA));
	}

	/** Uma peça de bônus só pode ser usada uma vez. */
	@Test(timeout = TIMEOUT)
	public void testBonusNaoPodeSerUsadoDuasVezes() {
		azul.adicionarBonus(new PecaBonus(TipoBonus.DUAS_ESPADAS));
		jogo.usarBonus(TipoBonus.DUAS_ESPADAS);
		assertFalse("A peça já foi virada", jogo.usarBonus(TipoBonus.DUAS_ESPADAS));
	}

	/** O bônus "voto extra" vale imediatamente: 1 da entrega + 1 do bônus. */
	@Test(timeout = TIMEOUT)
	public void testBonusVotoExtraEhImediato() {
		conselheiroD.setBonus(new PecaBonus(TipoBonus.VOTO_EXTRA));
		azul.adicionarTesouro(TipoTesouro.PELE);
		azul.adicionarTesouro(TipoTesouro.PELE);
		jogo.entregarTesouros("D", DUAS_PELES);
		assertEquals("Entrega + voto extra", 2, jogo.getTrilha().getVotos(CorJogador.AZUL));
	}

	/** Uma entrega que não atende à demanda é recusada. */
	@Test(timeout = TIMEOUT)
	public void testEntregaQueNaoAtendeDemandaFalha() {
		azul.adicionarTesouro(TipoTesouro.PELE);
		azul.adicionarTesouro(TipoTesouro.FERRO);
		assertFalse("D pede 2 peles", jogo.entregarTesouros("D", List.of(TipoTesouro.PELE, TipoTesouro.FERRO)));
	}

	/** Não se entrega tesouro que não se possui. */
	@Test(timeout = TIMEOUT)
	public void testEntregaSemPossuirOsTesourosFalha() {
		azul.adicionarTesouro(TipoTesouro.PELE);
		assertFalse("AZUL só tem 1 pele", jogo.entregarTesouros("D", DUAS_PELES));
	}

	/** Um tesouro virtual (melhoria) completa uma entrega com um tesouro a menos. */
	@Test(timeout = TIMEOUT)
	public void testTesouroVirtualCompletaAEntrega() {
		azul.adicionarTesouro(TipoTesouro.PELE);
		jogo.getTurno().getTesourosVirtuais().add(TipoTesouro.PELE);
		assertTrue("1 pele real + 1 virtual", jogo.entregarTesouros("D", DUAS_PELES));
	}

	/** Não se entrega a um conselheiro em parada não visitada no turno. */
	@Test(timeout = TIMEOUT)
	public void testEntregaEmParadaNaoVisitadaFalha() {
		jogo.encerrarTurno();                  // VERMELHO, que está em Karakorum
		jogo.ativarColuna(0);
		jogo.getJogadorAtual().adicionarTesouro(TipoTesouro.PELE);
		jogo.getJogadorAtual().adicionarTesouro(TipoTesouro.PELE);
		assertFalse("VERMELHO não esteve em D", jogo.entregarTesouros("D", DUAS_PELES));
	}

	/** A 1a pessoa a cumprir uma carta de conselheiro aberto ganha 1 voto. */
	@Test(timeout = TIMEOUT)
	public void testCartaAbertaDaVotoAQuemCumpre() {
		usarSoACartaQuatroEspadas();
		azul.adicionarTributo(TipoTributo.ESPADA, 4);
		jogo.usarBonus(TipoBonus.DUAS_ESPADAS); // qualquer ação bem-sucedida verifica as cartas
		assertEquals("1 voto pela carta", 1, jogo.getTrilha().getVotos(CorJogador.AZUL));
	}

	/** Cumprida, a carta sai do jogo. */
	@Test(timeout = TIMEOUT)
	public void testCartaAbertaCumpridaSaiDoJogo() {
		usarSoACartaQuatroEspadas();
		azul.adicionarTributo(TipoTributo.ESPADA, 4);
		jogo.usarBonus(TipoBonus.DUAS_ESPADAS);
		assertTrue("A carta saiu do jogo", jogo.getCartasAbertas().isEmpty());
	}

	// ------------------------- auxiliares -------------------------

	/** Coloca o jogador da vez em B, ativa a coluna 0 e o move até D. */
	private void irAteD(Jogador j) {
		j.setPosicao(jogo.getTabuleiro().getParada("B"));
		jogo.ativarColuna(0);
		jogo.mover(List.of("D"));
	}

	/** Deixa em jogo só a carta "Ter 4 espadas" e dá a AZUL um bônus para disparar a verificação. */
	private void usarSoACartaQuatroEspadas() {
		jogo.substituirCartasAbertas(new ArrayList<>(List.of(FabricaCartas.cartaAberta("Ter 4 espadas"))));
		azul.adicionarBonus(new PecaBonus(TipoBonus.DUAS_ESPADAS));
	}
}
