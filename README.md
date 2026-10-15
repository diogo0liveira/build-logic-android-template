# Build Logic Android Template
[![CI](https://github.com/diogo0liveira/build-logic-android-template/actions/workflows/ci.yml/badge.svg?event=push)](https://github.com/diogo0liveira/build-logic-android-template/actions/workflows/ci.yml)
[![codecov](https://codecov.io/github/diogo0liveira/build-logic-android-template/graph/badge.svg?token=X87EA571VD)](https://codecov.io/github/diogo0liveira/build-logic-android-template)

O foco principal deste repositório é o **`build-logic`**, que centraliza toda a configuração de build (Gradle) do projeto. Os demais módulos incluídos (`:app`, `:module-feature`, `:module-core`, `:module-jvm`, etc.) servem apenas como **exemplos e playground** para demonstrar como consumir e aplicar estes plugins de conveniência.

---

## 🛠️ Entradas no `libs.versions.toml` (Dependências do Build-Logic)

O gerenciamento de dependências e plugins para o próprio `build-logic` e suas ferramentas está declarado no catálogo de versões (`gradle/libs.versions.toml`). As seguintes entradas são necessárias para o funcionamento dos plugins de convenção do projeto:

*   **Plugins Gradle:**
    *   `android-gradle-plugin` (`com.android.tools.build`) - Base de build do Android.
    *   `detekt-gradle-plugin` (`dev.detekt`) - Analisador estático para o Kotlin.
    *   `spotless-gradle-plugin` (`com.diffplug.spotless`) - Formatador automático de código.
*   **Bibliotecas / Lint:**
    *   `android-gradle-lint` (`androidx.lint`) - Verificações adicionais para os arquivos de build do Android.
*   **Versões Essenciais (`[versions]`):**
    *   `agp`: Define a versão do Android Gradle Plugin.
    *   `kotlin`: Define a versão do Kotlin.
    *   `jvm-target`: Define a versão alvo da JVM.
    *   `detekt`: Controla a versão da ferramenta Detekt.
    *   `spotless`: Controla a versão da ferramenta Spotless.
    *   `jacoco`: Controla a versão do JaCoco para análise de cobertura de código.

---

## 🧩 Plugins de Conveniência (Convention Plugins)

Os plugins de conveniência centralizam toda a lógica de build, eliminando a repetição em arquivos `build.gradle.kts`. Abaixo estão organizados por categoria e finalidade:

### 🌐 1. Raiz & Infraestrutura

| Plugin ID         | Classe de Implementação | Descrição                                                                                                                                   |
|:------------------|:------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------|
| `convention.root` | `RootConventionPlugin`  | Aplicado na raiz do projeto. Configura o Spotless para arquivos `*.gradle.kts` e gerencia a agregação de relatórios de cobertura do JaCoCo. |

### 📱 2. Módulos Android

| Plugin ID                        | Classe de Implementação              | Descrição                                                                                                                  |
|:---------------------------------|:-------------------------------------|:---------------------------------------------------------------------------------------------------------------------------|
| `convention.android.application` | `AndroidApplicationConventionPlugin` | Configura apps executáveis (`:app`), definindo SDKs, AGP e ferramentas de qualidade.                                       |
| `convention.android.library`     | `AndroidLibraryConventionPlugin`     | Configura bibliotecas Android reutilizáveis comuns (ex: `:module-core`).                                                   |
| `convention.android.feature`     | `AndroidFeatureConventionPlugin`     | Especializado para módulos de funcionalidade do app (ex: `:module-feature`), incluindo suporte a UI e dependências comuns. |
| `convention.android.compose`     | `AndroidComposeConventionPlugin`     | Habilita o Jetpack Compose, opções do compilador e dependências de UI em módulos Android.                                  |

### ⚙️ 3. Módulos JVM & Injeção de Dependência

| Plugin ID               | Classe de Implementação     | Descrição                                                                          |
|:------------------------|:----------------------------|:-----------------------------------------------------------------------------------|
| `convention.kotlin.jvm` | `KotlinJvmConventionPlugin` | Configura módulos 100% Kotlin puros, sem dependências Android (ex: `:module-jvm`). |
| `convention.hilt`       | `HiltConventionPlugin`      | Configura Dagger/Hilt e KSP para injeção de dependência.                           |

> [!NOTE]
> **Plugins Internos:** Há plugins auxiliares (como `SpotlessConventionPlugin`, `DetektConventionPlugin`, `JacocoAndroidConventionPlugin`, etc.) que são aplicados automaticamente em segundo plano pelos plugins de conveniência citados acima.

---

## 📊 Relatório de Cobertura (JaCoco)

O projeto possui suporte integrado para gerar relatórios de cobertura de testes combinando a execução de múltiplos módulos e verificando metas mínimas de cobertura. Todas as tarefas públicas pertencem ao grupo **"Coverage Report Convention"**.

Para gerar o relatório agregado do projeto e verificar a cobertura, rode o comando abaixo na raiz:

```bash
./gradlew coverageTestReport -Pcoverage=true
```

> [!NOTE]
> O parâmetro `-Pcoverage=true` é necessário para habilitar a instrumentação e extração de métricas de cobertura nos módulos Android.

### Outras Tarefas de Cobertura Relevantes
Além do relatório global na raiz, tarefas granulares estão disponíveis diretamente em cada módulo. As tarefas locais conectam-se diretamente às saídas de compilação e teste (via `SourceSet` e `ScopedArtifact.CLASSES`), sem necessidade de duplicação física em disco:

*   **Para Módulos Android:**
    *   Relatório apenas dos testes de unidade:
        *   `coverageUnitTestReport` (ex: `./gradlew :app:coverageUnitTestReport -Pcoverage=true`)
    *   Relatório apenas dos testes instrumentados (GMD):
        *   `coverageAndroidTestReport` (ex: `./gradlew :app:coverageAndroidTestReport -Pcoverage=true`)
    *   Relatório agregado e verificação (unidade + instrumentados) específico do módulo:
        *   `coverageTestReport` (ex: `./gradlew :app:coverageTestReport -Pcoverage=true`)
*   **Para Módulos JVM puros:**
    *   Relatório e verificação específico do módulo JVM:
        *   `coverageTestReport` (ex: `./gradlew :module-jvm:coverageTestReport`)

> [!NOTE]
> A tarefa de coleta física (`collect*Coverage`) é acionada sob demanda apenas na agregação global da raiz (`:coverageTestReport`), necessária para compartilhar artefatos entre múltiplos módulos.

Ao finalizar, as tarefas exibem no terminal uma tabela hierárquica por pacote/classe com status colorido e o link direto para o relatório HTML interativo:

```text
=============================================================================================
📊 JaCoCo Coverage Report (:coverageTestReport)
=============================================================================================
Package/Class                                         Line (%)   Branch (%)   Instruction (%)
---------------------------------------------------------------------------------------------
com.dao.android.module.core                              80.0%        75.0%             90.0%
  ├── GetGreeting                                       100.0%          n/a            100.0%
  └── SetGreeting                                        66.7%          n/a             83.3%
---------------------------------------------------------------------------------------------
Total                                            80.0% (8/10) 75.0% (15/20)    90.0% (90/100)
=============================================================================================
👉 Relatório HTML: file:///.../build/reports/jacoco/reportTestCoverage/html/index.html
=============================================================================================
```

### Módulos Incluídos na Agregação Global
Para a tarefa agregada raiz (`coverageTestReport`), os módulos incluídos por padrão pelo plugin `convention.root` são `:app`, `:module-feature`, `:module-core` e `:module-jvm`. Você pode customizar os módulos diretamente no `build.gradle.kts` da raiz através da extensão `jacocoAggregation`:

```kotlin
// Em build.gradle.kts (root)
jacocoAggregation {
    modules.addAll(
        ":app",
        ":module-feature",
        ":module-core",
        ":module-jvm",
    )
}
```

### Verificação de Metas de Cobertura
A verificação estrita de limites de cobertura (`verify*Coverage`) é executada no fluxo da tarefa combinada `coverageTestReport` (tanto nos módulos locais quanto no root com `-Pcoverage`), garantindo que o ciclo padrão de compilação e checagem (`./gradlew check` e `./gradlew build`) permaneça desacoplado e rápido quando o relatório de cobertura não estiver em execução.

Por padrão, a convenção raiz estabelece as seguintes metas globais:
* **Cobertura de instruções (bytecode):** 90% (`minInstructionCoverage = 0.90`)
* **Cobertura de ramificações (branches):** 80% (`minBranchCoverage = 0.80`)
* **Cobertura de linhas:** 70% (`minLineCoverage = 0.70`) *(na extensão base o padrão é 0 / desabilitado)*
* **Tolerância de aviso (amarelo):** 15% (`warningTolerance = 0.15`) *(alerta quando a cobertura fica até 15% abaixo da meta antes de falhar)*
* **Falha no build:** Ativada por padrão (`haltOnFailure = true`)

**Sobrescrevendo as metas:**
Caso um módulo específico precise de regras diferentes das citadas acima, configure a extensão `jacocoVerification` **explicitamente no `build.gradle.kts` daquele módulo**.

A configuração colocada no `build.gradle.kts` da **raiz** afeta **apenas** a verificação agregada global (`coverageTestReport`), não atuando em cascata sobre os submódulos:

```kotlin
jacocoVerification {
    minInstructionCoverage.set(0.80.toBigDecimal()) // Exige 80% de instruções
    minBranchCoverage.set(0.70.toBigDecimal())      // Exige 70% de branches
    minLineCoverage.set(0.85.toBigDecimal())        // Exige 85% de linhas
    warningTolerance.set(0.10.toBigDecimal())       // Tolerância de 10% para alerta
    haltOnFailure.set(false)                        // Não falha o build em caso de violação
}
``````

---

## 🚀 Guia Rápido de Uso e Regras

Antes de submeter código ou considerar uma tarefa como finalizada, você DEVE rodar, de forma sequencial, as verificações abaixo:

1. `./gradlew spotlessApply` *(Formata o código e arquivos de configuração)*
2. `./gradlew detekt` *(Roda a análise estática em busca de code smells - falhas não devem ser ignoradas via `@Suppress`)*
3. `./gradlew build` *(Garante a compilação total do projeto)*

> Consulte o arquivo **[AGENTS.md](./AGENTS.md)** para ler detalhadamente todas as regras do repositório, incluindo o gerenciamento do `libs.versions.toml` através das Skills deste template.
