package com.assignments.search;

import com.assignments.common.Employee;
import com.assignments.common.EmployeeDirectory;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.List;
import java.util.Scanner;

/**
 * Assignment 3 - Employee Information Search Using Embeddings.
 *
 * Each employee description is embedded with an Ollama embedding model and
 * stored in an in-memory embedding store. Natural-language queries are
 * embedded and matched by cosine similarity, so searches like
 * "who knows machine learning" find the right employees without exact
 * keyword matching.
 */
public class EmployeeSearchApplication {

    public static void main(String[] args) {
        OllamaEmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("nomic-embed-text")
                .build();

        InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();

        // Convert every employee description into an embedding and index it.
        List<TextSegment> segments = EmployeeDirectory.all().stream()
                .map(e -> TextSegment.from(e.describe(), new Metadata().put("employeeId", e.id())))
                .toList();
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        store.addAll(embeddings, segments);
        System.out.println("Indexed " + segments.size() + " employee profiles.\n");

        System.out.println("=== Employee Semantic Search (Ollama embeddings + LangChain4j) ===");
        System.out.println("e.g. \"Who works with machine learning?\" or \"people good at DevOps\"");
        System.out.println("Type 'exit' to quit.\n");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("Search: ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String query = scanner.nextLine().trim();
                if (query.isEmpty()) {
                    continue;
                }
                if (query.equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye!");
                    break;
                }

                Embedding queryEmbedding = embeddingModel.embed(query).content();
                List<EmbeddingMatch<TextSegment>> matches = store.search(EmbeddingSearchRequest.builder()
                                .queryEmbedding(queryEmbedding)
                                .maxResults(3)
                                .minScore(0.4)
                                .build())
                        .matches();

                if (matches.isEmpty()) {
                    System.out.println("No relevant employees found.\n");
                    continue;
                }

                System.out.println("\nTop " + matches.size() + " matches:");
                for (EmbeddingMatch<TextSegment> match : matches) {
                    String employeeId = match.embedded().metadata().getString("employeeId");
                    Employee employee = EmployeeDirectory.findById(employeeId).orElse(null);
                    System.out.printf("  [score %.3f] %s%n", match.score(),
                            employee != null ? employee.toString() : match.embedded().text());
                }
                System.out.println();
            }
        }
    }
}
