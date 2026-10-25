package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Peça de cidade. Quando revelada recebe 4 tesouros; quem tomar o último
 * tesouro conquista a cidade e coloca um yurt nela.
 */
class Cidade {
	static final int TESOUROS_INICIAIS = 4;

	private final String nome;
	private final Regiao regiao;
	private final List<TipoTesouro> tesouros = new ArrayList<>();
	private CorJogador conquistador;

	Cidade(String nome, Regiao regiao) {
		this.nome = nome;
		this.regiao = regiao;
	}

	String getNome() { return nome; }
	Regiao getRegiao() { return regiao; }
	List<TipoTesouro> getTesouros() { return Collections.unmodifiableList(tesouros); }
	CorJogador getConquistador() { return conquistador; }
	boolean isConquistada() { return conquistador != null; }

	void adicionarTesouro(TipoTesouro t) { tesouros.add(t); }

	/** Retira o tesouro escolhido. Retorna false se a cidade não tiver esse tesouro. */
	boolean retirarTesouro(TipoTesouro t) { return tesouros.remove(t); }

	void conquistar(CorJogador cor) { this.conquistador = cor; }

	@Override
	public String toString() { return nome; }
}
