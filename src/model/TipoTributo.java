package model;

/**
 * Os 3 tipos de peça de tributo que ficam nas províncias.
 * Cada um permite uma ação extra diferente.
 */
enum TipoTributo {
	/** Permite atacar cidades. */
	ESPADA,
	/** Permite construir yurts no mapa. */
	YURT,
	/** Duas moedas compram uma peça de melhoria. */
	MOEDA;

	/** Quantidade de peças de cada tipo na caixa (10 espadas, 10 yurts, 15 moedas). */
	int quantidadeNaCaixa() {
		switch (this) {
		case ESPADA: return 10;
		case YURT:   return 10;
		default:     return 15;
		}
	}
}
