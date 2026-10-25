package model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * O tabuleiro principal (mapa): paradas ligadas por rotas, províncias com
 * tributos e cidades. É montado peça a peça pelos métodos "adicionar..." e
 * "ligar...", o que permite trocar o mapa (ou criar mapas de teste) sem alterar
 * as regras (princípio aberto/fechado).
 *
 * Todas as ligações são guardadas em HashMaps indexados pelo id do elemento.
 */
class Tabuleiro {
	private final Map<String, Parada> paradas = new HashMap<>();
	private final Map<String, Provincia> provincias = new HashMap<>();
	private final Map<String, Cidade> cidades = new HashMap<>();

	/** Rotas: parada -> paradas vizinhas. */
	private final Map<String, Set<String>> rotas = new HashMap<>();
	/** Parada -> províncias adjacentes. */
	private final Map<String, Set<String>> provinciasDaParada = new HashMap<>();
	/** Parada -> cidades adjacentes. */
	private final Map<String, Set<String>> cidadesDaParada = new HashMap<>();
	/** Província do Khan -> as 2 províncias indicadas pelas setas. */
	private final Map<String, List<String>> setasDoKhan = new HashMap<>();

	private Parada karakorum;

	// ======================= montagem =======================

	Parada adicionarParada(String id, TipoParada tipo, Regiao regiao) {
		if (paradas.containsKey(id)) throw new IllegalArgumentException("Parada repetida: " + id);
		Parada p = new Parada(id, tipo, regiao);
		paradas.put(id, p);
		rotas.put(id, new HashSet<>());
		provinciasDaParada.put(id, new HashSet<>());
		cidadesDaParada.put(id, new HashSet<>());
		if (tipo == TipoParada.KARAKORUM) karakorum = p;
		return p;
	}

	Provincia adicionarProvincia(String id, Regiao regiao, TipoTributo tipo, boolean doKhan) {
		Provincia p = new Provincia(id, regiao, tipo, doKhan);
		provincias.put(id, p);
		return p;
	}

	Cidade adicionarCidade(String nome, Regiao regiao) {
		Cidade c = new Cidade(nome, regiao);
		cidades.put(nome, c);
		return c;
	}

	/** Cria uma rota (mão dupla) entre duas paradas. */
	void ligarParadas(String a, String b) {
		exigeParada(a);
		exigeParada(b);
		rotas.get(a).add(b);
		rotas.get(b).add(a);
	}

	void ligarProvincia(String parada, String provincia) {
		exigeParada(parada);
		if (!provincias.containsKey(provincia)) throw new IllegalArgumentException("Província inexistente: " + provincia);
		provinciasDaParada.get(parada).add(provincia);
	}

	void ligarCidade(String parada, String cidade) {
		exigeParada(parada);
		if (!cidades.containsKey(cidade)) throw new IllegalArgumentException("Cidade inexistente: " + cidade);
		cidadesDaParada.get(parada).add(cidade);
	}

	void definirSetasDoKhan(String provinciaDoKhan, String alvo1, String alvo2) {
		setasDoKhan.put(provinciaDoKhan, List.of(alvo1, alvo2));
	}

	private void exigeParada(String id) {
		if (!paradas.containsKey(id)) throw new IllegalArgumentException("Parada inexistente: " + id);
	}

	// ======================= consultas =======================

	Parada getParada(String id) { return paradas.get(id); }
	Provincia getProvincia(String id) { return provincias.get(id); }
	Cidade getCidade(String nome) { return cidades.get(nome); }
	Parada getKarakorum() { return karakorum; }

	Collection<Parada> getParadas() { return Collections.unmodifiableCollection(paradas.values()); }
	Collection<Provincia> getProvincias() { return Collections.unmodifiableCollection(provincias.values()); }
	Collection<Cidade> getCidades() { return Collections.unmodifiableCollection(cidades.values()); }

	boolean saoVizinhas(String a, String b) {
		Set<String> v = rotas.get(a);
		return v != null && v.contains(b);
	}

	Set<String> vizinhas(String parada) { return Collections.unmodifiableSet(rotas.get(parada)); }

	Set<String> provinciasAdjacentes(String parada) {
		return Collections.unmodifiableSet(provinciasDaParada.get(parada));
	}

	Set<String> cidadesAdjacentes(String parada) {
		return Collections.unmodifiableSet(cidadesDaParada.get(parada));
	}

	List<String> setasDoKhan(String provincia) {
		return setasDoKhan.getOrDefault(provincia, List.of());
	}

