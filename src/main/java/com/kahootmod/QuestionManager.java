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
    public static final File PACKS_DIR = new File(FMLPaths.CONFIGDIR.get().toFile(), "kahoot_packs");
    private static final Gson GSON = new Gson();
    private static List<Question> questions = new ArrayList<>();
    private static final java.util.LinkedList<Question> history = new java.util.LinkedList<>();
    private static final Random RANDOM = new Random();

    public static void loadQuestions() {
        if (!PACKS_DIR.exists()) {
            PACKS_DIR.mkdirs();
        }
        
        List<Question> newQuestions = new ArrayList<>();
        File[] files = PACKS_DIR.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null && files.length > 0) {
            // Load exclusively only the first pack
            try (FileReader reader = new FileReader(files[0])) {
                Type listType = new TypeToken<List<Question>>() {}.getType();
                List<Question> imported = GSON.fromJson(reader, listType);
                if (imported != null) {
                    newQuestions.addAll(imported);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        questions = newQuestions;
        history.clear();
    }

    public static int getQuestionCount() {
        return questions.size();
    }

    public static Question getRandomQuestion() {
        if (questions.isEmpty()) return null;
        
        int maxHistory = Math.min(10, questions.size() / 2);
        Question q = null;
        
        for (int i = 0; i < 50; i++) {
            q = questions.get(RANDOM.nextInt(questions.size()));
            if (!history.contains(q)) {
                break;
            }
        }
        
        if (q != null) {
            history.addLast(q);
            while (history.size() > maxHistory) {
                history.removeFirst();
            }
        }
        return q;
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
