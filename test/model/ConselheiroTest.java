package model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

/** Testes unitários da classe {@link Conselheiro} (demandas de tesouros e bônus). */
public class ConselheiroTest {
	private static final int TIMEOUT = 2000;

	private static final TipoTesouro PELE = TipoTesouro.PELE;
	private static final TipoTesouro FERRO = TipoTesouro.FERRO;
	private static final TipoTesouro SEDA = TipoTesouro.SEDA;

	/** "2 iguais de pele" aceita exatamente 2 peles. */
	@Test(timeout = TIMEOUT)
	public void testDoisIguaisAceitaDoisTesourosDoTipoPedido() {
		Conselheiro c = Conselheiro.doisIguais(PELE);
		assertTrue("2 peles deveriam atender à demanda", c.aceita(List.of(PELE, PELE)));
	}

	/** "2 iguais de pele" recusa uma pele e um ferro. */
	@Test(timeout = TIMEOUT)
	public void testDoisIguaisRecusaTiposDiferentes() {
		Conselheiro c = Conselheiro.doisIguais(PELE);
		assertFalse("Pele + ferro não atende a '2 peles'", c.aceita(List.of(PELE, FERRO)));
	}

	/** "2 iguais de pele" recusa 3 peles (quantidade errada). */
	@Test(timeout = TIMEOUT)
	public void testDoisIguaisRecusaQuantidadeErrada() {
		Conselheiro c = Conselheiro.doisIguais(PELE);
		assertFalse("3 peles não atendem a '2 peles'", c.aceita(List.of(PELE, PELE, PELE)));
	}

	/** "3 de qualquer tipo" aceita 3 tesouros misturados. */
	@Test(timeout = TIMEOUT)
	public void testTresQuaisquerAceitaTiposMisturados() {
		Conselheiro c = Conselheiro.tresQuaisquer();
		assertTrue("3 tesouros quaisquer deveriam ser aceitos", c.aceita(List.of(PELE, FERRO, FERRO)));
	}

	/** Karakorum aceita os 3 tipos pedidos, em qualquer ordem. */
	@Test(timeout = TIMEOUT)
	public void testKarakorumAceitaOsTresTiposPedidosEmQualquerOrdem() {
		Conselheiro c = Conselheiro.karakorum(PELE, FERRO, SEDA);
		assertTrue("Seda, pele e ferro deveriam ser aceitos", c.aceita(List.of(SEDA, PELE, FERRO)));
	}

	/** Karakorum recusa tipos repetidos, mesmo que todos sejam pedidos. */
	@Test(timeout = TIMEOUT)
	public void testKarakorumRecusaTipoRepetido() {
		Conselheiro c = Conselheiro.karakorum(PELE, FERRO, SEDA);
		assertFalse("Pele repetida não atende Karakorum", c.aceita(List.of(PELE, PELE, FERRO)));
	}

	/** Entregar em Karakorum vale 2 votos. */
	@Test(timeout = TIMEOUT)
	public void testKarakorumValeDoisVotos() {
		Conselheiro c = Conselheiro.karakorum(PELE, FERRO, SEDA);
		assertEquals("Karakorum deveria dar 2 votos", 2, c.getVotos());
	}

	/** A peça de bônus só é entregue uma vez (ao primeiro que entrega). */
	@Test(timeout = TIMEOUT)
	public void testBonusSoEhRecolhidoNaPrimeiraEntrega() {
		Conselheiro c = Conselheiro.doisIguais(PELE);
		c.setBonus(new PecaBonus(TipoBonus.DUAS_MOEDAS));
		c.recolherBonus();
		assertNull("O 2o a entregar não deveria receber bônus", c.recolherBonus());
	}

	/** Criar o conselheiro de Karakorum com tipos repetidos é um erro de programação. */
	@Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
	public void testKarakorumComTiposRepetidosLancaExcecao() {
		Conselheiro.karakorum(PELE, PELE, FERRO);
	}
}
