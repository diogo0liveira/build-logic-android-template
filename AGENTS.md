# AI Agent Instructions

Bem-vindo! O foco principal deste repositório é o **`build-logic`**.
Este projeto serve como um modelo fundacional, onde os plugins de convenção customizados do Gradle são o produto principal. Os módulos incluídos (`:app`, `:module-feature`, `:module-core`, `:module-jvm`, etc.) são apenas **exemplos** de como consumir e aplicar este `build-logic`.

> 🔴 = regra crítica, não negociável. ⚠️ = convenção forte, siga salvo justificativa clara. ℹ️ = contexto/orientação.

---

## 0. Início rápido (leia antes de tocar em qualquer coisa)

| Ação                            | Comando                                    |
|---------------------------------|--------------------------------------------|
| Build completo                  | `./gradlew build`                          |
| Rodar testes (todos os módulos) | `./gradlew test`                           |
| Rodar testes de um módulo       | `./gradlew :module-core:test`              |
| Rodar testes do build-logic     | `./gradlew :build-logic:convention:test`   |
| Relatório/verificação cobertura | `./gradlew coverageTestReport -Pcoverage`  |
| Lint/format check (Spotless)    | `./gradlew spotlessCheck`                  |
| Aplicar formatação (Spotless)   | `./gradlew spotlessApply`                  |
| Análise estática (Detekt)       | `./gradlew detekt`                         |
| Detekt no build-logic           | `./gradlew :build-logic:convention:detekt` |
| Build + todas as verificações   | `./gradlew check`                          |

> ℹ️ Ajuste os nomes das tasks acima caso divirjam dos aliases reais definidos em `build-logic/convention` — se um comando aqui estiver desatualizado, corrija esta tabela ao invés de ignorá-la silenciosamente.

🔴 **Sempre use o wrapper (`./gradlew`), nunca um `gradle` instalado globalmente.**

### Definição de "pronto"
Antes de considerar qualquer tarefa concluída, o agente DEVE rodar, nesta ordem, e confirmar que todos passam:
1. `./gradlew spotlessApply`
2. `./gradlew detekt`
3. `./gradlew build`

Se algum falhar, corrija antes de finalizar — não entregue código que quebra o pipeline.

---

## 1. O Produto Central: `build-logic`

Toda configuração de build, gerenciamento de dependências e ferramentas (lint, formatação, cobertura) é centralizada em `build-logic/convention`.

- ⚠️ **Para modificar o processo de build**: edite o plugin de convenção correspondente dentro de `build-logic/convention/src/main/kotlin/com/dao/convention/plugins/`.
- 🔴 **NÃO FAÇA**: configurar manualmente o AGP (Android Gradle Plugin), opções do Kotlin ou dependências-base diretamente nos `build.gradle.kts` dos módulos de exemplo. Isso deve sempre passar por um plugin de convenção.

### Estrutura de referência

```
.
├── build-logic/
│   └── convention/
│       └── src/main/kotlin/com/dao/convention/plugins/   # ← plugins Gradle customizados (fonte da verdade)
├── app/                    # exemplo: aplicação Android
├── module-feature/         # exemplo: módulo de feature
├── module-core/            # exemplo: módulo core
├── module-jvm/             # exemplo: módulo JVM puro
├── gradle/
│   └── libs.versions.toml  # catálogo de versões (fonte única de dependências)
└── AGENTS.md
```

### Plugins de Convenção Disponíveis

