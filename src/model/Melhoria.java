package model;

/**
 * Peça de melhoria. Tem um tipo (ícone) e uma cor de fundo, que indica em qual
 * coluna colorida do tabuleiro de jogador ela pode ser colocada.
 */
class Melhoria {
	private final TipoMelhoria tipo;
	private final Regiao cor;           // cor de fundo = região da coluna
	private final TipoTesouro tesouro;  // só para TESOURO_VIRTUAL; null nos demais
	private int turnoCompra = -1;       // turno em que foi comprada (não pode ser usada nele)

	Melhoria(TipoMelhoria tipo, Regiao cor) {
		this(tipo, cor, null);
	}

	Melhoria(TipoMelhoria tipo, Regiao cor, TipoTesouro tesouro) {
		if (tipo == TipoMelhoria.TESOURO_VIRTUAL && tesouro == null)
			throw new IllegalArgumentException("Melhoria de tesouro virtual precisa de um tipo de tesouro");
		this.tipo = tipo;
		this.cor = cor;
		this.tesouro = tesouro;
	}

	TipoMelhoria getTipo() { return tipo; }
	Regiao getCor() { return cor; }
	TipoTesouro getTesouro() { return tesouro; }
	int getTurnoCompra() { return turnoCompra; }
	void setTurnoCompra(int turno) { this.turnoCompra = turno; }

	/**
	 * Identificação do símbolo, usada pelas cartas Colecionador e Sacerdote:
	 * tesouros virtuais de tipos diferentes contam como símbolos diferentes.
	 */
	String simbolo() {
		return tesouro == null ? tipo.name() : tipo.name() + ":" + tesouro.name();
	}

	/** Cavalo ou punho (usado pela carta secreta Cavaleiro). */
	boolean temCavaloOuPunho() {
		return tipo == TipoMelhoria.MOVIMENTO_DUPLO || tipo == TipoMelhoria.PEGAR_TRIBUTO;
	}

	@Override
	public String toString() {
		return simbolo() + "(" + cor + ")";
	}
}
