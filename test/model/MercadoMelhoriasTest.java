package model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes unitários da classe {@link MercadoMelhorias} (área de melhorias).
 * Cenário: pilha com 7 melhorias; 5 são reveladas e 2 ficam na pilha.
 */
public class MercadoMelhoriasTest {
	private static final int TIMEOUT = 2000;

	private MercadoMelhorias mercado;

	@Before
	public void setUp() {
		List<Melhoria> pilha = new ArrayList<>();
		for (int i = 0; i < 7; i++) pilha.add(new Melhoria(TipoMelhoria.MOVIMENTO_DUPLO, Regiao.values()[i % 3]));
		mercado = new MercadoMelhorias(pilha);
	}

	/** Na preparação, os 5 espaços são preenchidos. */
	@Test(timeout = TIMEOUT)
	public void testMercadoComecaComOsCincoEspacosOcupados() {
		assertEquals("Deveriam restar 2 na pilha após revelar 5", 2, mercado.tamanhoPilha());
	}

	/** Após uma compra, as restantes deslizam para a direita e o 1o espaço fica vazio. */
	@Test(timeout = TIMEOUT)
	public void testRetirarDeslocaRestantesParaDireita() {
		mercado.retirarDoEspaco(4);
		assertNull("O espaço 0 deveria ficar vazio", mercado.verEspaco(0));
	}

	/** O deslocamento preserva a ordem das melhorias restantes. */
	@Test(timeout = TIMEOUT)
	public void testDeslocamentoPreservaOrdem() {
		Melhoria vizinha = mercado.verEspaco(3);
		mercado.retirarDoEspaco(4);
		assertSame("A melhoria do espaço 3 deveria ir para o 4", vizinha, mercado.verEspaco(4));
	}

	/** Reabastecer preenche os espaços vazios com melhorias da pilha. */
	@Test(timeout = TIMEOUT)
	public void testReabastecerPegaDaPilha() {
		mercado.retirarDoEspaco(0);
		mercado.reabastecer();
		assertEquals("Uma melhoria deveria ter saído da pilha", 1, mercado.tamanhoPilha());
	}

	/** Retirar de um espaço vazio retorna null. */
	@Test(timeout = TIMEOUT)
	public void testRetirarDeEspacoVazioRetornaNull() {
		mercado.retirarDoEspaco(0);
		assertNull("O espaço 0 está vazio", mercado.retirarDoEspaco(0));
	}
}
