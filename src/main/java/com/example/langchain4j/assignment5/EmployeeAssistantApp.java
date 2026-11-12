package com.example.langchain4j.assignment5;

import java.util.Scanner;

import com.example.langchain4j.common.OllamaConfig;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;

/**
 * Assignment 5: AI-Powered Employee Assistant using AI Services and Tools.
 *
 * The EmployeeAssistant interface is turned into a working service by
 * AiServices: the LLM decides when to call the Java methods exposed in
 * EmployeeTools, and MessageWindowChatMemory preserves conversation history
 * so follow-up questions work naturally.
 * Type "exit" to quit.
 */
public class EmployeeAssistantApp {

    public static void main(String[] args) {
        OllamaChatModel model = OllamaConfig.chatModel();

        EmployeeAssistant assistant = AiServices.builder(EmployeeAssistant.class)
                .chatModel(model)
                .tools(new EmployeeTools())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .build();

        System.out.println("=== AI Employee Assistant ===");
        System.out.println("Try: 'Show me details of employee E103',");
        System.out.println("     'What is the annual salary if monthly is 6100?',");
        System.out.println("     'How much leave does E105 have left?',");
        System.out.println("     'If Bob uses 4 days of leave, how many remain?'");
        System.out.println("Type 'exit' to quit.");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("\nYou: ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    continue;
                }
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye!");
                    break;
                }
                System.out.println("Assistant: " + assistant.chat(input));
            }
        }
    }
}