| Alias no Catálogo (`libs.plugins...`) | Plugin ID                        | Classe de Implementação              | Propósito                                                        |
|---------------------------------------|----------------------------------|--------------------------------------|------------------------------------------------------------------|
| `convention.root`                     | `convention.root`                | `RootConventionPlugin`               | Configuração raiz do projeto, agregação Jacoco e Spotless global |
| `convention.android.application`      | `convention.android.application` | `AndroidApplicationConventionPlugin` | Aplicação Android executável (`:app`)                            |
| `convention.android.library`          | `convention.android.library`     | `AndroidLibraryConventionPlugin`     | Bibliotecas Android padrão (`:module-core`, etc.)                |
| `convention.android.feature`          | `convention.android.feature`     | `AndroidFeatureConventionPlugin`     | Módulos de feature Android (`:module-feature`)                   |
| `convention.android.compose`          | `convention.android.compose`     | `AndroidComposeConventionPlugin`     | Suporte e dependências do Jetpack Compose                        |
| `convention.kotlin.jvm`               | `convention.kotlin.jvm`          | `KotlinJvmConventionPlugin`          | Módulos Kotlin/JVM puros sem dependência Android (`:module-jvm`) |
| `convention.hilt`                     | `convention.hilt`                | `HiltConventionPlugin`               | Injeção de dependência via Dagger Hilt                           |

---

## 2. Gerenciamento de Dependências

- ⚠️ **Dependências comuns/compartilhadas**: devem ser adicionadas em `gradle/libs.versions.toml` e aplicadas via o plugin de convenção apropriado dentro de `build-logic`.
- ℹ️ **Dependências específicas de módulo**: podem ser adicionadas diretamente no `build.gradle.kts` do módulo específico (referenciando `libs.versions.toml`), desde que sejam exclusivas daquele módulo.
- 🔴 Nunca declare uma dependência com versão *hardcoded* fora do `libs.versions.toml` — mesmo em um módulo de exemplo.

### 2.1 Skills globais obrigatórias para este domínio

Este domínio é coberto por duas skills especializadas em dependências, escritas no formato **Agent Skills** (`SKILL.md` com frontmatter `name`/`description`). Elas **não fazem parte deste repositório**: ficam no escopo **global** do ambiente do desenvolvedor, em `~/.gemini/skills/`, e são compartilhadas entre todos os projetos.

🔴 **Sempre que a tarefa envolver `libs.versions.toml` ou o bloco `dependencies { }` de um `build.gradle.kts`, leia o `SKILL.md` correspondente ANTES de editar** — não confie apenas na memória das regras resumidas abaixo.

| Skill                 | Local (global)                                  | Quando usar                                                                                                                                                                                                                                                                    |
|-----------------------|-------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `version-catalog`     | `~/.gemini/skills/version-catalog/SKILL.md`     | Adicionar, remover, renomear ou reorganizar entradas em `gradle/libs.versions.toml` (seções `[versions]`, `[plugins]`, `[libraries]`, `[bundles]`; nomenclatura de aliases em kebab-case; ordenação e posicionamento de BOM).                                                  |
| `gradle-dependencies` | `~/.gemini/skills/gradle-dependencies/SKILL.md` | Adicionar, remover ou reorganizar dependências dentro de qualquer `build.gradle.kts` (ordem `api` → `implementation` → `testImplementation` → `androidTestImplementation` → `debugImplementation`; ordenação alfabética; prioridade de BOM; posicionamento de `testFixtures`). |

Resumo rápido do protocolo de cada uma (a versão completa no `SKILL.md` prevalece em caso de dúvida):

- **`version-catalog`**: reestruturar o TOML nas 4 seções obrigatórias → padronizar aliases (kebab-case, regras de transformação por contexto) → atualizar todas as referências afetadas em `build.gradle.kts` → rodar validação de sincronização.
- **`gradle-dependencies`**: validação inicial → reorganizar respeitando grupos de configuração, BOM e ordenação alfabética → validação final.
- ℹ️ **Sincronização via CLI (`gradle_sync`)**: Em ambiente de linha de comando (sem IDE), o passo `gradle_sync` das skills corresponde a executar `./gradlew help` ou `./gradlew dependencies` para confirmar que o grafo de dependências resolve sem erros.

⚠️ Como as duas skills se sobrepõem no fluxo (mudar um, aliás no catálogo normalmente exige atualizar as referências no `build.gradle.kts`), ao fazer uma refatoração ampla de dependências aplique primeiro `version-catalog` e só então `gradle-dependencies`, nessa ordem.

#### Onde as skills ficam

