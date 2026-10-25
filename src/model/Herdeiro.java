package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Os 5 herdeiros do Khan. Cada um tem um tabuleiro de jogador com ações
 * iniciais próprias e uma habilidade especial (manual, pág. 20).
 *
 * As ações iniciais abaixo foram lidas da imagem do tabuleiro de Tolui no
 * manual (pág. 6) e, por enquanto, são usadas para todos os herdeiros.
 * Ajustar quando os tabuleiros reais forem conferidos: basta alterar o método
 * {@link #acoesIniciais(int)}, sem mexer no resto do Model (princípio aberto/fechado).
 */
enum Herdeiro {
	ALTANI, CHAGATAI, JOCHI, OGEDEI, TOLUI;

	/** Ordem das cores das 3 colunas coloridas (a 4a coluna é sempre a branca). */
	static final Regiao[] CORES_COLUNAS = { Regiao.CHINA, Regiao.RUSSIA, Regiao.PERSIA };

	/** Ações impressas na base da coluna {@code indice} (0 a 3; 3 = branca). */
	Map<TipoAcao, Integer> acoesIniciais(int indice) {
		Map<TipoAcao, Integer> a = new HashMap<>();
		switch (indice) {
		case 0: // coluna vermelha (China)
			a.put(TipoAcao.MOVIMENTO, 1);
			a.put(TipoAcao.PEGAR_TRIBUTO, 1);
			break;
		case 1: // coluna amarela (Rússia)
			a.put(TipoAcao.MOVIMENTO, 2);
			a.put(TipoAcao.PEGAR_TRIBUTO, 1);
			break;
		case 2: // coluna verde (Pérsia)
			a.put(TipoAcao.MOVIMENTO, 1);
			a.put(TipoAcao.KHAN, 1);
			break;
		case 3: // coluna branca
			a.put(TipoAcao.MOVIMENTO, 2);
			a.put(TipoAcao.KHAN, 1);
			a.put(TipoAcao.RECUPERAR, 1);
			break;
		default:
			throw new IllegalArgumentException("Coluna inexistente: " + indice);
		}
		return a;
	}
}
