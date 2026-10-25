package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cria as 14 cartas de conselheiro aberto (manual, pág. 18) e as 10 cartas de
 * conselheiro secreto (pág. 19). Para criar uma carta nova basta acrescentar
 * uma linha aqui.
 */
final class FabricaCartas {

	private FabricaCartas() {}

	static List<CartaConselheiroAberto> cartasAbertas() {
		List<CartaConselheiroAberto> c = new ArrayList<>();
		c.add(new CartaConselheiroAberto("Ter 4 espadas",
				(j, g) -> j.getTributos(TipoTributo.ESPADA) >= 4));
		c.add(new CartaConselheiroAberto("Ter 6 yurts no mapa",
				(j, g) -> g.getTabuleiro().yurtsNoMapa(j.getCor()) >= 6));
		c.add(new CartaConselheiroAberto("Ter 10 yurts no mapa",
				(j, g) -> g.getTabuleiro().yurtsNoMapa(j.getCor()) >= 10));
		c.add(new CartaConselheiroAberto("Ter 4 tesouros",
				(j, g) -> j.getTotalTesouros() >= 4));
		c.add(new CartaConselheiroAberto("Ter 5 tesouros diferentes",
				(j, g) -> j.getTiposDiferentesTesouros() >= 5));
		c.add(new CartaConselheiroAberto("Ter 1 yurt em cada região",
				(j, g) -> yurtsEmCadaRegiao(j, g) >= 1));
		c.add(new CartaConselheiroAberto("Ter 2 yurts em cada região",
				(j, g) -> yurtsEmCadaRegiao(j, g) >= 2));
		c.add(new CartaConselheiroAberto("Conseguir 2 peças de bônus",
				(j, g) -> j.getBonus().size() >= 2));
		c.add(new CartaConselheiroAberto("Conseguir 3 peças de bônus",
				(j, g) -> j.getBonus().size() >= 3));
		c.add(new CartaConselheiroAberto("Conquistar 2 cidades",
				(j, g) -> j.getCidadesConquistadas() >= 2));
		c.add(new CartaConselheiroAberto("Conquistar 3 cidades",
				(j, g) -> j.getCidadesConquistadas() >= 3));
		c.add(new CartaConselheiroAberto("Completar uma linha de melhorias",
				(j, g) -> j.getTabuleiro().linhasCompletas() >= 1));
		c.add(new CartaConselheiroAberto("Ter 2 colunas com 2 melhorias cada",
				(j, g) -> colunasComPeloMenos(j, 2) >= 2));
		c.add(new CartaConselheiroAberto("Ter 4 melhorias em uma só coluna",
				(j, g) -> colunasComPeloMenos(j, 4) >= 1));
		return c;
	}

	static List<CartaConselheiroSecreto> cartasSecretas() {
		List<CartaConselheiroSecreto> c = new ArrayList<>();
		c.add(new CartaConselheiroSecreto("Viajante", new int[] { 3, 6, 8 },
				(j, g) -> g.getTabuleiro().maiorGrupoConectado(j.getCor())));
		c.add(new CartaConselheiroSecreto("Soldado", new int[] { 4, 7, 10 },
				(j, g) -> j.getTabuleiro().todasMelhorias().size()));
		c.add(new CartaConselheiroSecreto("Colecionador", new int[] { 4, 6, 7 },
				(j, g) -> contarSimbolos(j).size()));
		c.add(new CartaConselheiroSecreto("Sacerdote", new int[] { 1, 2, 4 },
				(j, g) -> contarPares(j)));
		c.add(new CartaConselheiroSecreto("Governador", new int[] { 1, 2, 3 },
				(j, g) -> regioesGovernadas(j, g)));
		c.add(new CartaConselheiroSecreto("Bajulador", new int[] { 2, 3, 4 },
				(j, g) -> g.getTabuleiro().provinciasDoKhanComYurtsAdjacentes(j.getCor(), 2)));
		c.add(new CartaConselheiroSecreto("Prefeito", new int[] { 2, 3, 4 },
				(j, g) -> g.getTabuleiro().cidadesComYurtsAdjacentes(j.getCor(), 2)));
		c.add(new CartaConselheiroSecreto("Cidadão", new int[] { 3, 6, 8 },
				(j, g) -> g.getTabuleiro().cidadesComYurtsAdjacentes(j.getCor(), 1)));
		c.add(new CartaConselheiroSecreto("Cavaleiro", new int[] { 2, 4, 5 },
				(j, g) -> contarCavaloOuPunho(j)));
		c.add(new CartaConselheiroSecreto("Mensageiro", new int[] { 2, 4, 6 },
				(j, g) -> g.getTabuleiro().paradasDuplasComYurt(j.getCor())));
		return c;
	}

	/** Procura uma carta de conselheiro aberto pela descrição (null se não existir). */
	static CartaConselheiroAberto cartaAberta(String descricao) {
		for (CartaConselheiroAberto c : cartasAbertas())
			if (c.getDescricao().equals(descricao)) return c;
		return null;
	}

	/** Procura uma carta de conselheiro secreto pelo nome (null se não existir). */
	static CartaConselheiroSecreto cartaSecreta(String nome) {
		for (CartaConselheiroSecreto c : cartasSecretas())
			if (c.getNome().equals(nome)) return c;
		return null;
	}

	// ----------------- medidas auxiliares -----------------

	/** Menor número de yurts do jogador entre as 3 regiões. */
	static int yurtsEmCadaRegiao(Jogador j, Jogo g) {
		int min = Integer.MAX_VALUE;
		for (Regiao r : Regiao.values())
			min = Math.min(min, g.getTabuleiro().yurtsNaRegiao(j.getCor(), r));
		return min;
	}

	static int colunasComPeloMenos(Jogador j, int n) {
		int cont = 0;
		for (ColunaJogador c : j.getTabuleiro().getColunas())
			if (c.getQuantidadeMelhorias() >= n) cont++;
		return cont;
	}

	/** Símbolo da melhoria -> quantidade (tesouros de tipos diferentes são símbolos diferentes). */
	static Map<String, Integer> contarSimbolos(Jogador j) {
		Map<String, Integer> cont = new HashMap<>();
		for (Melhoria m : j.getTabuleiro().todasMelhorias()) cont.merge(m.simbolo(), 1, Integer::sum);
		return cont;
	}

	/** Número de pares de melhorias com o mesmo símbolo. */
	static int contarPares(Jogador j) {
		int pares = 0;
		for (int q : contarSimbolos(j).values()) pares += q / 2;
		return pares;
	}

	/** Regiões com pelo menos 2 yurts do jogador e 2 melhorias na coluna associada. */
	static int regioesGovernadas(Jogador j, Jogo g) {
		int n = 0;
		for (Regiao r : Regiao.values()) {
			boolean yurts = g.getTabuleiro().yurtsNaRegiao(j.getCor(), r) >= 2;
			boolean melhorias = j.getTabuleiro().getColunaDaRegiao(r).getQuantidadeMelhorias() >= 2;
			if (yurts && melhorias) n++;
		}
		return n;
	}

	static int contarCavaloOuPunho(Jogador j) {
		int n = 0;
		for (Melhoria m : j.getTabuleiro().todasMelhorias()) if (m.temCavaloOuPunho()) n++;
		return n;
	}
}
