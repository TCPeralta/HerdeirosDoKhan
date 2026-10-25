# Herdeiros do Khan – INF1636 – 1ª iteração (componente Model)

## Como abrir
Eclipse → *File > Import > Existing Projects into Workspace* → selecione a pasta (ou o ZIP).
Requer Java 17+ e JUnit 4 (já referenciado como biblioteca do Eclipse).
Para rodar os testes: botão direito em `test/model/TodosOsTestes.java` → *Run As > JUnit Test*.

## Organização
- `src/model` – pacote **model**. Só a classe `ModelAPI` é pública; todas as outras
  (tabuleiro, cartas, peças, jogador…) são *package-private*, invisíveis para View e Controller.
- `test/model` – casos de teste JUnit 4 (mesmo pacote, para poder montar cenários).

| Classe | Responsabilidade |
|---|---|
| `ModelAPI` | API pública (tipos simples: String/int/boolean). Singleton + Façade na 3ª iteração. |
| `Jogo` | Estado da partida e validação/execução das regras de cada ação. |
| `Tabuleiro` | Mapa: paradas, rotas, províncias, cidades (tudo em `HashMap`). Consultas de grafo (distância, grupos de yurts…). |
| `MapaPadrao` | Monta o mapa do jogo (**provisório**, ver abaixo). |
| `Turno` | Ações disponíveis no turno, paradas visitadas, recursos virtuais. |
| `Jogador`, `TabuleiroJogador`, `ColunaJogador`, `Herdeiro` | Peças e tabuleiro individual. |
| `Parada`, `Provincia`, `Cidade`, `Conselheiro` | Elementos do mapa. |
| `MercadoMelhorias`, `Melhoria`, `PecaBonus`, `TrilhaKurultai` | Demais componentes. |
| `CartaConselheiroAberto`, `CartaConselheiroSecreto`, `FabricaCartas` | As 14 + 10 cartas; a condição de cada carta é um objeto (lambda). |

### Princípio aberto/fechado
- Mapa montado por métodos (`adicionarParada`, `ligarParadas`…): trocar o mapa não muda as regras.
- Cada carta recebe sua condição como objeto: carta nova = uma linha em `FabricaCartas`.
- Ações iniciais dos herdeiros concentradas em `Herdeiro.acoesIniciais`.

### Convenção de erros
Cada ação devolve `true`/`false`; se recusada, nada muda e o motivo fica em
`getUltimoErro()` (`getMensagemErro()` na API).

## Regras implementadas
Preparação (cidades + tesouros, tributos, bônus, cartas, moedas iniciais, Kurultai) ·
ativação de colunas e recuperação pela coluna branca · mover (rotas, pular o próprio yurt,
passar por parada ocupada, capacidade simples/dupla/Karakorum) · pegar tributo
(províncias adjacentes a paradas visitadas) · Khan (obrigatório, não repete lugar,
abastece 3 províncias até o máximo de 3, área de melhorias com moeda/espada) ·
atacar cidades (1 espada, depois 2) e conquista · construir yurts · comprar melhorias
(2 moedas, coluna da cor, não usa no turno da compra, linha completa → melhoria grátis
na coluna branca) · entrega a conselheiros + bônus · peças de bônus · cartas de
conselheiro aberto · trilha do Kurultai, fim de jogo e rodada final · influência nas
regiões · cartas secretas · vencedor e desempates.

## Pendências (dependem de conferir o jogo físico)
1. **Mapa** (`MapaPadrao`): rotas, paradas simples e províncias comuns são uma aproximação.
2. **Tabuleiros de herdeiro**: todos usam as ações do Tolui lidas no manual.
3. **Cores das melhorias** e nomes de 3 tipos de tesouro (SEDA, PORCELANA, CHIFRE) são provisórios.
4. **Habilidades especiais** dos herdeiros (pág. 20) ainda não implementadas (só a do Jochi na coluna branca).

## Casos de teste (170, JUnit 4)
Seguem as diretrizes do Cap. 18 (resumo JUnit):
- uma classe `XptoTest` para cada classe testada (as regras da classe `Jogo` foram divididas por ação: `JogoMovimentoTest`, `JogoKhanTest`…);
- nomes longos e descritivos; uma única assertiva por teste, sempre com mensagem explicativa;
- `timeout` em todos os testes; erros esperados testados com `expected = ...class`;
- `@Before` e métodos auxiliares para evitar redundância e lógica nos testes;
- testes independentes (cada um monta seu próprio cenário com o mapa pequeno `MapaTeste`);
- suíte `TodosOsTestes` que executa tudo.

| Classe de teste | O que verifica |
|---|---|
| `ProvinciaTest` | máximo de 3 tributos, retirar de província vazia |
| `ConselheiroTest` | demandas (2 iguais, 3 quaisquer, Karakorum), votos, bônus só na 1ª entrega |
| `TrilhaKurultaiTest` | limites 10/14/17, ficha neutra, disparo do fim |
| `TabuleiroJogadorTest` | linhas completas, Jochi, coluna cheia, recuperação |
| `MercadoMelhoriasTest` | deslizar para a direita, reabastecer |
| `TabuleiroTest` | rotas, distâncias, yurts por região, grupos conectados, capacidade |
| `FabricaCartasTest` | 14 + 10 cartas, condições e patamares |
| `JogoPreparacaoTest` | moedas, cidades, tesouros, cartas, bônus, nº de jogadores |
| `JogoColunasTest` | uma coluna por turno, coluna branca, passagem de vez |
| `JogoMovimentoTest` | rotas, paradas ocupadas, parada dupla, pular yurt |
| `JogoTributoTest` | adjacência, ações disponíveis, província vazia |
| `JogoKhanTest` | abastecimento, máximo 3, obrigatoriedade, área de melhorias |
| `JogoCidadeTest` | custo 1/2 espadas, espada virtual, conquista |
| `JogoYurtTest` | parada visitada, capacidade, Karakorum |
| `JogoMelhoriaTest` | custo, cor da coluna, turno da compra, melhoria grátis |
| `JogoConselheiroTest` | entregas, bônus, tesouro virtual, cartas abertas |
| `JogoFimDeJogoTest` | rodada final, influência, carta secreta, desempates |
