package com.example.demo.exception;

public class GoalLimitExceededException extends RuntimeException {
    public GoalLimitExceededException(String message) {
        super(message);
    }
}
