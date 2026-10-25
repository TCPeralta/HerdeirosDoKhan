package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Uma parada do mapa (local onde os peões podem parar).
 * As ligações (rotas) entre paradas e as adjacências com províncias e cidades
 * ficam guardadas no {@link Tabuleiro}.
 */
class Parada {
	private final String id;
	private final TipoParada tipo;
	private final Regiao regiao;          // null para Karakorum
	private Conselheiro conselheiro;      // só paradas duplas e Karakorum
	private final List<CorJogador> yurts = new ArrayList<>();

	Parada(String id, TipoParada tipo, Regiao regiao) {
		this.id = id;
		this.tipo = tipo;
		this.regiao = regiao;
	}

	String getId() { return id; }
	TipoParada getTipo() { return tipo; }
	Regiao getRegiao() { return regiao; }
	Conselheiro getConselheiro() { return conselheiro; }
	void setConselheiro(Conselheiro c) { this.conselheiro = c; }

	boolean isDupla() { return tipo == TipoParada.DUPLA; }
	boolean isKarakorum() { return tipo == TipoParada.KARAKORUM; }

	List<CorJogador> getYurts() { return Collections.unmodifiableList(yurts); }

	boolean temYurtDe(CorJogador cor) { return yurts.contains(cor); }

	boolean temEspacoParaYurt() { return yurts.size() < tipo.getCapacidadeYurts(); }

	void adicionarYurt(CorJogador cor) {
		if (!temEspacoParaYurt())
			throw new IllegalStateException("Parada " + id + " não comporta mais yurts");
		yurts.add(cor);
	}

	@Override
	public String toString() { return id; }
}
