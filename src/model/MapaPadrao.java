package model;

/**
 * Monta o mapa do tabuleiro principal.
 *
 * ATENÇÃO: versão provisória, montada a partir da imagem reduzida do manual.
 * As 9 cidades, os 7 conselheiros (6 paradas duplas + Karakorum) e as 4
 * províncias do Khan seguem o manual, mas as paradas simples, as rotas e as
 * províncias comuns são uma aproximação. Quando o tabuleiro real for mapeado,
 * só esta classe precisa mudar.
 */
final class MapaPadrao {

	static final String KARAKORUM = "KARAKORUM";

	private MapaPadrao() {}

	static Tabuleiro criar() {
		Tabuleiro t = new Tabuleiro();

		// ----------------------- paradas -----------------------
		t.adicionarParada(KARAKORUM, TipoParada.KARAKORUM, null)
				.setConselheiro(Conselheiro.karakorum(TipoTesouro.FERRO, TipoTesouro.PELE, TipoTesouro.CHIFRE));

		t.adicionarParada("C_KIEV", TipoParada.DUPLA, Regiao.RUSSIA)
				.setConselheiro(Conselheiro.doisIguais(TipoTesouro.PELE));
		t.adicionarParada("C_NORTE", TipoParada.DUPLA, Regiao.RUSSIA)
				.setConselheiro(Conselheiro.doisIguais(TipoTesouro.FERRO));
		t.adicionarParada("C_SARAI", TipoParada.DUPLA, Regiao.RUSSIA)
				.setConselheiro(Conselheiro.doisIguais(TipoTesouro.SEDA));
		t.adicionarParada("C_LESTE", TipoParada.DUPLA, Regiao.CHINA)
				.setConselheiro(Conselheiro.doisIguais(TipoTesouro.CHIFRE));
		t.adicionarParada("C_SUL", TipoParada.DUPLA, Regiao.CHINA)
				.setConselheiro(Conselheiro.tresQuaisquer());
		t.adicionarParada("C_PERSIA", TipoParada.DUPLA, Regiao.PERSIA)
				.setConselheiro(Conselheiro.doisIguais(TipoTesouro.PORCELANA));

		for (String id : new String[] { "R1", "R2", "R3", "R4", "R5" })
			t.adicionarParada(id, TipoParada.SIMPLES, Regiao.RUSSIA);
		for (String id : new String[] { "P1", "P2", "P3", "P4", "P5" })
			t.adicionarParada(id, TipoParada.SIMPLES, Regiao.PERSIA);
		for (String id : new String[] { "K1", "K2", "K3", "K4", "K5", "K6" })
			t.adicionarParada(id, TipoParada.SIMPLES, Regiao.CHINA);

		// ----------------------- rotas -----------------------
		String[][] rotas = {
			{ KARAKORUM, "R5" }, { KARAKORUM, "P1" }, { KARAKORUM, "P4" }, { KARAKORUM, "K3" }, { KARAKORUM, "K1" },
			{ "C_KIEV", "R1" }, { "C_KIEV", "R2" }, { "C_KIEV", "P1" },
			{ "R2", "C_NORTE" }, { "C_NORTE", "R3" }, { "C_NORTE", "R5" },
			{ "R3", "C_SARAI" }, { "C_SARAI", "R4" }, { "R4", "K1" },
			{ "R5", "P1" }, { "R1", "P5" },
			{ "P1", "P2" }, { "P5", "P2" }, { "P5", "C_PERSIA" }, { "P2", "P3" },
			{ "P3", "C_PERSIA" }, { "P3", "P4" }, { "P4", "K6" }, { "C_PERSIA", "K6" },
			{ "K6", "C_SUL" }, { "C_SUL", "K4" }, { "C_SUL", "K5" }, { "K5", "C_LESTE" },
			{ "K4", "K3" }, { "K3", "C_LESTE" }, { "C_LESTE", "K2" }, { "K2", "K1" },
		};
		for (String[] r : rotas) t.ligarParadas(r[0], r[1]);

		// ----------------------- cidades -----------------------
		cidade(t, "Kiev", Regiao.RUSSIA, "C_KIEV", "R1", "R2");
		cidade(t, "Moscou", Regiao.RUSSIA, "R2", "C_NORTE", "R5");
		cidade(t, "Sarai", Regiao.RUSSIA, "R3", "C_SARAI", "R4");
		cidade(t, "Samarcanda", Regiao.PERSIA, "P1", "P2", "P3");
		cidade(t, "Bagda", Regiao.PERSIA, "P5", "P2", "C_PERSIA");
		cidade(t, "Cabul", Regiao.PERSIA, "P4", "K6", "C_PERSIA");
		cidade(t, "Kaesong", Regiao.CHINA, "K1", "K2", "C_LESTE");
		cidade(t, "Pequim", Regiao.CHINA, "K3", "K4", "C_LESTE");
		cidade(t, "Cantao", Regiao.CHINA, "K5", "C_SUL", "K4");

		// ----------------------- províncias -----------------------
		provincia(t, "PR_R1", Regiao.RUSSIA, TipoTributo.ESPADA, false, "R1", "C_KIEV", "P5");
		provincia(t, "PR_R2", Regiao.RUSSIA, TipoTributo.MOEDA, false, "R2", "C_NORTE", "R5");
		provincia(t, "PR_R3", Regiao.RUSSIA, TipoTributo.YURT, false, "R3", "C_SARAI", "R4");
		provincia(t, "KHAN_RUSSIA_OESTE", Regiao.RUSSIA, TipoTributo.YURT, true, "C_KIEV", "R2", "R5", "P1");
		provincia(t, "KHAN_RUSSIA_LESTE", Regiao.RUSSIA, TipoTributo.MOEDA, true, "R4", "K1", "C_SARAI");

		provincia(t, "PR_P1", Regiao.PERSIA, TipoTributo.MOEDA, false, "P5", "P2");
		provincia(t, "PR_P2", Regiao.PERSIA, TipoTributo.ESPADA, false, "P3", "C_PERSIA");
		provincia(t, "PR_P3", Regiao.PERSIA, TipoTributo.YURT, false, "P1", "P2");
		provincia(t, "KHAN_PERSIA", Regiao.PERSIA, TipoTributo.ESPADA, true, "P2", "P3", "P4", KARAKORUM);

		provincia(t, "PR_K1", Regiao.CHINA, TipoTributo.ESPADA, false, "K1", "K2");
		provincia(t, "PR_K2", Regiao.CHINA, TipoTributo.YURT, false, "K3", "K4");
		provincia(t, "PR_K3", Regiao.CHINA, TipoTributo.MOEDA, false, "K5", "C_LESTE");
		provincia(t, "KHAN_CHINA", Regiao.CHINA, TipoTributo.MOEDA, true, "K6", "C_SUL", "K4");

		t.definirSetasDoKhan("KHAN_RUSSIA_OESTE", "PR_R1", "PR_R2");
		t.definirSetasDoKhan("KHAN_RUSSIA_LESTE", "PR_R3", "PR_K1");
		t.definirSetasDoKhan("KHAN_PERSIA", "PR_P2", "PR_P3");
		t.definirSetasDoKhan("KHAN_CHINA", "PR_K2", "PR_K3");

		return t;
	}

	private static void cidade(Tabuleiro t, String nome, Regiao r, String... paradas) {
		t.adicionarCidade(nome, r);
		for (String p : paradas) t.ligarCidade(p, nome);
	}

	private static void provincia(Tabuleiro t, String id, Regiao r, TipoTributo tipo, boolean khan, String... paradas) {
		t.adicionarProvincia(id, r, tipo, khan);
		for (String p : paradas) t.ligarProvincia(p, id);
	}
}
