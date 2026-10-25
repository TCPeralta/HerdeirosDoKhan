package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Um jogador: cor, herdeiro, posição do peão, peças de tributo, tesouros,
 * yurts na reserva, peças de bônus e cartas de conselheiro secreto.
 */
class Jogador {
	static final int YURTS_INICIAIS = 12;

	private final CorJogador cor;
	private final TabuleiroJogador tabuleiro;
	private Parada posicao;

	private final Map<TipoTributo, Integer> tributos = new HashMap<>();
	private final Map<TipoTesouro, Integer> tesouros = new HashMap<>();
	private int yurtsNaReserva = YURTS_INICIAIS;
	private int cidadesConquistadas;

	private final List<PecaBonus> bonus = new ArrayList<>();
	private final List<CartaConselheiroSecreto> cartasSecretasRecebidas = new ArrayList<>();
	private CartaConselheiroSecreto cartaSecreta;

	Jogador(CorJogador cor, Herdeiro herdeiro) {
		this.cor = cor;
		this.tabuleiro = new TabuleiroJogador(herdeiro);
	}

	CorJogador getCor() { return cor; }
	TabuleiroJogador getTabuleiro() { return tabuleiro; }
	Herdeiro getHerdeiro() { return tabuleiro.getHerdeiro(); }

	Parada getPosicao() { return posicao; }
	void setPosicao(Parada p) { this.posicao = p; }

	// ---------- tributos ----------
	int getTributos(TipoTributo t) { return tributos.getOrDefault(t, 0); }

	void adicionarTributo(TipoTributo t, int qtd) { tributos.merge(t, qtd, Integer::sum); }

	boolean gastarTributo(TipoTributo t, int qtd) {
		if (getTributos(t) < qtd) return false;
		tributos.put(t, getTributos(t) - qtd);
		return true;
	}

	// ---------- tesouros ----------
	int getTesouros(TipoTesouro t) { return tesouros.getOrDefault(t, 0); }

	int getTotalTesouros() {
		int soma = 0;
		for (int q : tesouros.values()) soma += q;
		return soma;
	}

	/** Quantidade de tipos diferentes de tesouro que o jogador possui. */
	int getTiposDiferentesTesouros() {
		int tipos = 0;
		for (int q : tesouros.values()) if (q > 0) tipos++;
		return tipos;
	}

	void adicionarTesouro(TipoTesouro t) { tesouros.merge(t, 1, Integer::sum); }

	boolean retirarTesouro(TipoTesouro t) {
		if (getTesouros(t) == 0) return false;
		tesouros.put(t, getTesouros(t) - 1);
		return true;
	}

	// ---------- yurts ----------
	int getYurtsNaReserva() { return yurtsNaReserva; }

	boolean retirarYurtDaReserva() {
		if (yurtsNaReserva == 0) return false;
		yurtsNaReserva--;
		return true;
	}

	int getCidadesConquistadas() { return cidadesConquistadas; }
	void registrarConquista() { cidadesConquistadas++; }

	// ---------- bônus ----------
	List<PecaBonus> getBonus() { return Collections.unmodifiableList(bonus); }
	void adicionarBonus(PecaBonus b) { bonus.add(b); }

	/** Peça de bônus desse tipo ainda não usada, ou null. */
	PecaBonus getBonusDisponivel(TipoBonus tipo) {
		for (PecaBonus b : bonus)
			if (b.getTipo() == tipo && !b.isUsada()) return b;
		return null;
	}

	// ---------- cartas de conselheiro secreto ----------
	void receberCartaSecreta(CartaConselheiroSecreto c) { cartasSecretasRecebidas.add(c); }

	List<CartaConselheiroSecreto> getCartasSecretasRecebidas() {
		return Collections.unmodifiableList(cartasSecretasRecebidas);
	}

	CartaConselheiroSecreto getCartaSecreta() { return cartaSecreta; }

	/** Escolhe uma das 2 cartas recebidas; a outra é descartada. */
	boolean escolherCartaSecreta(int indice) {
		if (cartaSecreta != null || indice < 0 || indice >= cartasSecretasRecebidas.size()) return false;
		cartaSecreta = cartasSecretasRecebidas.get(indice);
		cartasSecretasRecebidas.clear();
		return true;
	}

	@Override
	public String toString() { return cor.name(); }
}
