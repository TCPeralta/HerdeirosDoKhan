package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Conselheiro do Kurultai. Fica numa parada dupla ou em Karakorum e pede um
 * conjunto específico de tesouros em troca de votos.
 */
class Conselheiro {

	/** Os 3 tipos de demanda descritos no manual (pág. 13). */
	enum TipoDemanda {
		/** 2 tesouros iguais de um tipo específico: 1 voto. */
		DOIS_IGUAIS(2, 1),
		/** 3 tesouros de qualquer tipo: 1 voto. */
		TRES_QUAISQUER(3, 1),
		/** Karakorum: 3 tipos específicos de tesouro: 2 votos. */
		KARAKORUM(3, 2);

		final int quantidade;
		final int votos;

		TipoDemanda(int quantidade, int votos) {
			this.quantidade = quantidade;
			this.votos = votos;
		}
	}

	private final TipoDemanda demanda;
	private final Set<TipoTesouro> tiposPedidos;  // vazio para TRES_QUAISQUER
	private PecaBonus bonus;                     // recolhida na 1a entrega

	Conselheiro(TipoDemanda demanda, Set<TipoTesouro> tiposPedidos) {
		this.demanda = demanda;
		this.tiposPedidos = new HashSet<>(tiposPedidos);
		if (demanda == TipoDemanda.DOIS_IGUAIS && this.tiposPedidos.size() != 1)
			throw new IllegalArgumentException("Demanda de 2 iguais precisa de exatamente 1 tipo");
		if (demanda == TipoDemanda.KARAKORUM && this.tiposPedidos.size() != 3)
			throw new IllegalArgumentException("Karakorum precisa de exatamente 3 tipos");
	}

	static Conselheiro doisIguais(TipoTesouro tipo) {
		return new Conselheiro(TipoDemanda.DOIS_IGUAIS, Set.of(tipo));
	}

	static Conselheiro tresQuaisquer() {
		return new Conselheiro(TipoDemanda.TRES_QUAISQUER, Set.of());
	}

	static Conselheiro karakorum(TipoTesouro a, TipoTesouro b, TipoTesouro c) {
		return new Conselheiro(TipoDemanda.KARAKORUM, Set.of(a, b, c));
	}

	TipoDemanda getDemanda() { return demanda; }
	Set<TipoTesouro> getTiposPedidos() { return Collections.unmodifiableSet(tiposPedidos); }
	int getVotos() { return demanda.votos; }

	PecaBonus getBonus() { return bonus; }
	void setBonus(PecaBonus bonus) { this.bonus = bonus; }

	/** Entrega a peça de bônus (só existe na 1a entrega). Retorna null depois disso. */
	PecaBonus recolherBonus() {
		PecaBonus b = bonus;
		bonus = null;
		return b;
	}

	/** Verifica se a lista de tesouros oferecida satisfaz exatamente a demanda. */
	boolean aceita(List<TipoTesouro> oferta) {
		if (oferta == null || oferta.size() != demanda.quantidade) return false;
		Map<TipoTesouro, Integer> cont = new HashMap<>();
		for (TipoTesouro t : oferta) cont.merge(t, 1, Integer::sum);
		switch (demanda) {
		case DOIS_IGUAIS:
			return cont.getOrDefault(tiposPedidos.iterator().next(), 0) == 2;
		case KARAKORUM:
			return cont.keySet().equals(tiposPedidos);
		default:
			return true;
		}
	}
}
