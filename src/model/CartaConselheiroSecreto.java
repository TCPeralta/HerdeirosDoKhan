package model;

import java.util.function.ToIntBiFunction;

/**
 * Carta de conselheiro secreto: no fim do jogo dá 0 a 3 votos, conforme a
 * medida obtida pelo jogador atinja cada um dos 3 patamares da carta
 * (por exemplo, Soldado: 4, 7 e 10 peças de melhoria).
 */
class CartaConselheiroSecreto {
	private final String nome;
	private final int[] patamares;
	private final ToIntBiFunction<Jogador, Jogo> medida;

	CartaConselheiroSecreto(String nome, int[] patamares, ToIntBiFunction<Jogador, Jogo> medida) {
		if (patamares.length != 3) throw new IllegalArgumentException("A carta precisa de 3 patamares");
		this.nome = nome;
		this.patamares = patamares.clone();
		this.medida = medida;
	}

	String getNome() { return nome; }

	int medir(Jogador j, Jogo jogo) { return medida.applyAsInt(j, jogo); }

	/** Votos dados no fim do jogo: 1 por patamar atingido. */
	int votos(Jogador j, Jogo jogo) {
		int valor = medir(j, jogo);
		int v = 0;
		for (int p : patamares) if (valor >= p) v++;
		return v;
	}

	@Override
	public String toString() { return nome; }
}
