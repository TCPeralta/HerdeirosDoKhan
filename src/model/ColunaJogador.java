package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Uma coluna do tabuleiro de jogador. Tem as ações iniciais (impressas na base)
 * e até 4 espaços de melhoria, preenchidos de baixo para cima.
 * As 3 colunas coloridas correspondem às 3 regiões; a coluna branca tem cor null.
 */
class ColunaJogador {
	static final int ESPACOS_MELHORIA = 4;

	private final Regiao cor;   // null = coluna branca
	private final Map<TipoAcao, Integer> acoesIniciais = new HashMap<>();
	private final List<Melhoria> melhorias = new ArrayList<>();
	private boolean ativada;

	ColunaJogador(Regiao cor, Map<TipoAcao, Integer> acoesIniciais) {
		this.cor = cor;
		this.acoesIniciais.putAll(acoesIniciais);
	}

	Regiao getCor() { return cor; }
	boolean isBranca() { return cor == null; }

	int getAcaoInicial(TipoAcao tipo) { return acoesIniciais.getOrDefault(tipo, 0); }

	List<Melhoria> getMelhorias() { return Collections.unmodifiableList(melhorias); }
	int getQuantidadeMelhorias() { return melhorias.size(); }
	boolean temEspaco() { return melhorias.size() < ESPACOS_MELHORIA; }

	/** Coloca a melhoria no próximo espaço livre (de baixo para cima). */
	void adicionarMelhoria(Melhoria m) {
		if (!temEspaco()) throw new IllegalStateException("Coluna cheia");
		melhorias.add(m);
	}

	boolean isAtivada() { return ativada; }
	void ativar() { ativada = true; }
	void recuperar() { ativada = false; }
}
