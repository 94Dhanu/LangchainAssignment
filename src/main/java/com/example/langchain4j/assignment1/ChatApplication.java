package com.example.langchain4j.assignment1;

import java.util.Scanner;

import com.example.langchain4j.common.OllamaConfig;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;

/**
 * Assignment 1: AI Chat Application.
 *
 * Console chat loop backed by an LLM running on Ollama via LangChain4j.
 * The user can ask multiple questions; the conversation keeps a sliding
 * window of chat memory so follow-up questions have context.
 * Type "exit" to quit.
 */
public class ChatApplication {

    public static void main(String[] args) {
        OllamaChatModel model = OllamaConfig.chatModel();
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(20);

        System.out.println("=== AI Chat Application ===");
        System.out.println("Model: " + OllamaConfig.CHAT_MODEL + " @ " + OllamaConfig.BASE_URL);
        System.out.println("Type your question and press Enter. Type 'exit' to quit.");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("\nYou: ");
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
                ChatResponse response = model.chat(memory.messages());
                AiMessage answer = response.aiMessage();
                memory.add(answer);

                System.out.println("AI: " + answer.text());
            }
        }
    }
}
