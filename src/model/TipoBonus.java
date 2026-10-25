package model;

/** As 7 peças de bônus, ganhas pelo primeiro jogador que entrega tesouros a um conselheiro. */
enum TipoBonus {
	/** Ganha 1 voto extra na trilha do Kurultai imediatamente. */
	VOTO_EXTRA,
	/** Recebe 2 peças de tributo de espada. */
	DUAS_ESPADAS,
	/** Recebe 2 peças de tributo de moeda. */
	DUAS_MOEDAS,
	/** Recebe 2 peças de tributo de yurt. */
	DOIS_YURTS,
	/** Faz 3 movimentos extras e 1 ação extra de pegar tributo (usar quando quiser). */
	TRES_MOVIMENTOS_E_TRIBUTO,
	/** Faz 2 ações extras de pegar tributo (usar quando quiser). */
	DOIS_TRIBUTOS,
	/** Pega 3 tesouros aleatórios da pilha, escolhe 1 e descarta os outros. */
	TRES_TESOUROS
}
