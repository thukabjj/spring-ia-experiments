package com.techisthoughts.ia.movieclassification.application.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.techisthoughts.ia.movieclassification.domain.model.MovieChunk;
import com.techisthoughts.ia.movieclassification.domain.model.Question;
import com.techisthoughts.ia.movieclassification.domain.port.LLMServicePort;

/**
 * Application service for generating questions from movie chunks
 */
@Service
public class QuestionGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionGenerationService.class);

    private final LLMServicePort llmService;

    public QuestionGenerationService(LLMServicePort llmService) {
        this.llmService = llmService;
    }

    /**
     * Generate questions for a movie chunk
     */
    public List<Question> generateQuestions(MovieChunk chunk, int questionCount) {
        logger.info("Generating {} questions for chunk: {}", questionCount, chunk.getChunkId());

        try {
            String prompt = buildQuestionPrompt(chunk, questionCount);
            String response = llmService.generateStructuredResponse(prompt, "json");

            return parseQuestionsFromResponse(response, chunk.getChunkId());

        } catch (Exception e) {
            logger.error("Error generating questions for chunk: {}", chunk.getChunkId(), e);
            return generateFallbackQuestions(chunk, questionCount);
        }
    }

    private String buildQuestionPrompt(MovieChunk chunk, int questionCount) {
        return String.format("""
            Based on the following movie content, generate exactly %d diverse questions that could be answered using this information.

            Content:
            %s

            For each question, provide:
            1. The question text
            2. Question type (factual, analytical, comparative, or descriptive)
            3. Difficulty level (easy, medium, or hard)
            4. A brief answer
            5. Category (genre, rating, story, characters, or general)

            Format your response as a JSON array with objects containing: questionText, questionType, difficulty, answer, category

            Example format:
            [
              {
                "questionText": "What genre is this movie?",
                "questionType": "factual",
                "difficulty": "easy",
                "answer": "Action",
                "category": "genre"
              }
            ]

            Generate %d questions now:
            """, questionCount, chunk.getContent(), questionCount);
    }

    private List<Question> parseQuestionsFromResponse(String response, String chunkId) {
        List<Question> questions = new ArrayList<>();

        try {
            // Simple regex-based parsing since we don't have complex JSON parsing
            Pattern questionPattern = Pattern.compile(
                "\"questionText\":\\s*\"([^\"]+)\".*?" +
                "\"questionType\":\\s*\"([^\"]+)\".*?" +
                "\"difficulty\":\\s*\"([^\"]+)\".*?" +
                "\"answer\":\\s*\"([^\"]+)\".*?" +
                "\"category\":\\s*\"([^\"]+)\"",
                Pattern.DOTALL
            );

            Matcher matcher = questionPattern.matcher(response);
            int questionNumber = 1;

            while (matcher.find() && questions.size() < 20) { // Limit to 20 questions max
                String questionText = matcher.group(1);
                String questionTypeStr = matcher.group(2);
                String difficultyStr = matcher.group(3);
                String answer = matcher.group(4);
                String category = matcher.group(5);

                Question.QuestionType questionType = parseQuestionType(questionTypeStr);
                Question.Difficulty difficulty = parseDifficulty(difficultyStr);

                String questionId = generateQuestionId(chunkId, questionNumber++);

                Question question = new Question(
                    questionId,
                    questionText,
                    chunkId,
                    questionType,
                    difficulty,
                    answer,
                    0.8, // Default confidence
                    category
                );

                questions.add(question);
            }

        } catch (Exception e) {
            logger.warn("Failed to parse structured response, falling back to simple parsing", e);
            return parseQuestionsSimple(response, chunkId);
        }

        return questions;
    }

    private List<Question> parseQuestionsSimple(String response, String chunkId) {
        List<Question> questions = new ArrayList<>();

        // Simple line-based parsing as fallback
        String[] lines = response.split("\n");
        int questionNumber = 1;

        for (String line : lines) {
            String trimmedLine = line.trim();
            if (isQuestionLine(trimmedLine) && questions.size() < 20) {
                String questionText = extractQuestionText(trimmedLine);
                if (!questionText.isEmpty()) {
                    String questionId = generateQuestionId(chunkId, questionNumber++);

                    Question question = new Question(
                        questionId,
                        questionText,
                        chunkId,
                        Question.QuestionType.FACTUAL, // Default type
                        Question.Difficulty.MEDIUM,    // Default difficulty
                        "Answer based on the content", // Default answer
                        0.6, // Lower confidence for simple parsing
                        "general" // Default category
                    );

                    questions.add(question);
                }
            }
        }

        return questions;
    }

    private boolean isQuestionLine(String line) {
        return line.contains("?") &&
               (line.matches("^\\d+\\..*") ||
                line.startsWith("-") ||
                line.startsWith("*") ||
                line.toLowerCase().startsWith("question"));
    }

    private String extractQuestionText(String line) {
        // Remove common prefixes and clean up
        String cleaned = line.replaceAll("^\\d+\\.\\s*", "")
                            .replaceAll("^[-*]\\s*", "")
                            .replaceAll("^Question:?\\s*", "")
                            .trim();
        return cleaned;
    }

    private List<Question> generateFallbackQuestions(MovieChunk chunk, int requestedCount) {
        logger.info("Generating fallback questions for chunk: {}", chunk.getChunkId());

        List<Question> questions = new ArrayList<>();
        List<String> templates = Arrays.asList(
            "What movies are included in this collection?",
            "What is the primary genre of these movies?",
            "What are the key highlights mentioned?",
            "What rating range do these movies fall into?",
            "What year range do these movies cover?",
            "What life insights are mentioned?",
            "How many movies are in this collection?",
            "What advice is provided by these movies?"
        );

        int questionNumber = 1;
        int questionsToGenerate = Math.min(requestedCount, templates.size());

        for (int i = 0; i < questionsToGenerate; i++) {
            String questionId = generateQuestionId(chunk.getChunkId(), questionNumber++);

            Question question = new Question(
                questionId,
                templates.get(i),
                chunk.getChunkId(),
                Question.QuestionType.FACTUAL,
                Question.Difficulty.EASY,
                "Based on the chunk content",
                0.5, // Lower confidence for templates
                "general"
            );

            questions.add(question);
        }

        return questions;
    }

    private Question.QuestionType parseQuestionType(String typeStr) {
        if (typeStr == null) return Question.QuestionType.FACTUAL;

        return switch (typeStr.toLowerCase()) {
            case "analytical" -> Question.QuestionType.ANALYTICAL;
            case "comparative" -> Question.QuestionType.COMPARATIVE;
            case "descriptive" -> Question.QuestionType.DESCRIPTIVE;
            default -> Question.QuestionType.FACTUAL;
        };
    }

    private Question.Difficulty parseDifficulty(String difficultyStr) {
        if (difficultyStr == null) return Question.Difficulty.MEDIUM;

        return switch (difficultyStr.toLowerCase()) {
            case "easy" -> Question.Difficulty.EASY;
            case "hard" -> Question.Difficulty.HARD;
            default -> Question.Difficulty.MEDIUM;
        };
    }

    private String generateQuestionId(String chunkId, int questionNumber) {
        return "q_" + chunkId + "_" + questionNumber + "_" + UUID.randomUUID().toString().substring(0, 6);
    }
}