- 🔴 **Escopo global**: as skills residem em `~/.gemini/skills/` na máquina do desenvolvedor. **Não** copie nem crie *symlinks* delas dentro do repositório (`.agents/skills/`, `.claude/skills/`) — o repositório não é a fonte dessas regras.
- ℹ️ O agente carrega as skills globais automaticamente (com base na `description` do frontmatter) ou sob demanda, digitando `@nome-da-skill` no chat.
- ⚠️ Se a skill não estiver disponível no ambiente, não presuma as regras: siga o resumo acima e avise o humano de que a skill global está ausente.

---

## 3. Qualidade de Código & Detekt (REGRA ESTRITA)

O `build-logic` aplica Detekt e Spotless.

- ⚠️ **Spotless**: todo código Kotlin deve seguir a formatação padrão. Rode `./gradlew spotlessApply` antes de finalizar qualquer alteração.
- 🔴 **Detekt — proibição de `@Suppress`**: você NÃO PODE usar anotações `@Suppress` para silenciar regras do Detekt.
  - Se o código gerado disparar um warning/erro do Detekt, refatore a implementação até que passe organicamente.
  - **Critério de parada**: se após 2–3 tentativas de refatoração genuína a regra continuar falhando, PARE e faça uma das duas coisas — nunca use `@Suppress` como saída de emergência:
    1. Documente o motivo no PR/commit e pergunte a um humano se a regra deveria ser ajustada em `build-logic/convention` (arquivo de config do Detekt), ou
    2. Se a regra for legitimamente inaplicável a este caso, a mudança de configuração do Detekt em si deve ser feita — e justificada — no `build-logic`, nunca suprimida pontualmente no código consumidor.

---

## 4. Convenções de Testes de Unidade (Naming & Best Practices)

- 🔴 **Nomenclatura com backticks**: em testes de unidade Kotlin (JVM), declare o nome dos métodos usando backticks (`` `...` ``) expressando intenção e comportamento em linguagem natural.
- 🔴 **Proibição do prefixo `test`**: Nunca use o prefixo `test ` no nome de métodos anotados com `@Test`. A anotação `@Test` já identifica o teste, tornando a palavra redundante.
- ⚠️ **Padrão adotado**: `should <verbo no infinitivo> <resultado esperado> [when <condição>]`.
  - O verbo após `should` deve estar na forma base / infinitivo em inglês (ex.: `should calculate`, `should return`, `should aggregate`), evitando conjugações incorretas como *"should calculates"*.
  - Exemplos:
    - `` `should return zero coverage when classes list is empty` ``
    - `` `should calculate percentage when total is greater than zero` ``
    - `` `should return null percentage when total is zero` ``
- ℹ️ **Testes instrumentados (`androidTest`)**: Em testes instrumentados Android, dê preferência a camelCase iniciando com `should` (ex.: `shouldUseAppContext()`) caso haja limitações de runtime com backticks.

---

## 5. Documentação (KDoc)

- 🔴 **`build-logic`**: KDoc é OBRIGATÓRIO para todos os membros públicos (classes, funções, propriedades). A intenção e o uso de cada plugin de convenção e extension devem estar claramente explicados.
- ℹ️ **Outros módulos**: KDoc não é estritamente obrigatório nos módulos de exemplo consumidores, mas código limpo e autoexplicativo é esperado.

---

## 6. Uso dos Exemplos

Os módulos `:app`, `:module-feature`, `:module-core` e `:module-jvm` demonstram como estruturar um projeto multi-módulo consumindo o `build-logic` via aliases do catálogo de versões (ex.: `alias(libs.plugins.convention.android.application)`).

- ℹ️ Eles NÃO impõem arquitetura ao nível de aplicação (MVVM/MVI, etc.), apenas arquitetura ao nível de build.
- ⚠️ Ao adicionar um novo módulo de exemplo, aplique os plugins de convenção existente em vez de recriar configuração manualmente — se a convenção necessária não existir, crie-a em `build-logic` primeiro (ver seção 1).

---

## 7. Convenções de Commit / PR

