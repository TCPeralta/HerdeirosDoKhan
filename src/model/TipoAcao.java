package model;

/** Ícones de ação que aparecem nas colunas do tabuleiro de jogador e nas melhorias. */
enum TipoAcao {
	/** Cavalo: um movimento para uma parada adjacente. */
	MOVIMENTO,
	/** Punho: pegar uma peça de tributo de uma província adjacente. */
	PEGAR_TRIBUTO,
	/** Cabeça do Khan: mover o Khan (ação obrigatória). */
	KHAN,
	/** Setas circulares (coluna branca): recuperar as peças de ativação. */
	RECUPERAR
}
