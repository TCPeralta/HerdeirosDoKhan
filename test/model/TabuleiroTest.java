package model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes unitários da classe {@link Tabuleiro} (o mapa), usando o {@link MapaTeste}:
 * KARAKORUM - A - B - D - E - KARAKORUM.
 */
public class TabuleiroTest {
	private static final int TIMEOUT = 2000;

	private Tabuleiro mapa;

	@Before
	public void setUp() {
		mapa = MapaTeste.criar();
	}

	/** Paradas ligadas por rota estão à distância 1. */
	@Test(timeout = TIMEOUT)
	public void testDistanciaEntreParadasVizinhasEhUm() {
		assertEquals("Karakorum e A são vizinhas", 1, mapa.distancia("KARAKORUM", "A"));
	}

	/** A distância usa o caminho mais curto (Karakorum-E-D, e não Karakorum-A-B-D). */
	@Test(timeout = TIMEOUT)
	public void testDistanciaUsaCaminhoMaisCurto() {
		assertEquals("Menor caminho até D passa por E", 2, mapa.distancia("KARAKORUM", "D"));
	}

	/** As rotas valem nos dois sentidos. */
	@Test(timeout = TIMEOUT)
	public void testRotaEhDeMaoDupla() {
		assertTrue("Se A liga com B, B liga com A", mapa.saoVizinhas("B", "A"));
	}

	/** Paradas sem rota entre si não são vizinhas. */
	@Test(timeout = TIMEOUT)
	public void testParadasSemRotaNaoSaoVizinhas() {
		assertFalse("A e D não são ligadas diretamente", mapa.saoVizinhas("A", "D"));
	}

	/** Os yurts numa região incluem os das cidades conquistadas. */
	@Test(timeout = TIMEOUT)
	public void testYurtsNaRegiaoContamCidadeConquistada() {
		mapa.getParada("A").adicionarYurt(CorJogador.AZUL);
		mapa.getCidade("C1").conquistar(CorJogador.AZUL);
		assertEquals("Yurt em A + cidade C1 (ambos na Rússia)", 2, mapa.yurtsNaRegiao(CorJogador.AZUL, Regiao.RUSSIA));
	}

	/** O maior grupo conectado ignora yurts isolados (carta Viajante). */
	@Test(timeout = TIMEOUT)
	public void testMaiorGrupoConectadoIgnoraYurtIsolado() {
		mapa.getParada("A").adicionarYurt(CorJogador.AZUL);
		mapa.getParada("B").adicionarYurt(CorJogador.AZUL);
		mapa.getParada("E").adicionarYurt(CorJogador.AZUL);
		assertEquals("A-B formam um grupo de 2; E está separado", 2, mapa.maiorGrupoConectado(CorJogador.AZUL));
	}

	/** Um yurt em A é adjacente às 4 cidades do mapa de teste (cartas Prefeito/Cidadão). */
	@Test(timeout = TIMEOUT)
	public void testCidadesComYurtAdjacente() {
		mapa.getParada("A").adicionarYurt(CorJogador.AZUL);
		assertEquals("A é vizinha das 4 cidades", 4, mapa.cidadesComYurtsAdjacentes(CorJogador.AZUL, 1));
	}

	/** A parada dupla aceita 2 yurts (mesmo que sejam do mesmo jogador). */
	@Test(timeout = TIMEOUT)
	public void testParadaDuplaAceitaDoisYurtsDoMesmoJogador() {
		Parada d = mapa.getParada("D");
		d.adicionarYurt(CorJogador.AZUL);
		d.adicionarYurt(CorJogador.AZUL);
		assertEquals("D deveria ter 2 yurts", 2, d.getYurts().size());
	}

	/** Uma parada simples não aceita um 2o yurt. */
	@Test(timeout = TIMEOUT, expected = IllegalStateException.class)
	public void testParadaSimplesComSegundoYurtLancaExcecao() {
		mapa.getParada("A").adicionarYurt(CorJogador.AZUL);
		mapa.getParada("A").adicionarYurt(CorJogador.VERMELHO);
	}

	/** Não se pode criar duas paradas com o mesmo id. */
	@Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
	public void testParadaRepetidaLancaExcecao() {
		mapa.adicionarParada("A", TipoParada.SIMPLES, Regiao.RUSSIA);
	}
}
