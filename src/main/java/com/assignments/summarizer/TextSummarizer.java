package com.assignments.summarizer;

import dev.langchain4j.model.ollama.OllamaChatModel;

import java.util.Scanner;

/**
 * Assignment 2 - AI Text Summarizer.
 *
 * Reads a lengthy text from the console (finish input with a line
 * containing only "END"), asks for a summary length, then produces a
 * bullet-point summary, important keywords and key takeaways.
 */
public class TextSummarizer {

    public static void main(String[] args) {
        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2")
                .temperature(0.3)
                .build();

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("=== AI Text Summarizer (Ollama + LangChain4j) ===");

            while (true) {
                String text = readText(scanner);
                if (text.isBlank()) {
                    System.out.println("No text provided.");
                } else {
                    String length = readSummaryLength(scanner);
                    String prompt = buildPrompt(text, length);
                    System.out.println("\n--- Summary ---");
                    System.out.println(model.chat(prompt));
                    System.out.println("---------------\n");
                }

                System.out.print("Summarize another text? (yes/no): ");
                String again = scanner.nextLine().trim();
                if (!again.equalsIgnoreCase("yes") && !again.equalsIgnoreCase("y")) {
                    System.out.println("Goodbye!");
                    break;
                }
            }
        }
    }

    private static String readText(Scanner scanner) {
        System.out.println("\nPaste the text you want summarized.");
        System.out.println("When finished, type END on a new line and press Enter:");
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().equalsIgnoreCase("END")) {
                break;
            }
            sb.append(line).append('\n');
        }
        return sb.toString().trim();
    }

    private static String readSummaryLength(Scanner scanner) {
        while (true) {
            System.out.print("Summary length - [s]hort, [m]edium, [d]etailed: ");
            String choice = scanner.nextLine().trim().toLowerCase();
            switch (choice) {
                case "s", "short" -> { return "a very short summary of 2-3 bullet points"; }
                case "m", "medium" -> { return "a medium summary of about 5 bullet points"; }
                case "d", "detailed" -> { return "a detailed summary of 8-10 bullet points"; }
                default -> System.out.println("Please enter s, m or d.");
            }
        }
    }

    private static String buildPrompt(String text, String length) {
        return """
                You are an expert summarizer. Summarize the text below into %s.

                Respond using EXACTLY this structure:

                SUMMARY:
                - <bullet point>
                - <bullet point>

                KEYWORDS:
                <comma-separated list of the most important keywords>

                KEY TAKEAWAYS:
                - <bullet point>
                - <bullet point>

                TEXT:
                %s
                """.formatted(length, text);
    }
}
