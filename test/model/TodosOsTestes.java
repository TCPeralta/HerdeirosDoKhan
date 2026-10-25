package model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/** Suíte com todos os casos de teste do componente Model (Run As > JUnit Test). */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	// testes das classes básicas
	ProvinciaTest.class,
	ConselheiroTest.class,
	TrilhaKurultaiTest.class,
	TabuleiroJogadorTest.class,
	MercadoMelhoriasTest.class,
	TabuleiroTest.class,
	FabricaCartasTest.class,
	// testes das regras do jogo (classe Jogo)
	JogoPreparacaoTest.class,
	JogoColunasTest.class,
	JogoMovimentoTest.class,
	JogoTributoTest.class,
	JogoKhanTest.class,
	JogoCidadeTest.class,
	JogoYurtTest.class,
	JogoMelhoriaTest.class,
	JogoConselheiroTest.class,
	JogoFimDeJogoTest.class
})
public class TodosOsTestes {
}
