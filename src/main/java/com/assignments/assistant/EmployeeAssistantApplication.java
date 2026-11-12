package com.assignments.assistant;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

import java.util.Scanner;

/**
 * Assignment 5 - AI-Powered Employee Assistant (AI Services + Tools).
 *
 * An {@link AiServices}-generated implementation of {@link EmployeeAssistant}
 * lets the LLM call the Java methods in {@link EmployeeTools} to answer
 * natural-language requests, while a ChatMemory keeps conversation context.
 */
public class EmployeeAssistantApplication {

    interface EmployeeAssistant {
        @SystemMessage("""
                You are a helpful HR employee assistant.
                You help employees look up employee information, work out
                salary figures, and check leave balances.
                Always use the provided tools to fetch employee data or do
                calculations - never make up employee IDs, salaries or leave
                balances. If a tool reports that an employee does not exist,
                tell the user politely and suggest checking the employee ID.
                Keep answers concise and friendly.
                """)
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2")
                .temperature(0.2)
                .build();

        EmployeeAssistant assistant = AiServices.builder(EmployeeAssistant.class)
                .chatModel(model)
                .tools(new EmployeeTools())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        System.out.println("=== AI Employee Assistant (Tools + ChatMemory) ===");
        System.out.println("Try: \"Who is E105?\", \"What is the annual salary for 95000 a month?\",");
        System.out.println("     \"How many leaves does E107 have left?\", \"If E101 takes 5 more leaves, what remains?\"");
        System.out.println("Type 'exit' to quit.\n");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("You: ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String message = scanner.nextLine().trim();
                if (message.isEmpty()) {
                    continue;
                }
                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye!");
                    break;
                }
                System.out.println("Assistant: " + assistant.chat(message) + "\n");
            }
        }
    }
}
