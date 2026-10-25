package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tabuleiro individual do jogador: 3 colunas coloridas (uma por região) e a
 * coluna branca. O jogador ativa uma coluna por turno colocando nela uma peça
 * de ativação; a coluna branca recupera todas as peças.
 */
class TabuleiroJogador {
	static final int NUM_COLUNAS = 4;
	static final int INDICE_BRANCA = 3;

	private final Herdeiro herdeiro;
	private final List<ColunaJogador> colunas = new ArrayList<>();
	/** Linhas completas que já renderam melhoria gratuita na coluna branca. */
	private int linhasPremiadas;

	TabuleiroJogador(Herdeiro herdeiro) {
		this.herdeiro = herdeiro;
		for (int i = 0; i < INDICE_BRANCA; i++)
			colunas.add(new ColunaJogador(Herdeiro.CORES_COLUNAS[i], herdeiro.acoesIniciais(i)));
		colunas.add(new ColunaJogador(null, herdeiro.acoesIniciais(INDICE_BRANCA)));
		// Jochi já começa com o 1o espaço da coluna branca ocupado (manual, pág. 20)
		if (herdeiro == Herdeiro.JOCHI) linhasPremiadas = 1;
	}

	Herdeiro getHerdeiro() { return herdeiro; }
	List<ColunaJogador> getColunas() { return Collections.unmodifiableList(colunas); }
	ColunaJogador getColuna(int i) { return colunas.get(i); }
	ColunaJogador getColunaBranca() { return colunas.get(INDICE_BRANCA); }

	/** Coluna colorida associada a uma região. */
	ColunaJogador getColunaDaRegiao(Regiao r) {
		for (ColunaJogador c : colunas)
			if (c.getCor() == r) return c;
		throw new IllegalArgumentException("Sem coluna para " + r);
	}

	/** Recupera todas as peças de ativação (efeito da coluna branca). */
	void recuperarTodas() {
		for (ColunaJogador c : colunas) c.recuperar();
	}

	/** Pode receber uma melhoria colorida dessa cor? */
	boolean podeReceber(Melhoria m) {
		return getColunaDaRegiao(m.getCor()).temEspaco();
	}

	/**
	 * Número de linhas completas nas 3 colunas coloridas (uma linha está completa
	 * quando as 3 colunas têm melhoria naquela altura).
	 */
	int linhasCompletas() {
		int min = ColunaJogador.ESPACOS_MELHORIA;
		for (int i = 0; i < INDICE_BRANCA; i++)
			min = Math.min(min, colunas.get(i).getQuantidadeMelhorias());
		return min;
	}

	/**
	 * Quantas linhas foram completadas desde a última consulta e ainda não
	 * renderam melhoria gratuita. Cada uma dá direito a 1 melhoria na coluna branca.
	 */
	int registrarNovasLinhasCompletas() {
		int novas = Math.max(0, linhasCompletas() - linhasPremiadas);
		linhasPremiadas += novas;
		return novas;
	}

	/** Todas as melhorias do tabuleiro, inclusive as da coluna branca. */
	List<Melhoria> todasMelhorias() {
		List<Melhoria> todas = new ArrayList<>();
		for (ColunaJogador c : colunas) todas.addAll(c.getMelhorias());
		return todas;
	}
}
