package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Núcleo das regras de Herdeiros do Khan. Guarda o estado da partida e
 * valida/executa as ações do jogador da vez.
 *
 * Convenção: cada ação retorna {@code true} se foi executada. Se a ação violar
 * uma regra, nada é alterado, o método retorna {@code false} e o motivo fica
 * disponível em {@link #getUltimoErro()}.
 */
class Jogo {
	/** Identificador usado para a área de melhorias como destino do Khan. */
	static final String AREA_MELHORIAS = "AREA_MELHORIAS";
	static final int CIDADES_REVELADAS = 3;
	static final int CARTAS_ABERTAS_REVELADAS = 4;
	static final int CARTAS_SECRETAS_POR_JOGADOR = 2;
	static final int CUSTO_MELHORIA = 2;

	/** Votos de influência por número de melhorias na coluna (índice 0 a 4). */
	static final int[] VOTOS_MAIS_YURTS = { 1, 1, 2, 2, 3 };
	static final int[] VOTOS_SEGUNDO_MAIS_YURTS = { 0, 1, 1, 2, 2 };

	private final Tabuleiro tabuleiro;
	private final Random random;
	private final List<Jogador> jogadores = new ArrayList<>();
	private final Map<CorJogador, Jogador> jogadorPorCor = new HashMap<>();

	private final List<Cidade> pilhaCidades = new ArrayList<>();
	private final List<Cidade> cidadesDisponiveis = new ArrayList<>();
	private final List<TipoTesouro> pilhaTesouros = new ArrayList<>();
	private final List<CartaConselheiroAberto> cartasAbertas = new ArrayList<>();
	private final MercadoMelhorias mercado;
	private final TrilhaKurultai trilha;

	private String posicaoKhan;          // null = fora do tabuleiro
	private int indiceJogadorAtual;
	private int numeroTurno = 1;
	private Turno turno;

	private boolean fimDisparado;
	private boolean rodadaFinal;
	private boolean terminado;
	private String ultimoErro;

	// =====================================================================
	// Preparação do jogo (manual, págs. 3 e 4)
	// =====================================================================

	/**
	 * Prepara uma partida.
	 * @param tabuleiro mapa já montado
	 * @param cores cores dos jogadores, na ordem de jogo
	 * @param herdeiros herdeiro de cada jogador (mesma ordem de {@code cores})
	 * @param random gerador usado para embaralhar (permite partidas reproduzíveis nos testes)
	 */
	Jogo(Tabuleiro tabuleiro, List<CorJogador> cores, List<Herdeiro> herdeiros, Random random) {
		if (cores.size() < 2 || cores.size() > 5)
			throw new IllegalArgumentException("O jogo é para 2 a 5 jogadores");
		if (cores.size() != herdeiros.size())
			throw new IllegalArgumentException("Cada jogador precisa de um herdeiro");
		if (new HashSet<>(cores).size() != cores.size() || new HashSet<>(herdeiros).size() != herdeiros.size())
			throw new IllegalArgumentException("Cores e herdeiros não podem se repetir");
		if (tabuleiro.getKarakorum() == null)
			throw new IllegalArgumentException("O mapa precisa de Karakorum");

		this.tabuleiro = tabuleiro;
		this.random = random;

		// jogadores: peão em Karakorum; 1 moeda para o 1o e o 2o, 2 moedas para os demais
		for (int i = 0; i < cores.size(); i++) {
			Jogador j = new Jogador(cores.get(i), herdeiros.get(i));
			j.setPosicao(tabuleiro.getKarakorum());
			j.adicionarTributo(TipoTributo.MOEDA, i < 2 ? 1 : 2);
			jogadores.add(j);
			jogadorPorCor.put(j.getCor(), j);
		}

		// tesouros
		for (TipoTesouro t : TipoTesouro.values())
			for (int i = 0; i < TipoTesouro.QUANTIDADE_POR_TIPO; i++) pilhaTesouros.add(t);
		Collections.shuffle(pilhaTesouros, random);

		// cidades: embaralha e revela 3, cada uma com 4 tesouros
		pilhaCidades.addAll(ordenarCidades(tabuleiro.getCidades()));
		Collections.shuffle(pilhaCidades, random);
		for (int i = 0; i < CIDADES_REVELADAS; i++) revelarProximaCidade();

		// 1 tributo em cada província
		for (Provincia p : tabuleiro.getProvincias()) p.adicionarTributo();

		// uma peça de bônus aleatória em cada conselheiro (incluindo Karakorum)
		List<TipoBonus> bonus = new ArrayList<>(List.of(TipoBonus.values()));
		Collections.shuffle(bonus, random);
		int b = 0;
		for (Parada p : ordenarParadas(tabuleiro.getParadas()))
			if (p.getConselheiro() != null && b < bonus.size())
				p.getConselheiro().setBonus(new PecaBonus(bonus.get(b++)));

		// 4 cartas de conselheiro aberto
		List<CartaConselheiroAberto> abertas = FabricaCartas.cartasAbertas();
		Collections.shuffle(abertas, random);
		cartasAbertas.addAll(abertas.subList(0, CARTAS_ABERTAS_REVELADAS));

		// 2 cartas de conselheiro secreto para cada jogador
		List<CartaConselheiroSecreto> secretas = FabricaCartas.cartasSecretas();
		Collections.shuffle(secretas, random);
		int s = 0;
		for (Jogador j : jogadores)
			for (int k = 0; k < CARTAS_SECRETAS_POR_JOGADOR; k++) j.receberCartaSecreta(secretas.get(s++));

		// melhorias: embaralha e revela 5
		List<Melhoria> melhorias = criarMelhorias();
		Collections.shuffle(melhorias, random);
		mercado = new MercadoMelhorias(melhorias);

		trilha = new TrilhaKurultai(jogadores.size());
		turno = new Turno(jogadores.get(0), numeroTurno);
	}

	/**
	 * Cria as 44 peças de melhoria. A distribuição das cores de fundo é
	 * provisória (alternada entre as regiões) até ser conferida nas peças reais.
	 */
	static List<Melhoria> criarMelhorias() {
		List<Melhoria> lista = new ArrayList<>();
		Regiao[] cores = Regiao.values();
		int k = 0;
		for (TipoMelhoria tipo : TipoMelhoria.values()) {
			if (tipo == TipoMelhoria.TESOURO_VIRTUAL) {
				for (TipoTesouro t : TipoTesouro.values())
					lista.add(new Melhoria(tipo, cores[k++ % cores.length], t));
			} else {
				for (int i = 0; i < tipo.getQuantidade(); i++)
					lista.add(new Melhoria(tipo, cores[k++ % cores.length]));
			}
		}
		return lista;
	}

	/** Ordena por nome para que a preparação não dependa da ordem interna do HashMap. */
	private static List<Cidade> ordenarCidades(java.util.Collection<Cidade> c) {
		List<Cidade> l = new ArrayList<>(c);
		l.sort((a, b) -> a.getNome().compareTo(b.getNome()));
		return l;
	}

	private static List<Parada> ordenarParadas(java.util.Collection<Parada> c) {
		List<Parada> l = new ArrayList<>(c);
		l.sort((a, b) -> a.getId().compareTo(b.getId()));
		return l;
	}

	/** Revela a próxima cidade da pilha e coloca 4 tesouros nela. */
	private void revelarProximaCidade() {
		if (pilhaCidades.isEmpty()) return;
		Cidade c = pilhaCidades.remove(0);
		for (int i = 0; i < Cidade.TESOUROS_INICIAIS && !pilhaTesouros.isEmpty(); i++)
			c.adicionarTesouro(pilhaTesouros.remove(pilhaTesouros.size() - 1));
		cidadesDisponiveis.add(c);
	}

	// =====================================================================
	// Consultas
	// =====================================================================

	Tabuleiro getTabuleiro() { return tabuleiro; }
	List<Jogador> getJogadores() { return Collections.unmodifiableList(jogadores); }
	Jogador getJogador(CorJogador cor) { return jogadorPorCor.get(cor); }
	Jogador getJogadorAtual() { return jogadores.get(indiceJogadorAtual); }
	Turno getTurno() { return turno; }
	int getNumeroTurno() { return numeroTurno; }
	List<Cidade> getCidadesDisponiveis() { return Collections.unmodifiableList(cidadesDisponiveis); }
	List<CartaConselheiroAberto> getCartasAbertas() { return Collections.unmodifiableList(cartasAbertas); }
	MercadoMelhorias getMercado() { return mercado; }
	TrilhaKurultai getTrilha() { return trilha; }
	String getPosicaoKhan() { return posicaoKhan; }
	int getTesourosNaPilha() { return pilhaTesouros.size(); }
	boolean isFimDisparado() { return fimDisparado; }
	boolean isRodadaFinal() { return rodadaFinal; }
	boolean isTerminado() { return terminado; }
	String getUltimoErro() { return ultimoErro; }

	/** Quantidade de um recurso que o jogador da vez pode gastar agora (real + virtual). */
	int disponivel(TipoTributo t) {
		return getJogadorAtual().getTributos(t) + turno.getVirtuais(t);
	}

	// =====================================================================
	// Ações do turno
	// =====================================================================

	/** Antes da 1a rodada: o jogador escolhe 1 das 2 cartas de conselheiro secreto. */
	boolean escolherCartaSecreta(CorJogador cor, int indice) {
		Jogador j = jogadorPorCor.get(cor);
		if (j == null) return falha("Jogador inexistente");
		if (!j.escolherCartaSecreta(indice)) return falha("Escolha de carta secreta inválida");
		ultimoErro = null;
		return true;
	}

	/**
	 * Coloca a peça de ativação numa coluna (manual, pág. 6). Libera as ações da
	 * base da coluna mais as das melhorias nela (exceto as compradas neste turno).
	 */
	boolean ativarColuna(int indice) {
		if (terminado) return falha("A partida terminou");
		if (turno.colunaFoiAtivada()) return falha("Já foi ativada uma coluna neste turno");
		if (indice < 0 || indice >= TabuleiroJogador.NUM_COLUNAS) return falha("Coluna inexistente");
		ColunaJogador col = getJogadorAtual().getTabuleiro().getColuna(indice);
		if (col.isAtivada()) return falha("Esta coluna já tem uma peça de ativação");

		col.ativar();
		turno.setColunaAtivada(indice);
		turno.adicionarMovimentos(col.getAcaoInicial(TipoAcao.MOVIMENTO));
		turno.adicionarAcoesTributo(col.getAcaoInicial(TipoAcao.PEGAR_TRIBUTO));
		turno.setKhanPendente(col.getAcaoInicial(TipoAcao.KHAN) > 0);
		turno.setRecuperar(col.getAcaoInicial(TipoAcao.RECUPERAR) > 0);

		for (Melhoria m : col.getMelhorias()) {
			if (m.getTurnoCompra() == numeroTurno) continue; // não vale no turno da compra
			switch (m.getTipo()) {
			case MOVIMENTO_DUPLO: turno.adicionarMovimentos(2); break;
			case PEGAR_TRIBUTO:   turno.adicionarAcoesTributo(1); break;
			case MOEDA_VIRTUAL:   turno.adicionarVirtual(TipoTributo.MOEDA, 1); break;
			case ESPADA_VIRTUAL:  turno.adicionarVirtual(TipoTributo.ESPADA, 1); break;
			case YURT_VIRTUAL:    turno.adicionarVirtual(TipoTributo.YURT, 1); break;
			case TESOURO_VIRTUAL: turno.getTesourosVirtuais().add(m.getTesouro()); break;
			}
		}
		return sucesso();
	}

	/**
	 * Move o peão pelo caminho dado (lista de paradas, sem a parada de origem).
	 * Regras (manual, pág. 7):
	 * <ul>
	 * <li>cada parada do caminho tem de ser vizinha da anterior;</li>
	 * <li>cada parada custa 1 movimento, exceto as intermediárias com yurt do
	 *     próprio jogador, que são puladas (não custam nada e não contam como visitadas);</li>
	 * <li>pode-se passar por paradas ocupadas, mas não terminar numa parada lotada
	 *     (simples: 1 peão; dupla: 2; Karakorum: todos).</li>
	 * </ul>
	 */
	boolean mover(List<String> caminho) {
		if (terminado) return falha("A partida terminou");
		if (caminho == null || caminho.isEmpty()) return falha("Caminho vazio");
		Jogador j = getJogadorAtual();

		String anterior = j.getPosicao().getId();
		int custo = 0;
		List<String> visitadas = new ArrayList<>();
		for (int i = 0; i < caminho.size(); i++) {
			String atual = caminho.get(i);
			Parada p = tabuleiro.getParada(atual);
			if (p == null) return falha("Parada inexistente: " + atual);
			if (!tabuleiro.saoVizinhas(anterior, atual))
				return falha(anterior + " e " + atual + " não estão ligadas por uma rota");
			boolean ultima = i == caminho.size() - 1;
			if (!ultima && p.temYurtDe(j.getCor())) {
				// pula o próprio yurt: não gasta movimento e não "esteve" na parada
			} else {
				custo++;
				visitadas.add(atual);
			}
			anterior = atual;
		}

		Parada destino = tabuleiro.getParada(anterior);
		if (peoesNaParada(destino, j) >= destino.getTipo().getCapacidadePeoes())
			return falha("Não é possível terminar o movimento em " + destino.getId() + ": parada ocupada");
		if (custo > turno.getMovimentos())
			return falha("Movimentos insuficientes: precisa de " + custo + ", tem " + turno.getMovimentos());

		turno.gastarMovimentos(custo);
		for (String v : visitadas) turno.visitar(v);
		j.setPosicao(destino);
		return sucesso();
	}

	/** Peões de outros jogadores numa parada. */
	private int peoesNaParada(Parada p, Jogador ignorar) {
		int n = 0;
		for (Jogador o : jogadores) if (o != ignorar && o.getPosicao() == p) n++;
		return n;
	}

	/** O jogador esteve, neste turno, numa parada adjacente à província? */
	private boolean visitouAdjacenteAProvincia(String provincia) {
		for (String p : turno.getParadasVisitadas())
			if (tabuleiro.provinciasAdjacentes(p).contains(provincia)) return true;
		return false;
	}

	private boolean visitouAdjacenteACidade(String cidade) {
		for (String p : turno.getParadasVisitadas())
			if (tabuleiro.cidadesAdjacentes(p).contains(cidade)) return true;
		return false;
	}

	/**
	 * Pega 1 peça de tributo de uma província adjacente a uma parada em que o
	 * jogador esteve neste turno (manual, pág. 8).
	 */
	boolean pegarTributo(String provinciaId) {
		if (terminado) return falha("A partida terminou");
		if (turno.getAcoesTributo() <= 0) return falha("Não há ações de pegar tributo disponíveis");
		Provincia prov = tabuleiro.getProvincia(provinciaId);
		if (prov == null) return falha("Província inexistente: " + provinciaId);
		if (prov.getTributos() == 0) return falha("A província está vazia");
		if (!visitouAdjacenteAProvincia(provinciaId))
			return falha("A província não é adjacente a nenhuma parada em que você esteve neste turno");

		prov.retirarTributo();
		getJogadorAtual().adicionarTributo(prov.getTipoTributo(), 1);
		turno.gastarAcaoTributo();
		return sucesso();
	}

	/**
	 * Usa o Khan (ação obrigatória quando a coluna tem o ícone; manual, pág. 9).
	 * Destinos: uma das 4 províncias do Khan ou {@link #AREA_MELHORIAS}; o Khan não
	 * pode ficar onde está.
	 */
	boolean moverKhan(String destino) {
		if (terminado) return falha("A partida terminou");
		if (!turno.isKhanPendente()) return falha("A coluna ativada não tem a ação do Khan");
		if (destino == null) return falha("Destino do Khan inválido");
		if (destino.equals(posicaoKhan)) return falha("O Khan não pode permanecer onde está");

		if (destino.equals(AREA_MELHORIAS)) {
			Jogador j = getJogadorAtual();
			if (mercado.espacoVazio(MercadoMelhorias.ESPACO_ICONE_MOEDA)) j.adicionarTributo(TipoTributo.MOEDA, 1);
			if (mercado.espacoVazio(MercadoMelhorias.ESPACO_ICONE_ESPADA)) j.adicionarTributo(TipoTributo.ESPADA, 1);
			mercado.reabastecer();
		} else {
			Provincia p = tabuleiro.getProvincia(destino);
			if (p == null || !p.isProvinciaDoKhan()) return falha("Destino do Khan inválido: " + destino);
			p.adicionarTributo();
			for (String alvo : tabuleiro.setasDoKhan(destino)) tabuleiro.getProvincia(alvo).adicionarTributo();
		}
		posicaoKhan = destino;
		turno.setKhanPendente(false);
		return sucesso();
	}

	/** Paga com recursos virtuais primeiro (eles se perdem no fim do turno) e depois com os reais. */
	private void pagar(TipoTributo t, int qtd) {
		int virt = Math.min(qtd, turno.getVirtuais(t));
		turno.gastarVirtual(t, virt);
		getJogadorAtual().gastarTributo(t, qtd - virt);
	}

	/**
	 * Ataca uma cidade adjacente a uma parada em que o jogador esteve, tomando o
	 * tesouro escolhido (manual, pág. 10). O 1o tesouro de uma cidade no turno custa
	 * 1 espada; cada tesouro seguinte da mesma cidade custa 2. Quem toma o último
	 * tesouro conquista a cidade.
	 */
	boolean atacarCidade(String nomeCidade, TipoTesouro tesouro) {
		if (terminado) return falha("A partida terminou");
		Cidade c = tabuleiro.getCidade(nomeCidade);
		if (c == null || !cidadesDisponiveis.contains(c)) return falha("Cidade não disponível para ataque");
		if (!c.getTesouros().contains(tesouro)) return falha("A cidade não tem esse tesouro");
		if (!visitouAdjacenteACidade(nomeCidade))
			return falha("A cidade não é adjacente a nenhuma parada em que você esteve neste turno");
		int custo = turno.getTesourosTomados(nomeCidade) == 0 ? 1 : 2;
		if (disponivel(TipoTributo.ESPADA) < custo) return falha("Espadas insuficientes: o ataque custa " + custo);

		Jogador j = getJogadorAtual();
		pagar(TipoTributo.ESPADA, custo);
		c.retirarTesouro(tesouro);
		j.adicionarTesouro(tesouro);
		turno.registrarTesouroTomado(nomeCidade);

		if (c.getTesouros().isEmpty()) {
			// conquista: yurt no meio da cidade e revela a próxima
			c.conquistar(j.getCor());
			j.retirarYurtDaReserva();
			j.registrarConquista();
			cidadesDisponiveis.remove(c);
			revelarProximaCidade();
			if (cidadesDisponiveis.isEmpty()) fimDisparado = true; // todas conquistadas
		}
		return sucesso();
	}

	/**
	 * Constrói um yurt numa parada em que o jogador esteve neste turno (manual,
	 * pág. 11). Custa 1 tributo de yurt. Karakorum não aceita yurts; paradas
	 * simples aceitam 1 e paradas duplas 2.
	 */
	boolean construirYurt(String paradaId) {
		if (terminado) return falha("A partida terminou");
		Parada p = tabuleiro.getParada(paradaId);
		if (p == null) return falha("Parada inexistente: " + paradaId);
		if (!turno.visitou(paradaId)) return falha("Você não esteve nesta parada neste turno");
		if (!p.temEspacoParaYurt()) return falha("A parada não comporta mais yurts");
		Jogador j = getJogadorAtual();
		if (j.getYurtsNaReserva() == 0) return falha("Suas peças de yurt acabaram");
		if (disponivel(TipoTributo.YURT) < 1) return falha("É preciso 1 tributo de yurt");

		pagar(TipoTributo.YURT, 1);
		j.retirarYurtDaReserva();
		p.adicionarYurt(j.getCor());
		return sucesso();
	}

	/**
	 * Compra uma melhoria por 2 moedas (manual, pág. 11) e a coloca na coluna da
	 * sua cor. {@code espaco} é a posição na área de melhorias (0 a 4) ou -1 para
	 * o topo da pilha. Completar uma linha dá direito a uma melhoria gratuita na
	 * coluna branca (ver {@link #pegarMelhoriaGratuita(int)}).
	 */
	boolean comprarMelhoria(int espaco) {
		if (terminado) return falha("A partida terminou");
		if (turno.getMelhoriasGratuitasPendentes() > 0) return falha("Escolha antes a melhoria gratuita");
		if (disponivel(TipoTributo.MOEDA) < CUSTO_MELHORIA) return falha("São necessárias 2 moedas");
		Melhoria m = verMelhoria(espaco);
		if (m == null) return falha("Não há melhoria nesse espaço");
		TabuleiroJogador tab = getJogadorAtual().getTabuleiro();
		if (!tab.podeReceber(m)) return falha("A coluna " + m.getCor() + " está cheia");

		pagar(TipoTributo.MOEDA, CUSTO_MELHORIA);
		retirarMelhoria(espaco);
		m.setTurnoCompra(numeroTurno);
		tab.getColunaDaRegiao(m.getCor()).adicionarMelhoria(m);
		int novas = tab.registrarNovasLinhasCompletas();
		for (int i = 0; i < novas; i++)
			if (tab.getColunaBranca().temEspaco()) turno.adicionarMelhoriaGratuita();
		return sucesso();
	}

	/** Coloca na coluna branca a melhoria gratuita ganha ao completar uma linha (qualquer cor). */
	boolean pegarMelhoriaGratuita(int espaco) {
		if (terminado) return falha("A partida terminou");
		if (turno.getMelhoriasGratuitasPendentes() == 0) return falha("Não há melhoria gratuita a receber");
		Melhoria m = verMelhoria(espaco);
		if (m == null) return falha("Não há melhoria nesse espaço");
		ColunaJogador branca = getJogadorAtual().getTabuleiro().getColunaBranca();
		retirarMelhoria(espaco);
		m.setTurnoCompra(numeroTurno);
		branca.adicionarMelhoria(m);
		turno.consumirMelhoriaGratuita();
		return sucesso();
	}

	private Melhoria verMelhoria(int espaco) {
		if (espaco == -1) return mercado.verTopo();
		if (espaco < 0 || espaco >= MercadoMelhorias.NUM_ESPACOS) return null;
		return mercado.verEspaco(espaco);
	}

	private Melhoria retirarMelhoria(int espaco) {
		return espaco == -1 ? mercado.retirarDoTopo() : mercado.retirarDoEspaco(espaco);
	}

	/**
	 * Entrega tesouros a um conselheiro numa parada em que o jogador esteve neste
	 * turno (manual, págs. 12 e 13). O primeiro a entregar recebe a peça de bônus.
	 * Um tesouro virtual (melhoria) pode substituir um tesouro numa única entrega.
	 */
	boolean entregarTesouros(String paradaId, List<TipoTesouro> oferta) {
		if (terminado) return falha("A partida terminou");
		Parada p = tabuleiro.getParada(paradaId);
		if (p == null || p.getConselheiro() == null) return falha("Não há conselheiro nesta parada");
		if (!turno.visitou(paradaId)) return falha("Você não esteve nesta parada neste turno");
		Conselheiro cons = p.getConselheiro();
		if (!cons.aceita(oferta)) return falha("Os tesouros não atendem à demanda do conselheiro");

		Jogador j = getJogadorAtual();
		Map<TipoTesouro, Integer> pedidos = new HashMap<>();
		for (TipoTesouro t : oferta) pedidos.merge(t, 1, Integer::sum);
		TipoTesouro virtualUsado = null;
		for (Map.Entry<TipoTesouro, Integer> e : pedidos.entrySet()) {
			int falta = e.getValue() - j.getTesouros(e.getKey());
			if (falta <= 0) continue;
			if (falta == 1 && virtualUsado == null && turno.getTesourosVirtuais().contains(e.getKey()))
				virtualUsado = e.getKey();
			else
				return falha("Você não possui os tesouros oferecidos");
		}

		// executa a entrega: tira os tesouros reais e, se for o caso, o virtual
		for (Map.Entry<TipoTesouro, Integer> e : pedidos.entrySet()) {
			int reais = e.getKey() == virtualUsado ? e.getValue() - 1 : e.getValue();
			for (int i = 0; i < reais; i++) j.retirarTesouro(e.getKey());
		}
		if (virtualUsado != null) turno.getTesourosVirtuais().remove(virtualUsado);
		ganharVotos(j, cons.getVotos());
		PecaBonus bonus = cons.recolherBonus();
		if (bonus != null) {
			j.adicionarBonus(bonus);
			if (bonus.getTipo() == TipoBonus.VOTO_EXTRA) { // efeito imediato
				bonus.marcarUsada();
				ganharVotos(j, 1);
			}
		}
		return sucesso();
	}

	/** Usa uma peça de bônus guardada (manual, pág. 17). */
	boolean usarBonus(TipoBonus tipo) {
		if (terminado) return falha("A partida terminou");
		Jogador j = getJogadorAtual();
		PecaBonus b = j.getBonusDisponivel(tipo);
		if (b == null) return falha("Você não tem essa peça de bônus disponível");
		switch (tipo) {
		case DUAS_ESPADAS: j.adicionarTributo(TipoTributo.ESPADA, 2); break;
		case DUAS_MOEDAS:  j.adicionarTributo(TipoTributo.MOEDA, 2); break;
		case DOIS_YURTS:   j.adicionarTributo(TipoTributo.YURT, 2); break;
		case TRES_MOVIMENTOS_E_TRIBUTO:
			turno.adicionarMovimentos(3);
			turno.adicionarAcoesTributo(1);
			break;
		case DOIS_TRIBUTOS: turno.adicionarAcoesTributo(2); break;
		case TRES_TESOUROS:
			if (pilhaTesouros.isEmpty()) return falha("A pilha de tesouros está vazia");
			for (int i = 0; i < 3 && !pilhaTesouros.isEmpty(); i++)
				turno.getOpcoesTesouroBonus().add(pilhaTesouros.remove(pilhaTesouros.size() - 1));
			break;
		default: return falha("Este bônus tem efeito imediato");
		}
		b.marcarUsada();
		return sucesso();
	}

	/** Escolhe 1 dos 3 tesouros sorteados pelo bônus; os outros são descartados. */
	boolean escolherTesouroBonus(TipoTesouro escolhido) {
		List<TipoTesouro> opcoes = turno.getOpcoesTesouroBonus();
		if (opcoes.isEmpty()) return falha("Não há tesouros para escolher");
		if (!opcoes.contains(escolhido)) return falha("Esse tesouro não está entre as opções");
		getJogadorAtual().adicionarTesouro(escolhido);
		opcoes.clear();
		return sucesso();
	}

	/**
	 * Encerra o turno do jogador da vez e passa para o próximo (sentido horário).
	 * Controla também a rodada final depois que o fim do jogo é disparado
	 * (manual, pág. 14).
	 */
	boolean encerrarTurno() {
		if (terminado) return falha("A partida terminou");
		if (!turno.colunaFoiAtivada()) return falha("É preciso ativar uma coluna no turno");
		if (turno.isKhanPendente()) return falha("Usar o Khan é obrigatório");
		if (turno.getMelhoriasGratuitasPendentes() > 0 && temMelhoriaParaPegar())
			return falha("Escolha a melhoria gratuita antes de encerrar");
		if (!turno.getOpcoesTesouroBonus().isEmpty()) return falha("Escolha o tesouro do bônus");

		if (turno.isRecuperar()) getJogadorAtual().getTabuleiro().recuperarTodas();

		indiceJogadorAtual = (indiceJogadorAtual + 1) % jogadores.size();
		if (indiceJogadorAtual == 0 && fimDisparado) {
			if (rodadaFinal) terminado = true;  // acabou a rodada final
			else rodadaFinal = true;            // termina a rodada atual e começa a final
		}
		numeroTurno++;
		turno = new Turno(getJogadorAtual(), numeroTurno);
		ultimoErro = null;
		return true;
	}

	private boolean temMelhoriaParaPegar() {
		if (mercado.verTopo() != null) return true;
		for (Melhoria m : mercado.getEspacos()) if (m != null) return true;
		return false;
	}

	// =====================================================================
	// Votos, cartas abertas e fim de jogo
	// =====================================================================

	/** Troca as cartas de conselheiro aberto em jogo (usado para montar cenários de teste). */
	void substituirCartasAbertas(List<CartaConselheiroAberto> novas) {
		cartasAbertas.clear();
		cartasAbertas.addAll(novas);
	}

	void ganharVotos(Jogador j, int qtd) {
		if (trilha.adicionarVotos(j.getCor(), qtd)) fimDisparado = true;
	}

	/**
	 * Dá 1 voto ao jogador da vez para cada carta de conselheiro aberto cuja
	 * condição ele cumpra agora. A carta sai do jogo e não é reposta.
	 */
	void verificarCartasAbertas() {
		Jogador j = getJogadorAtual();
		for (CartaConselheiroAberto c : new ArrayList<>(cartasAbertas)) {
			if (c.cumprida(j, this)) {
				cartasAbertas.remove(c);
				ganharVotos(j, CartaConselheiroAberto.VOTOS);
			}
		}
	}

	/** Votos de influência sobre as 3 regiões (manual, págs. 14 e 15). */
	int votosDeInfluencia(Jogador j) {
		int total = 0;
		for (Regiao r : Regiao.values()) {
			Map<CorJogador, Integer> yurts = new HashMap<>();
			for (Jogador o : jogadores) yurts.put(o.getCor(), tabuleiro.yurtsNaRegiao(o.getCor(), r));
			int meus = yurts.get(j.getCor());
			if (meus == 0) continue;

			int maior = Collections.max(yurts.values());
			int empatadosNoMaior = 0;
			for (int y : yurts.values()) if (y == maior) empatadosNoMaior++;
			int segundo = 0;
			if (empatadosNoMaior == 1) // empate no 1o lugar: ninguém fica em 2o
				for (int y : yurts.values()) if (y < maior) segundo = Math.max(segundo, y);

			int melhorias = j.getTabuleiro().getColunaDaRegiao(r).getQuantidadeMelhorias();
			if (meus == maior) total += VOTOS_MAIS_YURTS[melhorias];
			else if (meus == segundo) total += VOTOS_SEGUNDO_MAIS_YURTS[melhorias];
		}
		return total;
	}

	/** Votos finais: trilha do Kurultai + influência + carta de conselheiro secreto. */
	Map<CorJogador, Integer> calcularVotosFinais() {
		Map<CorJogador, Integer> votos = new HashMap<>();
		for (Jogador j : jogadores) {
			int v = trilha.getVotos(j.getCor()) + votosDeInfluencia(j);
			if (j.getCartaSecreta() != null) v += j.getCartaSecreta().votos(j, this);
			votos.put(j.getCor(), v);
		}
		return votos;
	}

	/**
	 * Vencedor(es): mais votos; desempate por mais tesouros não entregues e depois
	 * pelo peão mais próximo de Karakorum. Persistindo o empate, todos vencem.
	 */
	List<CorJogador> vencedores() {
		Map<CorJogador, Integer> votos = calcularVotosFinais();
		List<Jogador> candidatos = new ArrayList<>(jogadores);
		candidatos = filtrarMelhores(candidatos, j -> votos.get(j.getCor()));
		candidatos = filtrarMelhores(candidatos, Jogador::getTotalTesouros);
		String kara = tabuleiro.getKarakorum().getId();
		candidatos = filtrarMelhores(candidatos, j -> -tabuleiro.distancia(j.getPosicao().getId(), kara));
		List<CorJogador> res = new ArrayList<>();
		for (Jogador j : candidatos) res.add(j.getCor());
		return res;
	}

	private static List<Jogador> filtrarMelhores(List<Jogador> l, java.util.function.ToIntFunction<Jogador> f) {
		int melhor = Integer.MIN_VALUE;
		for (Jogador j : l) melhor = Math.max(melhor, f.applyAsInt(j));
		List<Jogador> res = new ArrayList<>();
		for (Jogador j : l) if (f.applyAsInt(j) == melhor) res.add(j);
		return res;
	}

	// =====================================================================

	private boolean falha(String motivo) {
		ultimoErro = motivo;
		return false;
	}

	/** Toda ação bem-sucedida verifica as cartas de conselheiro aberto. */
	private boolean sucesso() {
		ultimoErro = null;
		verificarCartasAbertas();
		return true;
	}
}
