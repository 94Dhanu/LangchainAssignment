package com.example.langchain4j.assignment5;

import dev.langchain4j.service.SystemMessage;

/**
 * AI Service interface for the Employee Assistant. LangChain4j generates the
 * implementation at runtime, routes tool calls to {@link EmployeeTools} and
 * keeps the conversation history in the configured ChatMemory.
 */
public interface EmployeeAssistant {

    @SystemMessage("""
            You are a helpful employee assistant for a company.
            You can look up employee details by employee ID, calculate annual
            salary from a monthly salary, check leave balances, and compute the
            remaining leave after leave usage, using the tools provided to you.
            Always use a tool when the request needs employee data or a
            calculation. If a tool reports that no employee was found, tell the
            user plainly. Keep answers concise and friendly.
            """)
    String chat(String userMessage);
}
