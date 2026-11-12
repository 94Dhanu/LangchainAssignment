package com.example.langchain4j.assignment4;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.example.langchain4j.common.OllamaConfig;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

/**
 * Assignment 4: Company Policy Question Answering System using RAG.
 *
 * Policy documents under src/main/resources/policies are loaded, split into
 * chunks, embedded and stored in an in-memory vector store. For each user
 * question the most relevant chunks are retrieved and injected into the LLM
 * prompt as context. If no chunk is relevant, or the model cannot answer
 * from the retrieved context, the user is told the information is not
 * available in the documents.
 * Type "exit" to quit.
 */
public class PolicyAssistantApp {

    private static final double MIN_RELEVANCE_SCORE = 0.55;
    private static final int MAX_RESULTS = 4;

    private static final String PROMPT_TEMPLATE = """
            You are a company policy assistant. Answer the employee's question using
            ONLY the policy excerpts below. If the excerpts do not contain the answer,
            reply exactly with: "Sorry, this information is not available in the
            company policy documents." Do not use any outside knowledge.

            POLICY EXCERPTS:
            %s

            QUESTION: %s
            """;

    public static void main(String[] args) throws Exception {
        EmbeddingModel embeddingModel = OllamaConfig.embeddingModel();
        OllamaChatModel chatModel = OllamaConfig.chatModel();
        InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();

        // 1. Load the policy documents from the classpath.
        URL policiesUrl = PolicyAssistantApp.class.getResource("/policies");
        if (policiesUrl == null) {
            throw new IllegalStateException("Policies folder not found on the classpath");
        }
        Path policiesDir = Paths.get(policiesUrl.toURI());
        List<Document> documents = FileSystemDocumentLoader.loadDocuments(
                policiesDir, new TextDocumentParser());
        System.out.println("Loaded " + documents.size() + " policy documents.");

        // 2. Split documents into chunks, embed them, and store in the vector store.
        List<TextSegment> segments = documents.stream()
                .flatMap(doc -> DocumentSplitters.recursive(400, 60).split(doc).stream())
                .toList();
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        store.addAll(embeddings, segments);
        System.out.println("Indexed " + segments.size() + " document chunks.");

        System.out.println("=== Company Policy Assistant ===");
        System.out.println("Ask about leave, attendance, work-from-home or benefits.");
        System.out.println("Type 'exit' to quit.");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("\nQuestion: ");
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

                // 3. Retrieve the most relevant chunks for the question.
                Embedding queryEmbedding = embeddingModel.embed(question).content();
                List<EmbeddingMatch<TextSegment>> matches = store.search(
                        EmbeddingSearchRequest.builder()
                                .queryEmbedding(queryEmbedding)
                                .maxResults(MAX_RESULTS)
                                .minScore(MIN_RELEVANCE_SCORE)
                                .build())
                        .matches();

                // 4. If nothing relevant was found, the answer is unavailable.
                if (matches.isEmpty()) {
                    System.out.println("Sorry, this information is not available "
                            + "in the company policy documents.");
                    continue;
                }

                // 5. Build the prompt with retrieved context and ask the LLM.
                String context = matches.stream()
                        .map(m -> m.embedded().text())
                        .collect(Collectors.joining("\n---\n"));
                System.out.println(chatModel.chat(PROMPT_TEMPLATE.formatted(context, question)));
            }
        }
    }
}
