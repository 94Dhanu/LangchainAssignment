package com.assignments.common;

import java.util.List;
import java.util.Optional;

/** In-memory sample employee dataset. */
public final class EmployeeDirectory {

    private static final List<Employee> EMPLOYEES = List.of(
            new Employee("E101", "Ananya Sharma", "Engineering", "Senior Software Engineer",
                    List.of("Java", "Spring Boot", "Microservices", "Kafka"), 120000, 24, 6),
            new Employee("E102", "Rahul Verma", "Engineering", "DevOps Engineer",
                    List.of("Docker", "Kubernetes", "AWS", "Terraform", "CI/CD"), 95000, 24, 10),
            new Employee("E103", "Priya Nair", "Data Science", "Data Scientist",
                    List.of("Python", "Machine Learning", "Pandas", "SQL", "TensorFlow"), 110000, 24, 4),
            new Employee("E104", "Amit Patel", "Human Resources", "HR Manager",
                    List.of("Recruitment", "Employee Relations", "Payroll", "Onboarding"), 85000, 24, 12),
            new Employee("E105", "Sneha Kulkarni", "Quality Assurance", "QA Lead",
                    List.of("Selenium", "Test Automation", "JUnit", "API Testing"), 90000, 24, 8),
            new Employee("E106", "Vikram Reddy", "Engineering", "Frontend Developer",
                    List.of("React", "TypeScript", "CSS", "Next.js"), 78000, 24, 2),
            new Employee("E107", "Meera Iyer", "Finance", "Financial Analyst",
                    List.of("Excel", "Financial Modeling", "SAP", "Budgeting"), 82000, 24, 15),
            new Employee("E108", "Arjun Desai", "Data Science", "ML Engineer",
                    List.of("Python", "Deep Learning", "LangChain", "LLM", "NLP"), 115000, 24, 5),
            new Employee("E109", "Kavya Menon", "Marketing", "Digital Marketing Specialist",
                    List.of("SEO", "Content Marketing", "Google Analytics", "Social Media"), 65000, 24, 9),
            new Employee("E110", "Rohan Joshi", "Engineering", "Backend Developer",
                    List.of("Java", "LangChain4j", "PostgreSQL", "Redis", "REST API"), 88000, 24, 7));

    private EmployeeDirectory() {
    }

    public static List<Employee> all() {
        return EMPLOYEES;
    }

    public static Optional<Employee> findById(String employeeId) {
        if (employeeId == null) {
            return Optional.empty();
        }
        String normalized = employeeId.trim().toUpperCase();
        return EMPLOYEES.stream()
                .filter(e -> e.id().equalsIgnoreCase(normalized))
                .findFirst();
    }
}
