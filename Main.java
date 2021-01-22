package readability;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final int[] AGE = new int[]{6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 24, 25};
    public static void main(String[] args) {
        String text;
        try(Scanner scanner = new Scanner(Paths.get(args[0]))) {
            text = scanner.nextLine();
            start(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void start(String text) {

        double characters = countCharacters(text);
        double words = countWords(text);
        double sentences = countSentences(text);
        double syllables = countSyllables(text);
        double polysyllables = countPolysyllables(text);


        System.out.printf("The text is: %n%s%n%n" +
                        "Words: %.0f%n" +
                        "Sentences: %.0f%n" +
                        "Characters: %.0f%n" +
                        "Syllables: %.0f%n" +
                        "Polysyllables: %.0f%n",
                text, words, sentences, characters, syllables, polysyllables);

        chooseTheScore(characters, syllables, polysyllables, words, sentences);
    }

    private static void chooseTheScore(double characters, double syllables, double polysyllables, double words, double sentences) {
        String algorithm;
        double scoreARI = calculateAutomatedReadabilityIndex(characters, words, sentences);
        double ageARI = foundAge(scoreARI);

        double scoreFK = calculateFleschKincaidReadabilityTests(words,syllables,sentences);
        double ageFK = foundAge(scoreFK);

        double scoreSMOG = calculateSimpleMeasureOfGobbledygook(sentences, polysyllables);
        double ageSMOG = foundAge(scoreSMOG);

        double scoreCL = calculateCalemanLiauIndex(characters, words, sentences);
        double ageCL = foundAge(scoreCL);

        double averageAge = (ageARI + ageFK + ageSMOG + ageCL) / 4.0;

        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");

        try(Scanner scanner = new Scanner(System.in)) {
            algorithm = scanner.nextLine();
        }

        System.out.println();
        switch (algorithm) {
            case "ARI" :
                System.out.printf("Automated Readability Index: %.2f (about %.0f year olds).%n", scoreARI, ageARI);
                break;
            case "FK" :
                System.out.printf("Flesch–Kincaid readability tests: %.2f (about %.0f year olds).%n", scoreFK, ageFK);
                break;
            case "SMOG" :
                System.out.printf("Simple Measure of Gobbledygook: %.2f (about %.0f year olds).%n", scoreSMOG, ageSMOG);
                break;
            case "CL" :
                System.out.printf("Coleman–Liau index: %.2f (about %.0f year olds).%n%n", scoreCL, ageCL);
                break;
            case "all" :
                System.out.printf("Automated Readability Index: %.2f (about %.0f year olds).%n" +
                                "Flesch–Kincaid readability tests: %.2f (about %.0f year olds).%n" +
                                "Simple Measure of Gobbledygook: %.2f (about %.0f year olds).%n" +
                                "Coleman–Liau index: %.2f (about %.0f year olds).%n%n" +
                                "This text should be understood in average by %.2f year olds.",
                        scoreARI, ageARI, scoreFK, ageFK, scoreSMOG, ageSMOG, scoreCL, ageCL, averageAge);
                break;
            default:
                System.out.println("Unknown algorithm");
                break;
        }
    }

    private static double calculateAutomatedReadabilityIndex(double characters, double words, double sentences) {

        return 4.71 * (characters / words) + 0.5 * (words / sentences) - 21.43;
    }

    private static double calculateFleschKincaidReadabilityTests(double words, double syllables, double sentences) {

        return 0.39 * (words / sentences) + 11.8 * (syllables / words) - 15.59;
    }

    private static double calculateSimpleMeasureOfGobbledygook(double sentences, double polysyllables) {
        return 1.043 * Math.sqrt(polysyllables * (30.0 / sentences)) + 3.1291;
    }

    private static double calculateCalemanLiauIndex(double characters, double words, double sentences) {
        return 0.0588 * (characters * 100.0 / words) - 0.296 * (sentences * 100.0 / words) - 15.8;
    }

    private static double countSentences(String text) {

        return text.split("[.!?]").length;
    }

    private static double countWords(String text) {
        return text.split("\\s").length;
    }

    private static double countSyllables(String text) {
        double syllables = 0;

        text = text.replaceAll("!,.?", "");
        Pattern pattern = Pattern.compile("(?!e\\b)[aeoiuy]+|\\b[^aeoiuy\\s]+e\\b|\\b[^aeoiuy\\s]+\\b");
        Matcher matcher = pattern.matcher(text.toLowerCase());

        while(matcher.find()) {
            syllables++;
        }
        return syllables;
    }

    private static double countPolysyllables(String text) {
        double polysyllables = 0;
        for (String word : text.split("\\s")) {
            if (countSyllables(word) > 2) {
                polysyllables++;
            }
        }
        return polysyllables;
    }

    private static double countCharacters(String text) {
        return text.replaceAll("\\s", "").length();
    }

    private static double foundAge(double score) {
        return AGE[(int)Math.round(score) - 1];
    }
}
