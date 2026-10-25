package model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/** Testes unitários da classe {@link Provincia}. */
public class ProvinciaTest {
	private static final int TIMEOUT = 2000;

	private Provincia provincia;

	@Before
	public void setUp() {
		provincia = new Provincia("P", Regiao.CHINA, TipoTributo.MOEDA, false);
	}

	/** Uma província recém-criada não tem tributos. */
	@Test(timeout = TIMEOUT)
	public void testProvinciaNovaComecaSemTributos() {
		assertEquals("Província nova deveria estar vazia", 0, provincia.getTributos());
	}

	/** Mesmo recebendo 4 tributos, a província guarda no máximo 3. */
	@Test(timeout = TIMEOUT)
	public void testProvinciaGuardaNoMaximoTresTributos() {
		adicionar(4);
		assertEquals("Uma província não pode passar de 3 tributos", 3, provincia.getTributos());
	}

	/** Adicionar um tributo numa província cheia retorna false. */
	@Test(timeout = TIMEOUT)
	public void testAdicionarTributoEmProvinciaCheiaRetornaFalse() {
		adicionar(3);
		assertFalse("Província cheia não deveria aceitar mais tributo", provincia.adicionarTributo());
	}

	/** Retirar tributo diminui a quantidade em 1. */
	@Test(timeout = TIMEOUT)
	public void testRetirarTributoDiminuiUm() {
		adicionar(2);
		provincia.retirarTributo();
		assertEquals("Deveria sobrar 1 tributo", 1, provincia.getTributos());
	}

	/** Retirar tributo de uma província vazia retorna false. */
	@Test(timeout = TIMEOUT)
	public void testRetirarTributoDeProvinciaVaziaRetornaFalse() {
		assertFalse("Não há tributo para retirar", provincia.retirarTributo());
	}

	private void adicionar(int quantidade) {
		for (int i = 0; i < quantidade; i++) provincia.adicionarTributo();
	}
}
