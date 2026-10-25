package model;

/**
 * Tipos de parada do mapa. Cada tipo define quantos peões podem terminar
 * o movimento nela e quantos yurts ela comporta.
 */
enum TipoParada {
	/** Parada comum: 1 peão e 1 yurt. */
	SIMPLES(1, 1),
	/** Parada dupla (local de conselheiro): 2 peões e 2 yurts. */
	DUPLA(2, 2),
	/** Karakorum, a capital: todos os peões e nenhum yurt. */
	KARAKORUM(Integer.MAX_VALUE, 0);

	private final int capacidadePeoes;
	private final int capacidadeYurts;

	TipoParada(int capacidadePeoes, int capacidadeYurts) {
		this.capacidadePeoes = capacidadePeoes;
		this.capacidadeYurts = capacidadeYurts;
	}

	int getCapacidadePeoes() { return capacidadePeoes; }
	int getCapacidadeYurts() { return capacidadeYurts; }
}
