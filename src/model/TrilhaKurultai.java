package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Trilha do Kurultai: votos de cada jogador e a ficha branca (neutra), que
 * avança sempre que alguém ganha votos. Quando a ficha branca chega ao limite
 * (10/14/16/17 votos para 2/3/4/5 jogadores), o fim da partida é disparado.
 */
class TrilhaKurultai {
	private final Map<CorJogador, Integer> votos = new HashMap<>();
	private final int limite;
	private int fichaNeutra;

	TrilhaKurultai(int numJogadores) {
		this.limite = limiteParaJogadores(numJogadores);
	}

	static int limiteParaJogadores(int n) {
		switch (n) {
		case 2: return 10;
		case 3: return 14;
		case 4: return 16;
		case 5: return 17;
		default: throw new IllegalArgumentException("O jogo é para 2 a 5 jogadores");
		}
	}

	int getLimite() { return limite; }
	int getFichaNeutra() { return fichaNeutra; }
	int getVotos(CorJogador cor) { return votos.getOrDefault(cor, 0); }

	/** Avança a ficha do jogador e a ficha neutra. Retorna true se o fim foi atingido. */
	boolean adicionarVotos(CorJogador cor, int qtd) {
		votos.merge(cor, qtd, Integer::sum);
		fichaNeutra += qtd;
		return fimAtingido();
	}

	boolean fimAtingido() { return fichaNeutra >= limite; }
}
