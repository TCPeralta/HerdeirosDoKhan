package model;

import java.util.function.BiPredicate;

/**
 * Carta de conselheiro aberto: o primeiro jogador que cumprir a condição
 * ganha 1 voto e a carta sai do jogo.
 *
 * A condição é passada como um objeto (BiPredicate), de modo que novas cartas
 * podem ser criadas sem alterar esta classe (princípio aberto/fechado).
 */
class CartaConselheiroAberto {
	static final int VOTOS = 1;

	private final String descricao;
	private final BiPredicate<Jogador, Jogo> condicao;

	CartaConselheiroAberto(String descricao, BiPredicate<Jogador, Jogo> condicao) {
		this.descricao = descricao;
		this.condicao = condicao;
	}

	String getDescricao() { return descricao; }

	boolean cumprida(Jogador j, Jogo jogo) { return condicao.test(j, jogo); }

	@Override
	public String toString() { return descricao; }
}
