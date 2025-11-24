🌟 AuraLearn — Seu Hub Inteligente de Estudos

![Build](https://img.shields.io/badge/Gradle-Build-brightgreen?logo=gradle) ![Kotlin](https://img.shields.io/badge/Kotlin-1.9-blue?logo=kotlin) ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Enabled-5C6FFF?logo=android) ![Firebase](https://img.shields.io/badge/Firebase-Integrated-FFCA28?logo=firebase) ![OpenAI](https://img.shields.io/badge/OpenAI-Enabled-black?logo=openai)

AuraLearn é um aplicativo Android focado em produtividade, estudo assistido por IA e organização pessoal.
Gerencie seu tempo, receba ajuda de um tutor inteligente e acompanhe sua evolução — tudo em um único lugar.

✨ Principais Funcionalidades
🔐 Autenticação

Login com Google

Login e cadastro com e-mail/senha

Recuperação de senha ("Esqueci minha senha")

🤖 Tutoria com IA

Chat Tutor para dúvidas, explicações e resumos

Geração automatizada de planos de estudo personalizados

🏠 Organização & Produtividade

Home com visão geral do progresso

Adicionar tarefas, matérias e gerenciar estudos

Notificações (tarefas, lembretes e Pomodoro)

⏱️ Pomodoro

Timer Pomodoro funcional

Histórico de sessões

Integração com o sistema de relatórios

📊 Relatórios

Relatório semanal de produtividade

Gráficos e indicadores de foco

🎨 Aparência

Tema claro e tema escuro (Material 3)

Interface moderna com animações

🛠️ Tecnologias Utilizadas
Área	Tecnologias
Linguagem	Kotlin
UI	Jetpack Compose, Material 3
Backend	Firebase Authentication, Firestore
Autenticação	Google Sign-In
IA	OpenAI API (via com.aallam.openai)
Arquitetura	ViewModel, StateFlow, Coroutines
Outros	Navigation Compose, WorkManager
⚡ Instalação (Desenvolvimento Local)
1. Clone o repositório
git clone <REPO_URL>
cd auraLearn

2. Abra o projeto no Android Studio

File → Open → pasta do projeto

🔥 Configuração
Firebase


Caso utilize outro projeto Firebase, substitua-o pelo seu.

OpenAI (Chat Tutor)

local.properties (NÃO comitar):

OPENAI_API_KEY=sk-xxxxxxxx


E no app/build.gradle.kts:

buildConfigField("String", "OPENAI_API_KEY", "\"${project.properties["OPENAI_API_KEY"]}\"")

🚀 Build & Execução
Via Gradle (Windows):
./gradlew.bat assembleDebug
./gradlew.bat installDebug


Ou simplesmente use Run ▶ no Android Studio.

📂 Estrutura do Projeto
app/
 ├── src/main/java/com/eliel/studytrack   # Código-fonte
 ├── src/main/res                         # Recursos do app
 ├── google-services.json                 # Configuração Firebase
gradle/libs.versions.toml                 # Gerenciamento de dependências

🔒 Segurança

Não comite chaves privadas.

Utilize local.properties, environment variables ou secrets no CI/CD.