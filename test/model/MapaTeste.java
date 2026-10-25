package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Mapa pequeno e conhecido, usado apenas nos testes, para que os casos de
 * teste não dependam do mapa completo do jogo.
 *
 * <pre>
 *            KARAKORUM ---- A ---- B ---- D (dupla, conselheiro: 2 PELE)
 *                \                         |
 *                 '----------- E ----------'
 *
 * Regiões: A e B = RÚSSIA, D = CHINA, E = PÉRSIA.
 * Províncias: PA (espada) adj. a A; PB (moeda) adj. a B;
 *             KHAN1 (yurt, do Khan) adj. a D e E, setas para PA e PB;
 *             KHAN2 (espada, do Khan) adj. a Karakorum, setas para PA e PB.
 * Cidades C1..C4 (4 tesouros cada) adjacentes apenas à parada A.
 * </pre>
 */
final class MapaTeste {
	static final long SEMENTE = 42L;

	private MapaTeste() {}

	static Tabuleiro criar() {
		Tabuleiro t = new Tabuleiro();
		t.adicionarParada("KARAKORUM", TipoParada.KARAKORUM, null)
				.setConselheiro(Conselheiro.karakorum(TipoTesouro.PELE, TipoTesouro.FERRO, TipoTesouro.SEDA));
		t.adicionarParada("A", TipoParada.SIMPLES, Regiao.RUSSIA);
		t.adicionarParada("B", TipoParada.SIMPLES, Regiao.RUSSIA);
		t.adicionarParada("D", TipoParada.DUPLA, Regiao.CHINA)
				.setConselheiro(Conselheiro.doisIguais(TipoTesouro.PELE));
		t.adicionarParada("E", TipoParada.SIMPLES, Regiao.PERSIA);

		t.ligarParadas("KARAKORUM", "A");
		t.ligarParadas("A", "B");
		t.ligarParadas("B", "D");
		t.ligarParadas("D", "E");
		t.ligarParadas("E", "KARAKORUM");

		t.adicionarProvincia("PA", Regiao.RUSSIA, TipoTributo.ESPADA, false);
		t.adicionarProvincia("PB", Regiao.RUSSIA, TipoTributo.MOEDA, false);
		t.adicionarProvincia("KHAN1", Regiao.CHINA, TipoTributo.YURT, true);
		t.adicionarProvincia("KHAN2", Regiao.PERSIA, TipoTributo.ESPADA, true);
		t.ligarProvincia("A", "PA");
		t.ligarProvincia("B", "PB");
		t.ligarProvincia("D", "KHAN1");
		t.ligarProvincia("E", "KHAN1");
		t.ligarProvincia("KARAKORUM", "KHAN2");
		t.definirSetasDoKhan("KHAN1", "PA", "PB");
		t.definirSetasDoKhan("KHAN2", "PA", "PB");

		t.adicionarCidade("C1", Regiao.RUSSIA);
		t.adicionarCidade("C2", Regiao.RUSSIA);
		t.adicionarCidade("C3", Regiao.CHINA);
		t.adicionarCidade("C4", Regiao.PERSIA);
		for (String c : new String[] { "C1", "C2", "C3", "C4" }) t.ligarCidade("A", c);
		return t;
	}

	/**
	 * Partida de 2 jogadores (AZUL joga primeiro, VERMELHO depois) neste mapa,
	 * sem cartas de conselheiro aberto, para que nenhum voto inesperado
	 * interfira nos testes.
	 */
	static Jogo novaPartida() {
		return novaPartida(CorJogador.AZUL, CorJogador.VERMELHO);
	}

	/**
	 * Partida com as cores dadas (na ordem de jogo). Os herdeiros são atribuídos
	 * na ordem TOLUI, ALTANI, CHAGATAI, JOCHI, OGEDEI.
	 */
	static Jogo novaPartida(CorJogador... ordem) {
		Herdeiro[] todos = { Herdeiro.TOLUI, Herdeiro.ALTANI, Herdeiro.CHAGATAI, Herdeiro.JOCHI, Herdeiro.OGEDEI };
		List<CorJogador> cores = List.of(ordem);
		List<Herdeiro> herdeiros = new ArrayList<>(List.of(todos).subList(0, Math.min(ordem.length, todos.length)));
		Jogo jogo = new Jogo(criar(), cores, herdeiros, new Random(SEMENTE));
		jogo.substituirCartasAbertas(new ArrayList<>());
		return jogo;
	}
}
