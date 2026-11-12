package com.assignments.common;

import java.util.List;

/**
 * Sample employee record shared by Assignment 3 (semantic search)
 * and Assignment 5 (AI tools).
 */
public record Employee(
        String id,
        String name,
        String department,
        String designation,
        List<String> skills,
        double monthlySalary,
        int totalLeaves,
        int leavesUsed) {

    public int leaveBalance() {
        return totalLeaves - leavesUsed;
    }

    /** Plain-text representation used for embedding and for tool responses. */
    public String describe() {
        return "Employee ID: " + id
                + ", Name: " + name
                + ", Department: " + department
                + ", Designation: " + designation
                + ", Skills: " + String.join(", ", skills);
    }

    @Override
    public String toString() {
        return describe()
                + ", Monthly Salary: " + monthlySalary
                + ", Leave Balance: " + leaveBalance() + "/" + totalLeaves;
    }
}
