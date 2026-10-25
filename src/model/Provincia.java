package model;

/**
 * Província: área entre as rotas onde ficam as peças de tributo.
 * Cada província produz um único tipo de tributo e acumula no máximo 3 peças
 * quando o Khan a abastece.
 */
class Provincia {
	static final int MAXIMO_TRIBUTOS = 3;

	private final String id;
	private final Regiao regiao;
	private final TipoTributo tipoTributo;
	private final boolean provinciaDoKhan;
	private int tributos;

	Provincia(String id, Regiao regiao, TipoTributo tipoTributo, boolean provinciaDoKhan) {
		this.id = id;
		this.regiao = regiao;
		this.tipoTributo = tipoTributo;
		this.provinciaDoKhan = provinciaDoKhan;
	}

	String getId() { return id; }
	Regiao getRegiao() { return regiao; }
	TipoTributo getTipoTributo() { return tipoTributo; }
	boolean isProvinciaDoKhan() { return provinciaDoKhan; }
	int getTributos() { return tributos; }

	/** Coloca 1 tributo, respeitando o máximo de 3. Retorna true se colocou. */
	boolean adicionarTributo() {
		if (tributos >= MAXIMO_TRIBUTOS) return false;
		tributos++;
		return true;
	}

	/** Retira 1 tributo. Retorna false se a província estiver vazia. */
	boolean retirarTributo() {
		if (tributos == 0) return false;
		tributos--;
		return true;
	}

	@Override
	public String toString() { return id; }
}
