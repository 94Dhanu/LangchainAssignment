# LangChain4j Hands-on Assignments (Core Java + LangChain4j + Ollama)

Five console applications demonstrating core LangChain4j capabilities on top of
a locally running Ollama LLM.

| # | Assignment | Main class | Concepts |
|---|------------|------------|----------|
| 1 | AI Chat Application | `com.assignments.chat.ChatApplication` | `OllamaChatModel`, `ChatMemory`, console loop |
| 2 | AI Text Summarizer | `com.assignments.summarizer.TextSummarizer` | Prompt engineering, bullet summaries, keywords, takeaways |
| 3 | Employee Search via Embeddings | `com.assignments.search.EmployeeSearchApplication` | `OllamaEmbeddingModel`, `InMemoryEmbeddingStore`, similarity search |
| 4 | Company Policy Q&A (RAG) | `com.assignments.rag.PolicyAssistantApplication` | Document loading, splitting, embedding, retrieval, grounded answers |
| 5 | Employee Assistant (AI Services + Tools) | `com.assignments.assistant.EmployeeAssistantApplication` | `AiServices`, `@Tool` methods, `@SystemMessage`, `ChatMemory` |

## Prerequisites

1. **Java 17+** and **Maven**
2. **Ollama** running locally with the required models:

   ```bash
   # install Ollama (https://ollama.com) and start it
   ollama serve

   # chat model used by assignments 1, 2, 4, 5
   ollama pull llama3.2

   # embedding model used by assignments 3 and 4
   ollama pull nomic-embed-text
   ```

   LangChain4j connects to Ollama at `http://localhost:11434` (the default).

## Build

```bash
mvn compile
```

## Run

```bash
mvn -q compile exec:java -Dexec.mainClass=com.assignments.chat.ChatApplication
mvn -q compile exec:java -Dexec.mainClass=com.assignments.summarizer.TextSummarizer
mvn -q compile exec:java -Dexec.mainClass=com.assignments.search.EmployeeSearchApplication
mvn -q compile exec:java -Dexec.mainClass=com.assignments.rag.PolicyAssistantApplication
mvn -q compile exec:java -Dexec.mainClass=com.assignments.assistant.EmployeeAssistantApplication
```

(Or run the `main` methods directly from your IDE.)

## Notes

- **Assignment 1**: type `exit` to quit; the last 20 messages are kept in a
  `MessageWindowChatMemory`, so follow-up questions work.
- **Assignment 2**: paste text, then finish input with a line containing only
  `END`. Choose `[s]hort`, `[m]edium` or `[d]etailed` summaries.
- **Assignment 3**: the shared employee dataset lives in
  `com.assignments.common` (`E101`–`E110`). Queries match on meaning, not
  keywords — try "who is good with machine learning".
- **Assignment 4**: policy documents are in `src/main/resources/policies/`
  (leave, attendance, work-from-home, benefits). Questions outside the corpus
  get the "information is not available" reply.
- **Assignment 5**: tool calling quality depends on the model; `llama3.2`
  supports tools in Ollama. Example questions are printed when the app starts.
