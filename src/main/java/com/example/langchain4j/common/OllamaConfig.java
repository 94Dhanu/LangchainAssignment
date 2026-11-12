package com.example.langchain4j.common;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;

/**
 * Central place for Ollama connection settings shared by all assignments.
 * Start Ollama with "ollama serve" and pull the models below before running.
 */
public final class OllamaConfig {

    public static final String BASE_URL = "http://localhost:11434";
    public static final String CHAT_MODEL = "llama3.2:3b";
    public static final String EMBEDDING_MODEL = "nomic-embed-text";

    private OllamaConfig() {
    }

    public static OllamaChatModel chatModel() {
        return OllamaChatModel.builder()
                .baseUrl(BASE_URL)
                .modelName(CHAT_MODEL)
                .temperature(0.3)
                .build();
    }

    public static EmbeddingModel embeddingModel() {
        return OllamaEmbeddingModel.builder()
                .baseUrl(BASE_URL)
                .modelName(EMBEDDING_MODEL)
                .build();
    }
}
