# 🧠 AppQuiz - Trivia Challenge

![Demonstração do App](caminho/para/seu/arquivo.gif)

[![Nível](https://img.shields.io/badge/N%C3%ADvel-Iniciante%20Pro-blue)](#)
[![Tech](https://img.shields.io/badge/Tech-Android%20Java-blue)](#)
[![API](https://img.shields.io/badge/API-Open%20Trivia%20DB-green)](#)

O **AppQuiz** é um aplicativo Android desenvolvido em Java que desafia o conhecimento do usuário com perguntas dinâmicas. O projeto nasceu como um exercício do curso do **Tito Petri (Udemy)** focado em listas e manipulação de Views, mas foi expandido por iniciativa própria para integrar consumo de API real e recursos avançados de UX.


## 🚀 Diferenciais deste Projeto (Além do Curso)

Enquanto a proposta original do curso era trabalhar com dados fixos, decidi implementar:
- **Consumo de API Real**: Integração direta com a [Open Trivia DB](https://opentdb.com/) via `HttpURLConnection`.
- **Feedback Assíncrono**: Uso de `ProgressBar` para indicar o carregamento dos dados durante a requisição `GET`.
- **Validação Visual**: Sistema de correção em tempo real que indica acertos e erros visualmente.
- **Persistência de Score**: Lógica para contabilizar acertos e exibir o resultado final em um `AlertDialog` personalizado.

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Java
- **IDE**: Android Studio
- **Comunicação de Rede**: `HttpURLConnection` para requisições REST.
- **Interface**: `ConstraintLayout`, `RadioGroup`, `ProgressBar` e `AlertDialog`.
- **API Externa**: Open Trivia DB (Múltipla escolha).

## 🧩 Implementação Técnica

### Integração com API
Para buscar as 10 perguntas dinâmicas, utilizei a classe `URL` do Java para estabelecer uma conexão segura e recuperar os dados em formato JSON diretamente da fonte.

```java
URL url = new URL("[https://opentdb.com/api.php?amount=10&type=multiple](https://opentdb.com/api.php?amount=10&type=multiple)");
HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
conexao.setRequestMethod("GET");
conexao.connect();

