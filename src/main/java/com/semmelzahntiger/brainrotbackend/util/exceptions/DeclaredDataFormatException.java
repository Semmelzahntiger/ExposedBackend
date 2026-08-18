package com.semmelzahntiger.brainrotbackend.util.exceptions;

public class DeclaredDataFormatException extends RuntimeException {
    public DeclaredDataFormatException(String message) {
        super(message);
    }
    public DeclaredDataFormatException() {
        super("Declared file format does not match with actual file format");
    }
}
