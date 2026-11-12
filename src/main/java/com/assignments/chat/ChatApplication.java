package com.assignments.chat;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.util.Scanner;

/**
 * Assignment 1 - AI Chat Application.
 *
 * Console chat loop backed by an Ollama-hosted LLM through LangChain4j.
 * Keeps the last N messages in a ChatMemory so the model sees conversation
 * context. Type "exit" to quit.
 */
public class ChatApplication {

    public static void main(String[] args) {
        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2")
                .temperature(0.7)
                .build();

        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(20);

        System.out.println("=== AI Chat Application (Ollama + LangChain4j) ===");
        System.out.println("Ask anything. Type 'exit' to quit.\n");

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

                memory.add(UserMessage.from(question));

                AiMessage answer = model.chat(ChatRequest.builder()
                                .messages(memory.messages())
                                .build())
                        .aiMessage();
                memory.add(answer);

                System.out.println("AI: " + answer.text() + "\n");
            }
        }
    }
}
