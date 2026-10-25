package model;

/** Peça de bônus. Depois de usada é virada para o verso e não pode ser usada novamente. */
class PecaBonus {
	private final TipoBonus tipo;
	private boolean usada;

	PecaBonus(TipoBonus tipo) { this.tipo = tipo; }

	TipoBonus getTipo() { return tipo; }
	boolean isUsada() { return usada; }
	void marcarUsada() { usada = true; }
}
