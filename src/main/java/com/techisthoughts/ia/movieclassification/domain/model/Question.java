package com.techisthoughts.ia.movieclassification.domain.model;

import java.util.Objects;

/**
 * Domain entity representing a generated question about movie chunks
 */
public class Question {

    private final String questionId;
    private final String questionText;
    private final String chunkId;
    private final QuestionType questionType;
    private final Difficulty difficulty;
    private final String answer;
    private final double confidence;
    private final String category;

    public Question(String questionId, String questionText, String chunkId, QuestionType questionType,
                    Difficulty difficulty, String answer, double confidence, String category) {
        this.questionId = Objects.requireNonNull(questionId, "Question ID cannot be null");
        this.questionText = Objects.requireNonNull(questionText, "Question text cannot be null");
        this.chunkId = Objects.requireNonNull(chunkId, "Chunk ID cannot be null");
        this.questionType = Objects.requireNonNull(questionType, "Question type cannot be null");
        this.difficulty = Objects.requireNonNull(difficulty, "Difficulty cannot be null");
        this.answer = answer;
        this.confidence = Math.max(0.0, Math.min(1.0, confidence));
        this.category = category;
    }

    // Getters
    public String getQuestionId() { return questionId; }
    public String getQuestionText() { return questionText; }
    public String getChunkId() { return chunkId; }
    public QuestionType getQuestionType() { return questionType; }
    public Difficulty getDifficulty() { return difficulty; }
    public String getAnswer() { return answer; }
    public double getConfidence() { return confidence; }
    public String getCategory() { return category; }

    /**
     * Creates embedding text for the question
     */
    public String toEmbeddingText() {
        StringBuilder text = new StringBuilder();
        text.append("Question: ").append(questionText);

        if (answer != null && !answer.trim().isEmpty()) {
            text.append(" | Answer: ").append(answer);
        }

        text.append(" | Type: ").append(questionType.getValue());
        text.append(" | Difficulty: ").append(difficulty.getValue());

        if (category != null && !category.trim().isEmpty()) {
            text.append(" | Category: ").append(category);
        }

        return text.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(questionId, question.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId);
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionId='" + questionId + '\'' +
                ", questionType=" + questionType +
                ", difficulty=" + difficulty +
                '}';
    }

    /**
     * Question types for categorization
     */
    public enum QuestionType {
        FACTUAL("factual"),
        ANALYTICAL("analytical"),
        COMPARATIVE("comparative"),
        DESCRIPTIVE("descriptive");

        private final String value;

        QuestionType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Difficulty levels for questions
     */
    public enum Difficulty {
        EASY("easy"),
        MEDIUM("medium"),
        HARD("hard");

        private final String value;

        Difficulty(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
