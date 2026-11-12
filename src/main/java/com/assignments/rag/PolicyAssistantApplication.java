package com.assignments.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Assignment 4 - Company Policy Q&A using Retrieval-Augmented Generation.
 *
 * Policy text files are loaded from the classpath, split into chunks,
 * embedded and stored in an in-memory vector store. For every question the
 * most relevant chunks are retrieved and injected into the prompt, so the
 * LLM answers from the documents instead of its own knowledge.
 */
public class PolicyAssistantApplication {

    interface PolicyAssistant {
        @SystemMessage("""
                You are a company policy assistant.
                Answer the employee's question strictly based on the policy
                document content provided to you as context.
                Keep answers short and precise, and mention which policy the
                answer comes from.
                If the answer cannot be found in the provided context, reply
                exactly: "Sorry, this information is not available in the
                company policy documents."
                """)
        String chat(String question);
    }

    public static void main(String[] args) throws Exception {
        OllamaChatModel chatModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2")
                .temperature(0.2)
                .build();

        OllamaEmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("nomic-embed-text")
                .build();

        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 1. Load the policy documents bundled on the classpath.
        URL policiesUrl = Objects.requireNonNull(
                PolicyAssistantApplication.class.getClassLoader().getResource("policies"),
                "policies directory not found on classpath");
        Path policiesDir = Paths.get(policiesUrl.toURI());
        List<Document> documents = FileSystemDocumentLoader.loadDocumentsRecursively(
                policiesDir, new TextDocumentParser());

        // 2. Split into chunks, embed, and store in the vector store.
        EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(400, 60))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build()
                .ingest(documents);
        System.out.println("Loaded " + documents.size() + " policy documents into the vector store.\n");

        // 3. Retriever: finds the chunks most similar to each question.
        ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.5)
                .build();

        PolicyAssistant assistant = AiServices.builder(PolicyAssistant.class)
                .chatModel(chatModel)
                .contentRetriever(retriever)
                .build();

        System.out.println("=== Company Policy Assistant (RAG) ===");
        System.out.println("Ask about leave, attendance, work from home or benefits.");
        System.out.println("Type 'exit' to quit.\n");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("You: ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String question = scanner.nextLine().trim();
                if (question.isEmpty()) {
                    continue;
                }
                if (question.equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye!");
                    break;
                }
                System.out.println("Assistant: " + assistant.chat(question) + "\n");
            }
        }
    }
}
