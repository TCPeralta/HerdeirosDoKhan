package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * API pública do componente Model. É a ÚNICA classe pública do pacote: View e
 * Controller só conversam com o Model por aqui, usando tipos simples (String,
 * int, boolean), de modo que as classes internas podem mudar sem afetá-los.
 *
 * Nos parâmetros, cores, herdeiros, tesouros, tributos e bônus são passados
 * pelo nome da constante (por exemplo "AZUL", "TOLUI", "PELE", "ESPADA").
 *
 * Toda ação retorna true se foi executada; se não, getMensagemErro() explica o
 * motivo. (Os padrões Singleton e Façade serão aplicados na 3a iteração.)
 */
public class ModelAPI {
	private Jogo jogo;
	private String erro;

	// ===================== partida =====================

	/** Cria uma partida com o mapa padrão. Os jogadores jogam na ordem dos vetores. */
	public boolean novaPartida(String[] cores, String[] herdeiros) {
		return novaPartida(cores, herdeiros, new Random().nextLong());
	}

	/** Igual a {@link #novaPartida(String[], String[])}, com semente fixa para o embaralhamento. */
	public boolean novaPartida(String[] cores, String[] herdeiros, long semente) {
		try {
			List<CorJogador> c = new ArrayList<>();
			for (String s : cores) c.add(CorJogador.valueOf(s));
			List<Herdeiro> h = new ArrayList<>();
			for (String s : herdeiros) h.add(Herdeiro.valueOf(s));
			jogo = new Jogo(MapaPadrao.criar(), c, h, new Random(semente));
			erro = null;
			return true;
		} catch (IllegalArgumentException e) {
			erro = e.getMessage();
			return false;
		}
	}

	public String getMensagemErro() {
		return erro;
	}

	// ===================== consultas =====================

	public String getJogadorDaVez() { return jogo.getJogadorAtual().getCor().name(); }

	public int getNumeroJogadores() { return jogo.getJogadores().size(); }

	public String getPosicao(String cor) { return jogador(cor).getPosicao().getId(); }

	public int getTributos(String cor, String tipoTributo) {
		return jogador(cor).getTributos(TipoTributo.valueOf(tipoTributo));
	}

	public int getTesouros(String cor, String tipoTesouro) {
		return jogador(cor).getTesouros(TipoTesouro.valueOf(tipoTesouro));
	}

	public int getYurtsNaReserva(String cor) { return jogador(cor).getYurtsNaReserva(); }

	public int getVotos(String cor) { return jogo.getTrilha().getVotos(CorJogador.valueOf(cor)); }

	public int getFichaNeutraKurultai() { return jogo.getTrilha().getFichaNeutra(); }

	public int getMovimentosRestantes() { return jogo.getTurno().getMovimentos(); }

	public int getAcoesTributoRestantes() { return jogo.getTurno().getAcoesTributo(); }

	public boolean isKhanPendente() { return jogo.getTurno().isKhanPendente(); }

	public String getPosicaoKhan() { return jogo.getPosicaoKhan(); }

	public String[] getCidadesDisponiveis() {
		List<String> l = new ArrayList<>();
		for (Cidade c : jogo.getCidadesDisponiveis()) l.add(c.getNome());
		return l.toArray(new String[0]);
	}

	public String[] getCartasAbertas() {
		List<String> l = new ArrayList<>();
		for (CartaConselheiroAberto c : jogo.getCartasAbertas()) l.add(c.getDescricao());
		return l.toArray(new String[0]);
	}

	public boolean isFimDisparado() { return jogo.isFimDisparado(); }

	public boolean isPartidaTerminada() { return jogo.isTerminado(); }

	/** Votos totais no fim do jogo (trilha + influência + conselheiro secreto). */
	public int getVotosFinais(String cor) {
		return jogo.calcularVotosFinais().get(CorJogador.valueOf(cor));
	}

	public String[] getVencedores() {
		List<String> l = new ArrayList<>();
		for (CorJogador c : jogo.vencedores()) l.add(c.name());
		return l.toArray(new String[0]);
	}

	// ===================== ações =====================

	public boolean escolherCartaSecreta(String cor, int indice) {
		return resultado(jogo.escolherCartaSecreta(CorJogador.valueOf(cor), indice));
	}

	public boolean ativarColuna(int indice) { return resultado(jogo.ativarColuna(indice)); }

	public boolean mover(String... caminho) { return resultado(jogo.mover(Arrays.asList(caminho))); }

	public boolean pegarTributo(String provincia) { return resultado(jogo.pegarTributo(provincia)); }

	public boolean moverKhan(String destino) { return resultado(jogo.moverKhan(destino)); }

	/** Destino do Khan que representa a área de melhorias. */
	public static String areaDeMelhorias() { return Jogo.AREA_MELHORIAS; }

	public boolean atacarCidade(String cidade, String tesouro) {
		TipoTesouro t = converter(TipoTesouro.class, tesouro);
		return t != null && resultado(jogo.atacarCidade(cidade, t));
	}

	public boolean construirYurt(String parada) { return resultado(jogo.construirYurt(parada)); }

	/** @param espaco 0 a 4 na área de melhorias, ou -1 para o topo da pilha */
	public boolean comprarMelhoria(int espaco) { return resultado(jogo.comprarMelhoria(espaco)); }

	public boolean pegarMelhoriaGratuita(int espaco) { return resultado(jogo.pegarMelhoriaGratuita(espaco)); }

	public boolean entregarTesouros(String parada, String... tesouros) {
		List<TipoTesouro> l = new ArrayList<>();
		for (String s : tesouros) {
			TipoTesouro t = converter(TipoTesouro.class, s);
			if (t == null) return false;
			l.add(t);
		}
		return resultado(jogo.entregarTesouros(parada, l));
	}

	public boolean usarBonus(String tipo) {
		TipoBonus b = converter(TipoBonus.class, tipo);
		return b != null && resultado(jogo.usarBonus(b));
	}

	public boolean escolherTesouroBonus(String tesouro) {
		TipoTesouro t = converter(TipoTesouro.class, tesouro);
		return t != null && resultado(jogo.escolherTesouroBonus(t));
	}

	public boolean encerrarTurno() { return resultado(jogo.encerrarTurno()); }

	// ===================== auxiliares =====================

	private Jogador jogador(String cor) {
		return jogo.getJogador(CorJogador.valueOf(cor));
	}

	private boolean resultado(boolean ok) {
		erro = ok ? null : jogo.getUltimoErro();
		return ok;
	}

	private <E extends Enum<E>> E converter(Class<E> tipo, String nome) {
		try {
			return Enum.valueOf(tipo, nome);
		} catch (IllegalArgumentException | NullPointerException e) {
			erro = "Valor inválido: " + nome;
			return null;
		}
	}
}
