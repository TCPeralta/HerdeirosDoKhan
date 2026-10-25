package model;

/**
 * Os 5 tipos de peça de tesouro (8 de cada, 40 no total).
 * Os nomes de PELE e FERRO vêm do manual; os demais são provisórios
 * e devem ser conferidos com as peças reais do jogo.
 */
enum TipoTesouro {
	PELE, FERRO, SEDA, PORCELANA, CHIFRE;

	static final int QUANTIDADE_POR_TIPO = 8;
}
