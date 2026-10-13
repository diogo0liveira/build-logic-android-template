# Build Logic Android Template

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

Eles centralizam a lógica, evitando a repetição exaustiva de configurações nos arquivos `build.gradle.kts` de cada módulo do projeto. Abaixo, a lista dos principais plugins registrados e suas finalidades:

*   **`convention.root` (`RootConventionPlugin`)**
    Deve ser aplicado no `build.gradle.kts` principal da raiz do projeto. Ele aplica configurações gerais como o Spotless para arquivos `*.gradle.kts` e também registra e orquestra a agregação de relatórios de cobertura do JaCoco para todos os módulos.
*   **`convention.android.application` (`AndroidApplicationConventionPlugin`)**
    Configura os módulos finais executáveis do tipo "app". Ele aplica os padrões Android (SDK mínimo, alvo e compilação), assina as ferramentas de build (AGP) e integra as ferramentas de qualidade de código.
*   **`convention.android.library` (`AndroidLibraryConventionPlugin`)**
    Utilizado em módulos de biblioteca Android comuns (como `:module-core`).
*   **`convention.android.feature` (`AndroidFeatureConventionPlugin`)**
    Especialização de biblioteca para módulos de funcionalidades (features) do app (como `:module-feature`), frequentemente incluindo outras dependências utilitárias de UI.
*   **`convention.android.compose` (`AndroidComposeConventionPlugin`)**
    Aplica as dependências, as features de compilação e o plugin do compilador do Kotlin necessários para utilizar o Jetpack Compose em um módulo.
*   **`convention.kotlin.jvm` (`KotlinJvmConventionPlugin`)**
    Configura módulos 100% Kotlin (JVM), que não possuem nenhuma dependência das APIs do Android, ideal para regras de negócios puras ou utilitários (ex: `:module-jvm`).
*   **`convention.hilt` (`HiltConventionPlugin`)**
    Aplica o plugin de injeção de dependência Dagger/Hilt e o KSP (Kotlin Symbol Processing) para gerar os componentes necessários.

> *Nota:* Há outros plugins internos (como `SpotlessConventionPlugin`, `DetektConventionPlugin`, `JacocoAndroidConventionPlugin`, etc.) que são aplicados automaticamente por trás dos panos nos plugins citados acima.

---

## 📊 Relatório de Cobertura (JaCoco)

O projeto possui suporte integrado para gerar relatórios de cobertura de testes combinando a execução de testes em múltiplos módulos.

Para gerar o relatório agregado, basta rodar o comando abaixo na raiz do projeto:

```bash
./gradlew jacocoAggregatedReport
```

### Outras Tarefas de Cobertura Relevantes
Além do relatório agregado na raiz, os plugins de conveniência também disponibilizam tarefas granulares diretamente em cada módulo:

*   **Para Módulos Android (Exclusivo para a variante `debug`):**
    *   `debugUnitTestCoverage` (ex: `./gradlew :app:debugUnitTestCoverage`): Relatório apenas dos testes de unidade.
    *   `debugAndroidTestCoverage` (ex: `./gradlew :app:debugAndroidTestCoverage`): Relatório apenas dos testes instrumentados.
    *   `debugAggregateCoverage` (ex: `./gradlew :app:debugAggregateCoverage`): Relatório agregado (unidade + instrumentados) específico daquele módulo.
*   **Para Módulos JVM puros:**
    *   `reportJvmCoverage` (ex: `./gradlew :module-jvm:reportJvmCoverage`): Relatório de cobertura específico do módulo JVM.

### Como funciona
A agregação é feita por meio do plugin **`JacocoAggregationConventionPlugin`**, que é aplicado pelo plugin root. Esse plugin varre todos os módulos registrados e agrega seus arquivos de execução (arquivos `.exec` e `.ec`) em um único relatório HTML que exibe a cobertura geral do projeto.

**Onde configurar os módulos incluídos:**
Os módulos que devem fazer parte do relatório final são configurados diretamente no `build.gradle.kts` da raiz, usando a extensão `jacocoAggregation`. Exemplo:

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

O plugin raiz também cria um serviço (`CoverageReportLinkService`) que imprime automaticamente um link para o arquivo HTML no seu terminal (console) quando a task finaliza com sucesso.

---

## 🚀 Guia Rápido de Uso e Regras

Antes de submeter código ou considerar uma tarefa como finalizada, você DEVE rodar, de forma sequencial, as verificações abaixo:

1. `./gradlew spotlessApply` *(Formata o código e arquivos de configuração)*
2. `./gradlew detekt` *(Roda a análise estática em busca de code smells - falhas não devem ser ignoradas via `@Suppress`)*
3. `./gradlew build` *(Garante a compilação total do projeto)*

> Consulte o arquivo **[AGENTS.md](./AGENTS.md)** para ler detalhadamente todas as regras do repositório, incluindo o gerenciamento do `libs.versions.toml` através das Skills deste template.
