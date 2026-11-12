package com.example.langchain4j.assignment2;

import java.util.Scanner;

import com.example.langchain4j.common.OllamaConfig;

import dev.langchain4j.model.ollama.OllamaChatModel;

/**
 * Assignment 2: AI Text Summarizer.
 *
 * Accepts lengthy text from the console (end input with a line containing
 * only "END"), then produces:
 *   - a bullet-point summary at the requested length (short/medium/detailed)
 *   - important keywords
 *   - key takeaways
 * The user can summarize another text or type "exit" to quit.
 */
public class TextSummarizer {

    private static final String SUMMARY_PROMPT = """
            You are an expert text summarizer.
            Summarize the following text as a %s summary in bullet points.
            Use '-' for each bullet. Do not add anything beyond the bullet list.

            TEXT:
            %s
            """;

    private static final String KEYWORDS_PROMPT = """
            Analyze the following text and respond in exactly this format:

            KEYWORDS: <comma-separated list of the most important keywords>
            KEY TAKEAWAYS:
            - <takeaway 1>
            - <takeaway 2>
            - <takeaway 3>

            TEXT:
            %s
            """;

    public static void main(String[] args) {
        OllamaChatModel model = OllamaConfig.chatModel();

        System.out.println("=== AI Text Summarizer ===");
        System.out.println("Paste the text to summarize, then type END on a new line.");
        System.out.println("Type 'exit' at any prompt to quit.");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String text = readText(scanner);
                if (text == null) {
                    break; // user typed exit or EOF
                }
                if (text.isBlank()) {
                    System.out.println("No text provided, try again.");
                    continue;
                }

                String length = readSummaryLength(scanner);
                if (length == null) {
                    break;
                }

                System.out.println("\n--- Summary (" + length + ") ---");
                System.out.println(model.chat(SUMMARY_PROMPT.formatted(length, text)));

                System.out.println("\n--- Keywords & Key Takeaways ---");
                System.out.println(model.chat(KEYWORDS_PROMPT.formatted(text)));

                System.out.print("\nSummarize another text? (yes/no): ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String again = scanner.nextLine().trim();
                if (again.equalsIgnoreCase("exit") || !again.equalsIgnoreCase("yes")) {
                    System.out.println("Goodbye!");
                    break;
                }
            }
        }
    }

    /** Reads lines until a line containing only "END". Returns null on 'exit'/EOF. */
    private static String readText(Scanner scanner) {
        System.out.println("\nEnter text (END on a new line to finish):");
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().equalsIgnoreCase("exit")) {
                return null;
            }
            if (line.trim().equalsIgnoreCase("END")) {
                break;
            }
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    /** Returns "short", "medium" or "detailed"; null if the user wants out. */
    private static String readSummaryLength(Scanner scanner) {
        while (true) {
            System.out.print("Summary length (short/medium/detailed): ");
            if (!scanner.hasNextLine()) {
                return null;
            }
            String choice = scanner.nextLine().trim().toLowerCase();
            if (choice.equals("exit")) {
                return null;
            }
            if (choice.equals("short") || choice.equals("medium") || choice.equals("detailed")) {
                return choice;
            }
            System.out.println("Please enter 'short', 'medium' or 'detailed'.");
        }
    }
}
