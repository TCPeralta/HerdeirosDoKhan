package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Área de melhorias na parte de baixo do tabuleiro principal: uma pilha virada
 * para baixo e 5 espaços com melhorias reveladas. Dois espaços têm ícones
 * (moeda e espada) que o Khan "paga" quando é colocado aqui e esses espaços
 * estão vazios.
 */
class MercadoMelhorias {
	static final int NUM_ESPACOS = 5;
	/** Espaços com ícone impresso (posições provisórias; conferir no tabuleiro). */
	static final int ESPACO_ICONE_MOEDA = 1;
	static final int ESPACO_ICONE_ESPADA = 3;

	private final List<Melhoria> pilha;
	private final List<Melhoria> espacos = new ArrayList<>();

	MercadoMelhorias(List<Melhoria> pilhaEmbaralhada) {
		this.pilha = new ArrayList<>(pilhaEmbaralhada);
		for (int i = 0; i < NUM_ESPACOS; i++) espacos.add(null);
		reabastecer();
	}

	List<Melhoria> getEspacos() { return Collections.unmodifiableList(espacos); }
	int tamanhoPilha() { return pilha.size(); }

	Melhoria verEspaco(int i) { return espacos.get(i); }

	/** Topo da pilha sem retirá-lo (ou null). */
	Melhoria verTopo() { return pilha.isEmpty() ? null : pilha.get(pilha.size() - 1); }

	/** Retira a melhoria de um espaço e desloca as restantes para a direita. */
	Melhoria retirarDoEspaco(int i) {
		Melhoria m = espacos.get(i);
		if (m == null) return null;
		espacos.set(i, null);
		deslocarParaDireita();
		return m;
	}

	Melhoria retirarDoTopo() {
		return pilha.isEmpty() ? null : pilha.remove(pilha.size() - 1);
	}

	boolean espacoVazio(int i) { return espacos.get(i) == null; }

	/** Preenche os espaços vazios com melhorias da pilha (só quando o Khan vem para cá). */
	void reabastecer() {
		for (int i = 0; i < NUM_ESPACOS; i++)
			if (espacos.get(i) == null && !pilha.isEmpty()) espacos.set(i, retirarDoTopo());
	}

	private void deslocarParaDireita() {
		List<Melhoria> restantes = new ArrayList<>();
		for (Melhoria m : espacos) if (m != null) restantes.add(m);
		int vazios = NUM_ESPACOS - restantes.size();
		for (int i = 0; i < NUM_ESPACOS; i++)
			espacos.set(i, i < vazios ? null : restantes.get(i - vazios));
	}
}
