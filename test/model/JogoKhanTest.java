package model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes da ação USAR O KHAN de {@link Jogo} (manual, pág. 9).
 * KHAN1 e KHAN2 são províncias do Khan com setas para PA e PB.
 * A coluna 2 do jogador tem o ícone do Khan; a coluna 0 não tem.
 */
public class JogoKhanTest {
	private static final int TIMEOUT = 2000;

	private Jogo jogo;
	private Tabuleiro mapa;

	@Before
	public void setUp() {
		jogo = MapaTeste.novaPartida();
		mapa = jogo.getTabuleiro();
	}

	/** O Khan coloca 1 tributo na província onde é colocado. */
	@Test(timeout = TIMEOUT)
	public void testKhanAbasteceAPropriaProvincia() {
		jogo.ativarColuna(2);
		jogo.moverKhan("KHAN1");
		assertEquals("KHAN1: 1 inicial + 1", 2, mapa.getProvincia("KHAN1").getTributos());
	}

	/** O Khan também abastece as províncias indicadas pelas setas. */
	@Test(timeout = TIMEOUT)
	public void testKhanAbasteceProvinciasDasSetas() {
		jogo.ativarColuna(2);
		jogo.moverKhan("KHAN1");
		assertEquals("PB: 1 inicial + 1", 2, mapa.getProvincia("PB").getTributos());
	}

	/** Uma província com 3 tributos não recebe mais. */
	@Test(timeout = TIMEOUT)
	public void testKhanRespeitaMaximoDeTresTributos() {
		mapa.getProvincia("PA").adicionarTributo();
		mapa.getProvincia("PA").adicionarTributo();
		jogo.ativarColuna(2);
		jogo.moverKhan("KHAN1");
		assertEquals("PA já tinha o máximo", 3, mapa.getProvincia("PA").getTributos());
	}

	/** Sem o ícone do Khan na coluna, não se pode movê-lo. */
	@Test(timeout = TIMEOUT)
	public void testMoverKhanSemIconeNaColunaFalha() {
		jogo.ativarColuna(0);
		assertFalse("A coluna 0 não tem o Khan", jogo.moverKhan("KHAN1"));
	}

	/** Usar o Khan é obrigatório: o turno não termina com o Khan pendente. */
	@Test(timeout = TIMEOUT)
	public void testTurnoNaoTerminaComKhanPendente() {
		jogo.ativarColuna(2);
		assertFalse("O Khan não foi usado", jogo.encerrarTurno());
	}

	/** Depois de usar o Khan, o turno pode terminar. */
	@Test(timeout = TIMEOUT)
	public void testTurnoTerminaDepoisDeUsarKhan() {
		jogo.ativarColuna(2);
		jogo.moverKhan("KHAN1");
		assertTrue("O Khan já foi usado", jogo.encerrarTurno());
	}

	/** O Khan não pode permanecer onde está. */
	@Test(timeout = TIMEOUT)
	public void testKhanNaoPodeFicarNoMesmoLugar() {
		jogo.ativarColuna(2);
		jogo.moverKhan("KHAN1");
		jogo.encerrarTurno();
		jogo.ativarColuna(2);
		assertFalse("O Khan já está em KHAN1", jogo.moverKhan("KHAN1"));
	}

	/** O Khan só vai para províncias do Khan ou para a área de melhorias. */
	@Test(timeout = TIMEOUT)
	public void testKhanEmProvinciaComumFalha() {
		jogo.ativarColuna(2);
		assertFalse("PA não é província do Khan", jogo.moverKhan("PA"));
	}

	/** Na área de melhorias, com o espaço do ícone de moeda vazio, o jogador ganha 1 moeda. */
	@Test(timeout = TIMEOUT)
	public void testKhanNaAreaDeMelhoriasComIconeDeMoedaVazioDaMoeda() {
		esvaziarDoisEspacosDoMercado();
		jogo.ativarColuna(2);
		jogo.moverKhan(Jogo.AREA_MELHORIAS);
		assertEquals("1 moeda inicial + 1 do ícone", 2, jogo.getJogador(CorJogador.AZUL).getTributos(TipoTributo.MOEDA));
	}

	/** Na área de melhorias, os espaços vazios são reabastecidos. */
	@Test(timeout = TIMEOUT)
	public void testKhanNaAreaDeMelhoriasReabasteceEspacos() {
		esvaziarDoisEspacosDoMercado();
		jogo.ativarColuna(2);
		jogo.moverKhan(Jogo.AREA_MELHORIAS);
		assertNotNull("O espaço 0 deveria ser reabastecido", jogo.getMercado().verEspaco(0));
	}

	/** Retira 2 melhorias: os espaços 0 e 1 (ícone de moeda) ficam vazios. */
	private void esvaziarDoisEspacosDoMercado() {
		jogo.getMercado().retirarDoEspaco(4);
		jogo.getMercado().retirarDoEspaco(4);
	}
}