	List<Provincia> provinciasDoKhan() {
		List<Provincia> l = new ArrayList<>();
		for (Provincia p : provincias.values()) if (p.isProvinciaDoKhan()) l.add(p);
		return l;
	}

	/** Distância (em número de rotas) entre duas paradas, ou -1 se não houver caminho. */
	int distancia(String origem, String destino) {
		Map<String, Integer> dist = new HashMap<>();
		Deque<String> fila = new ArrayDeque<>();
		dist.put(origem, 0);
		fila.add(origem);
		while (!fila.isEmpty()) {
			String atual = fila.poll();
			if (atual.equals(destino)) return dist.get(atual);
			for (String v : rotas.get(atual)) {
				if (!dist.containsKey(v)) {
					dist.put(v, dist.get(atual) + 1);
					fila.add(v);
				}
			}
		}
		return -1;
	}

	// ======================= yurts (usadas por cartas e pontuação) =======================

	/** Quantos yurts de uma cor existem numa parada. */
	private int yurtsNaParada(Parada p, CorJogador cor) {
		int n = 0;
		for (CorJogador c : p.getYurts()) if (c == cor) n++;
		return n;
	}

	/** Yurts do jogador no mapa: nas paradas e nas cidades conquistadas. */
	int yurtsNoMapa(CorJogador cor) {
		int n = 0;
		for (Parada p : paradas.values()) n += yurtsNaParada(p, cor);
		for (Cidade c : cidades.values()) if (c.getConquistador() == cor) n++;
		return n;
	}

	/** Yurts do jogador numa região (paradas da região + cidades conquistadas nela). */
	int yurtsNaRegiao(CorJogador cor, Regiao r) {
		int n = 0;
		for (Parada p : paradas.values()) if (p.getRegiao() == r) n += yurtsNaParada(p, cor);
		for (Cidade c : cidades.values()) if (c.getRegiao() == r && c.getConquistador() == cor) n++;
		return n;
	}

	/** Tamanho do maior grupo de yurts do jogador conectados por rotas (carta Viajante). */
	int maiorGrupoConectado(CorJogador cor) {
		Set<String> visitadas = new HashSet<>();
		int maior = 0;
		for (Parada inicio : paradas.values()) {
			if (!inicio.temYurtDe(cor) || visitadas.contains(inicio.getId())) continue;
			int tamanho = 0;
			Deque<String> pilha = new ArrayDeque<>();
			pilha.push(inicio.getId());
			visitadas.add(inicio.getId());
			while (!pilha.isEmpty()) {
				Parada atual = paradas.get(pilha.pop());
				tamanho += yurtsNaParada(atual, cor);
				for (String v : rotas.get(atual.getId())) {
					if (!visitadas.contains(v) && paradas.get(v).temYurtDe(cor)) {
						visitadas.add(v);
						pilha.push(v);
					}
				}
			}
			maior = Math.max(maior, tamanho);
		}
		return maior;
	}

	/** Quantas províncias do Khan têm pelo menos {@code minimo} yurts do jogador em paradas adjacentes. */
	int provinciasDoKhanComYurtsAdjacentes(CorJogador cor, int minimo) {
		Map<String, Integer> cont = new HashMap<>();
		for (Parada p : paradas.values()) {
			int y = yurtsNaParada(p, cor);
			if (y == 0) continue;
			for (String prov : provinciasDaParada.get(p.getId()))
				if (provincias.get(prov).isProvinciaDoKhan()) cont.merge(prov, y, Integer::sum);
		}
		return contarAtingidos(cont, minimo);
	}

	/**
	 * Quantas cidades têm pelo menos {@code minimo} yurts do jogador em paradas
	 * adjacentes. Yurts dentro de cidades não contam (cartas Prefeito e Cidadão).
	 */
	int cidadesComYurtsAdjacentes(CorJogador cor, int minimo) {
		Map<String, Integer> cont = new HashMap<>();
		for (Parada p : paradas.values()) {
			int y = yurtsNaParada(p, cor);
			if (y == 0) continue;
			for (String c : cidadesDaParada.get(p.getId())) cont.merge(c, y, Integer::sum);
		}
		return contarAtingidos(cont, minimo);
	}

	/** Paradas duplas com pelo menos 1 yurt do jogador (carta Mensageiro). */
	int paradasDuplasComYurt(CorJogador cor) {
		int n = 0;
		for (Parada p : paradas.values()) if (p.isDupla() && p.temYurtDe(cor)) n++;
		return n;
	}

	private static int contarAtingidos(Map<String, Integer> cont, int minimo) {
		int n = 0;
		for (int v : cont.values()) if (v >= minimo) n++;
		return n;
	}
}
