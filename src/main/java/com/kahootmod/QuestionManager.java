package com.kahootmod;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QuestionManager {
    private static final File CONFIG_FILE = new File(FMLPaths.CONFIGDIR.get().toFile(), "kahoot_questions.json");
    private static final Gson GSON = new Gson();
    private static List<Question> questions = new ArrayList<>();
    private static final Random RANDOM = new Random();

    public static void loadQuestions() {
        if (!CONFIG_FILE.exists()) {
            createDefaultQuestions();
        }
        
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Type listType = new TypeToken<List<Question>>() {}.getType();
            questions = GSON.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createDefaultQuestions() {
        questions = Arrays.asList(
            new Question("What is 2 + 2?", Arrays.asList("3", "4", "5", "6"), 1),
            new Question("What is the capital of France?", Arrays.asList("Berlin", "Madrid", "Paris", "Rome"), 2),
            new Question("How do you craft a torch?", Arrays.asList("Stick + Coal", "Wood + Coal", "Stick + Flint", "Wood + Flint"), 0)
        );
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(questions, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Question getRandomQuestion() {
        if (questions.isEmpty()) return null;
        return questions.get(RANDOM.nextInt(questions.size()));
    }

    public static class Question {
        public String text;
        public List<String> answers;
        public int correctIndex;

        public Question(String text, List<String> answers, int correctIndex) {
            this.text = text;
            this.answers = answers;
            this.correctIndex = correctIndex;
        }
    }
}
