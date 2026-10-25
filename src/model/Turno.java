package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Estado do turno do jogador da vez: ações ainda disponíveis, paradas
 * visitadas, recursos virtuais das melhorias da coluna ativada, etc.
 * Tudo isto é descartado quando o turno termina.
 */
class Turno {
	private final Jogador jogador;
	private final int numero;

	private int colunaAtivada = -1;
	private int movimentos;
	private int acoesTributo;
	private boolean khanPendente;
	private boolean recuperar;

	/** Paradas onde o jogador esteve no turno (exclui as que ele pulou). */
	private final Set<String> paradasVisitadas = new HashSet<>();
	/** Recursos virtuais vindos das melhorias da coluna ativada. */
	private final Map<TipoTributo, Integer> virtuais = new HashMap<>();
	/** Tesouros virtuais ainda não usados neste turno. */
	private final List<TipoTesouro> tesourosVirtuais = new ArrayList<>();
	/** Tesouros tomados de cada cidade neste turno (define o custo em espadas). */
	private final Map<String, Integer> tesourosTomados = new HashMap<>();
	/** Melhorias gratuitas da coluna branca a receber. */
	private int melhoriasGratuitasPendentes;
	/** Opções de tesouro do bônus "3 tesouros" aguardando escolha. */
	private final List<TipoTesouro> opcoesTesouroBonus = new ArrayList<>();

	Turno(Jogador jogador, int numero) {
		this.jogador = jogador;
		this.numero = numero;
		if (jogador.getPosicao() != null) paradasVisitadas.add(jogador.getPosicao().getId());
	}

	Jogador getJogador() { return jogador; }
	int getNumero() { return numero; }

	boolean colunaFoiAtivada() { return colunaAtivada >= 0; }
	int getColunaAtivada() { return colunaAtivada; }
	void setColunaAtivada(int c) { colunaAtivada = c; }

	int getMovimentos() { return movimentos; }
	void adicionarMovimentos(int n) { movimentos += n; }
	void gastarMovimentos(int n) { movimentos -= n; }

	int getAcoesTributo() { return acoesTributo; }
	void adicionarAcoesTributo(int n) { acoesTributo += n; }
	void gastarAcaoTributo() { acoesTributo--; }

	boolean isKhanPendente() { return khanPendente; }
	void setKhanPendente(boolean b) { khanPendente = b; }

	boolean isRecuperar() { return recuperar; }
	void setRecuperar(boolean b) { recuperar = b; }

	Set<String> getParadasVisitadas() { return paradasVisitadas; }
	void visitar(String parada) { paradasVisitadas.add(parada); }
	boolean visitou(String parada) { return paradasVisitadas.contains(parada); }

	int getVirtuais(TipoTributo t) { return virtuais.getOrDefault(t, 0); }
	void adicionarVirtual(TipoTributo t, int n) { virtuais.merge(t, n, Integer::sum); }
	void gastarVirtual(TipoTributo t, int n) { virtuais.put(t, getVirtuais(t) - n); }

	List<TipoTesouro> getTesourosVirtuais() { return tesourosVirtuais; }

	int getTesourosTomados(String cidade) { return tesourosTomados.getOrDefault(cidade, 0); }
	void registrarTesouroTomado(String cidade) { tesourosTomados.merge(cidade, 1, Integer::sum); }

	int getMelhoriasGratuitasPendentes() { return melhoriasGratuitasPendentes; }
	void adicionarMelhoriaGratuita() { melhoriasGratuitasPendentes++; }
	void consumirMelhoriaGratuita() { melhoriasGratuitasPendentes--; }

	List<TipoTesouro> getOpcoesTesouroBonus() { return opcoesTesouroBonus; }
}