Este repositório aplica rigorosamente as diretrizes de mensagens de commit utilizando `commitlint` (`@commitlint/config-conventional`). Toda mensagem de commit e título de Pull Request gerado DEVE seguir a especificação do Conventional Commits.

### 🔴 Formato Obrigatório:
`<type>(<optional scope>): <short description in lowercase>`

🔴 **Idioma da mensagem e título**: Embora este documento esteja em português, o texto da mensagem de commit e o título do PR DEVEM ser redigidos em **inglês**, preservando o histórico do repositório (`git log`).

### 🔴 Tipos Permitidos:
- `feat`: Uma nova funcionalidade ou plugin de convenção.
- `fix`: Uma correção de bug em plugin ou código existente.
- `docs`: Alterações exclusivamente em documentação (`README.md`, `AGENTS.md`, KDoc).
- `style`: Alterações de formatação e estilo que não afetam a semântica do código (espaçamento, Spotless, etc.).
- `refactor`: Refatoração de código que não corrige bug nem adiciona funcionalidade.
- `perf`: Mudança de código que melhora o desempenho (ex.: otimização de compilação, regex).
- `test`: Adição de novos testes ou correção de testes existentes.
- `build`: Mudanças que afetam o sistema de build, plugins de convenção ou dependências externas (escopos: `build-logic`, `deps`, `catalog`).
- `ci`: Mudanças em arquivos e scripts de configuração da CI (escopo: `.github/workflows/ci.yml`).
- `chore`: Outras tarefas de manutenção rotineira que não modificam arquivos de código-fonte ou testes.
- `revert`: Reverte um commit anterior.

### 🔴 Regras de Formatação:
- A linha de assunto (subject) DEVE ser escrita inteiramente em letras minúsculas.
- NÃO termine a linha de assunto com ponto final (`.`).
- Mantenha a linha de assunto concisa (máximo de 72 caracteres).
- Use o modo imperativo em inglês na descrição (ex.: `"add feature"` em vez de `"added feature"` ou `"adds feature"`).
- Se a tarefa resolver uma issue específica, referencie-a no corpo usando palavras-chave (ex.: `Fixes #123`).

### ⚠️ Escopos e Boas Práticas:
- ⚠️ Se a mudança for em um plugin de convenção, use o escopo correspondente: `feat(build-logic): ...` ou `fix(build-logic): ...`.
- ⚠️ Para atualizações de dependências, utilize o escopo: `chore(deps): ...` ou `build(deps): ...`.
- ℹ️ No corpo do PR, indique claramente:
  1. Qual plugin de convenção ou módulo foi alterado.
  2. O motivo da alteração.
  3. Quais comandos locais foram validados (ex.: `./gradlew build`, `./gradlew spotlessApply`, `./gradlew detekt`).

---

## 8. Limites de Segurança & Disciplina de Escopo

- 🔴 **Proibição estrita de adivinhação e desvio de plano**: se durante a implementação for identificado que o que foi planejado não é possível de ser entregue como desenhado, ou surgir qualquer dúvida/impedimento técnico, você DEVE PARAR IMEDIATAMENTE. Nunca tente "adivinhar", improvisar contornos silenciosos ou entregar algo divergente do que foi aprovado no plano. Reporte o impedimento com precisão ao usuário e aguarde nova decisão/replanejamento.
- 🔴 **Aprovação prévia e escopo estrito**: nunca altere código, arquivos ou configurações que não sejam estritamente necessários para a tarefa solicitada ou que não tenham sido previamente aprovados pelo usuário.
- 🔴 Nunca commit segredos, chaves de API, keystores ou credenciais de assinatura.
- 🔴 Nunca modifique `gradle/wrapper/gradle-wrapper.properties` para apontar a uma distribuição do Gradle não oficial.
- ⚠️ Mudanças em `settings.gradle.kts` (inclusão/remoção de módulos) devem ser explicitadas claramente no PR, pois afetam todo o grafo de build.
- ⚠️ **Minimização de ruído**: não realize refatorações cosméticas em arquivos alheios ao escopo da tarefa e preserve comentários e documentações existentes.
