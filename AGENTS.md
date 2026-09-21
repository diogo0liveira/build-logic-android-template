# AI Agent Instructions

Bem-vindo! O foco principal deste repositório é o **`build-logic`**.
Este projeto serve como um template fundacional, onde os plugins de convenção customizados do Gradle são o produto principal. Os módulos incluídos (`:app`, `:module-feature`, `:module-core`, `:module-jvm`, etc.) são apenas **exemplos** de como consumir e aplicar este `build-logic`.

> 🔴 = regra crítica, não negociável. ⚠️ = convenção forte, siga salvo justificativa clara. ℹ️ = contexto/orientação.

---

## 0. Início rápido (leia antes de tocar em qualquer coisa)

| Ação | Comando |
|---|---|
| Build completo | `./gradlew build` |
| Rodar testes (todos os módulos) | `./gradlew test` |
| Rodar testes de um módulo | `./gradlew :module-core:test` |
| Lint/format check (Spotless) | `./gradlew spotlessCheck` |
| Aplicar formatação (Spotless) | `./gradlew spotlessApply` |
| Análise estática (Detekt) | `./gradlew detekt` |
| Build + todas as verificações | `./gradlew check` |

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
- 🔴 **NÃO FAÇA**: configurar manualmente o AGP (Android Gradle Plugin), opções do Kotlin ou dependências base diretamente nos `build.gradle.kts` dos módulos de exemplo. Isso deve sempre passar por um plugin de convenção.

### Estrutura de referência

