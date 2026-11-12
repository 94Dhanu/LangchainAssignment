package com.example.langchain4j.assignment3;

import java.util.List;
import java.util.Scanner;

import com.example.langchain4j.common.OllamaConfig;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

/**
 * Assignment 3: Employee Information Search Using Embeddings.
 *
 * A sample employee dataset is converted into text segments, embedded with
 * an Ollama embedding model and stored in an in-memory embedding store.
 * Natural-language queries are embedded the same way and matched by
 * cosine similarity, so results are based on meaning rather than keywords.
 * Type "exit" to quit.
 */
public class EmployeeSearchApp {

    record Employee(String id, String name, String department, String designation, String skills) {
        String toSearchableText() {
            return "Employee ID: " + id
                    + ". Name: " + name
                    + ". Department: " + department
                    + ". Designation: " + designation
                    + ". Skills: " + skills + ".";
        }
    }

    private static final List<Employee> EMPLOYEES = List.of(
            new Employee("E101", "Alice Johnson", "Engineering", "Senior Software Engineer",
                    "Java, Spring Boot, Microservices, SQL, Docker"),
            new Employee("E102", "Bob Smith", "Engineering", "DevOps Engineer",
                    "Kubernetes, AWS, Terraform, CI/CD, Linux"),
            new Employee("E103", "Carol White", "Human Resources", "HR Manager",
                    "Recruitment, Employee Relations, Payroll, Onboarding"),
            new Employee("E104", "David Brown", "Finance", "Financial Analyst",
                    "Accounting, Excel, Budgeting, Financial Modeling, SAP"),
            new Employee("E105", "Emma Davis", "Engineering", "Data Scientist",
                    "Python, Machine Learning, TensorFlow, Pandas, Statistics"),
            new Employee("E106", "Frank Miller", "Marketing", "Digital Marketing Specialist",
                    "SEO, Content Marketing, Google Analytics, Social Media"),
            new Employee("E107", "Grace Lee", "Engineering", "Frontend Developer",
                    "React, TypeScript, CSS, HTML, UI/UX Design"),
            new Employee("E108", "Henry Wilson", "Finance", "Accountant",
                    "Bookkeeping, Tax, Auditing, QuickBooks, Compliance"),
            new Employee("E109", "Ivy Chen", "Human Resources", "Recruiter",
                    "Talent Acquisition, Interviewing, LinkedIn Recruiting, Negotiation"),
            new Employee("E110", "Jack Taylor", "Marketing", "Marketing Manager",
                    "Brand Strategy, Campaign Management, Market Research, Team Leadership"));

    public static void main(String[] args) {
        EmbeddingModel embeddingModel = OllamaConfig.embeddingModel();
        InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();

        // Convert each employee into a TextSegment and store its embedding.
        List<TextSegment> segments = EMPLOYEES.stream()
                .map(e -> TextSegment.from(e.toSearchableText()))
                .toList();
        Response<List<Embedding>> embeddings = embeddingModel.embedAll(segments);
        store.addAll(embeddings.content(), segments);
        System.out.println("Indexed " + segments.size() + " employee records.");

        System.out.println("=== Employee Information Search ===");
        System.out.println("Ask things like: 'Who knows machine learning?' or 'employees in finance'.");
        System.out.println("Type 'exit' to quit.");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("\nQuery: ");
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
                EmbeddingSearchResult<TextSegment> result = store.search(EmbeddingSearchRequest.builder()
                        .queryEmbedding(queryEmbedding)
                        .maxResults(3)
                        .minScore(0.5)
                        .build());

                List<EmbeddingMatch<TextSegment>> matches = result.matches();
                if (matches.isEmpty()) {
                    System.out.println("No relevant employees found.");
                    continue;
                }
                System.out.println("Top matches:");
                for (EmbeddingMatch<TextSegment> match : matches) {
                    System.out.printf("  [score %.3f] %s%n", match.score(), match.embedded().text());
                }
            }
        }
    }
}
