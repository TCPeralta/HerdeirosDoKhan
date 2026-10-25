package model;

/**
 * Tipos de peça de melhoria (44 no total) e as quantidades indicadas no manual.
 */
enum TipoMelhoria {
	/** Moeda virtual usável só no turno em que a coluna for ativada (8x). */
	MOEDA_VIRTUAL(8),
	/** Espada virtual usável só no turno em que a coluna for ativada (8x). */
	ESPADA_VIRTUAL(8),
	/** Yurt virtual usável só no turno em que a coluna for ativada (8x). */
	YURT_VIRTUAL(8),
	/** Tesouro virtual de um tipo específico (1x por tipo de tesouro). */
	TESOURO_VIRTUAL(1),
	/** Ação extra de pegar 1 peça de tributo (6x). Símbolo de punho. */
	PEGAR_TRIBUTO(6),
	/** Dois movimentos extras (9x). Símbolo de cavalo. */
	MOVIMENTO_DUPLO(9);

	private final int quantidade;

	TipoMelhoria(int quantidade) { this.quantidade = quantidade; }

	/** Quantidade de peças desse tipo (para TESOURO_VIRTUAL, por tipo de tesouro). */
	int getQuantidade() { return quantidade; }
}