```
.
├── .agents/
│   └── skills/                            # ← skills compartilhadas (Agent Skills open standard)
│       ├── version-catalog/SKILL.md       #    padronização do libs.versions.toml
│       └── gradle-dependencies/SKILL.md   #    padronização dos blocos dependencies{} em build.gradle.kts
├── .claude/
│   └── skills -> ../.agents/skills        # ← symlink para o Claude Code enxergar as mesmas skills
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

---

## 2. Gerenciamento de Dependências

- ⚠️ **Dependências comuns/compartilhadas**: devem ser adicionadas em `gradle/libs.versions.toml` e aplicadas via o plugin de convenção apropriado dentro de `build-logic`.
- ℹ️ **Dependências específicas de módulo**: podem ser adicionadas diretamente no `build.gradle.kts` do módulo específico (referenciando `libs.versions.toml`), desde que sejam exclusivas daquele módulo.
- 🔴 Nunca declare uma dependência com versão *hardcoded* fora do `libs.versions.toml` — mesmo em um módulo de exemplo.

### 2.1 Skills locais obrigatórias para este domínio

Este repositório possui duas skills locais especializadas em dependências, escritas no formato **Agent Skills** (`SKILL.md` com frontmatter `name`/`description`). Esse é um padrão aberto lido tanto pelo **Claude Code** quanto pelo **Gemini no Android Studio** (Agent Mode → "Extend Agent Mode with skills"), então uma única fonte de verdade serve os dois agentes.

🔴 **Sempre que a tarefa envolver `libs.versions.toml` ou o bloco `dependencies { }` de um `build.gradle.kts`, leia o `SKILL.md` correspondente ANTES de editar** — não confie apenas na memória das regras resumidas abaixo.

| Skill | Local (fonte) | Quando usar |
|---|---|---|
| `version-catalog` | `.agents/skills/version-catalog/SKILL.md` | Adicionar, remover, renomear ou reorganizar entradas em `gradle/libs.versions.toml` (seções `[versions]`, `[plugins]`, `[libraries]`, `[bundles]`; nomenclatura de aliases em kebab-case; ordenação e posicionamento de BOM). |
| `gradle-dependencies` | `.agents/skills/gradle-dependencies/SKILL.md` | Adicionar, remover ou reorganizar dependências dentro de qualquer `build.gradle.kts` (ordem `api` → `implementation` → `testImplementation` → `androidTestImplementation` → `debugImplementation`; ordenação alfabética; prioridade de BOM; posicionamento de `testFixtures`). |

Resumo rápido do protocolo de cada uma (a versão completa no `SKILL.md` prevalece em caso de dúvida):

- **`version-catalog`**: reestruturar o TOML nas 4 seções obrigatórias → padronizar aliases (kebab-case, regras de transformação por contexto) → atualizar todas as referências afetadas em `build.gradle.kts` → rodar `gradle_sync`.
- **`gradle-dependencies`**: `gradle_sync` inicial → reorganizar respeitando grupos de configuração, BOM e ordenação alfabética → `gradle_sync` final para validar.

⚠️ Como as duas skills se sobrepõem no fluxo (mudar um alias no catálogo normalmente exige atualizar as referências no `build.gradle.kts`), ao fazer uma refatoração ampla de dependências aplique primeiro `version-catalog` e só então `gradle-dependencies`, nessa ordem.

#### Onde cada agente procura as skills

- 🔴 **Fonte única**: mantenha os arquivos reais em `.agents/skills/` — esse é o diretório padrão do Gemini no Android Studio (a partir da versão *Quail*; versões anteriores usavam `.skills/` ou `.agent/skills/`, hoje legado).
- ⚠️ **Claude Code** procura em `.claude/skills/`. Em vez de duplicar o conteúdo, mantenha `.claude/skills` como *symlink* para `.agents/skills` (`ln -s ../.agents/skills .claude/skills`), garantindo que os dois agentes leiam exatamente o mesmo `SKILL.md` — sem risco de divergência entre cópias.
- ℹ️ No Gemini/Android Studio, a skill pode ser acionada automaticamente (o modelo decide com base na `description` do frontmatter) ou manualmente digitando `@nome-da-skill` no chat do Agent Mode.
- ℹ️ Se seu ambiente não suportar *symlinks* (ex.: alguns runners de CI no Windows), duplique os dois arquivos e adicione um lembrete no PR para mantê-los sincronizados manualmente.

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

## 4. Documentação (KDoc)

- 🔴 **`build-logic`**: KDoc é OBRIGATÓRIO para todos os membros públicos (classes, funções, propriedades). A intenção e o uso de cada plugin de convenção e extension devem estar claramente explicados.
- ℹ️ **Outros módulos**: KDoc não é estritamente obrigatório nos módulos de exemplo consumidores, mas código limpo e autoexplicativo é esperado.

---

## 5. Uso dos Exemplos

Os módulos `:app`, `:module-feature`, `:module-core` e `:module-jvm` demonstram como estruturar um projeto multi-módulo consumindo o `build-logic` via aliases do catálogo de versões (ex.: `alias(libs.plugins.convention.android.application)`).

- ℹ️ Eles NÃO impõem arquitetura em nível de aplicação (MVVM/MVI etc.), apenas arquitetura em nível de build.
- ⚠️ Ao adicionar um novo módulo de exemplo, aplique os plugins de convenção existentes em vez de recriar configuração manualmente — se a convenção necessária não existir, crie-a em `build-logic` primeiro (ver seção 1).

---

## 6. Convenções de Commit / PR

- ⚠️ Use *Conventional Commits* (`feat:`, `fix:`, `refactor:`, `build:`, `docs:`, `test:`, `chore:`).
- ⚠️ Se a mudança for em um plugin de convenção, prefixe o escopo: `feat(build-logic): ...`.
- ℹ️ No corpo do PR, indique: (1) qual plugin de convenção foi alterado, (2) por que, (3) quais módulos consumidores foram validados (`./gradlew build` local).

---

## 7. Limites de Segurança

- 🔴 Nunca commite segredos, chaves de API, keystores ou credenciais de assinatura.
- 🔴 Nunca modifique `gradle/wrapper/gradle-wrapper.properties` para apontar a uma distribuição do Gradle não oficial.
- ⚠️ Mudanças em `settings.gradle.kts` (inclusão/remoção de módulos) devem ser explicitadas claramente no PR, pois afetam todo o grafo de build.
