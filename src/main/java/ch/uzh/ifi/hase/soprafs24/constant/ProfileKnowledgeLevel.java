package ch.uzh.ifi.hase.soprafs24.constant;

public enum ProfileKnowledgeLevel {
    BEGINNER, INTERMEDIATE, ADVANCED;

    public static ProfileKnowledgeLevel fromString(String level) {
        if (level == null) {
            throw new IllegalArgumentException("Level string is null");
        }

        return switch (level.trim().toUpperCase()) {
            case "BEGINNER" -> BEGINNER;
            case "INTERMEDIATE" -> INTERMEDIATE;
            case "ADVANCED" -> ADVANCED;
            default -> throw new IllegalArgumentException("Unknown level: " + level);
        };
    }
}


