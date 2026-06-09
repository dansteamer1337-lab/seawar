package ui;

import model.Coordinate;

import java.util.Scanner;

public class InputParser {
    private static final Scanner scanner = new Scanner(System.in);

    public static Coordinate askCoordinate(String prompt) {
        while (true) {
            System.out.print(prompt + " (например A1): ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("EXIT")) {
                System.out.println("Выход из игры.\nПока!");
                System.exit(0);
            }

            try {
                Coordinate coord = new Coordinate(input);
                System.out.println("Клетка: " + coord);
                return coord;
            }
            catch (IllegalArgumentException e) {
                System.out.println("Ошибка " + e.getMessage());
                System.out.println("Вводите координаты в этом диапозоне А1-Р16");
            }
        }
    }

    public static boolean askDirection(String prompt) {
        while (true) {
            System.out.println(prompt);
            System.out.print("(h) - горизонтально / (v) - вертикально: ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("h")) {
                return true;
            }
            if (input.equals("v")) {
                return false;
            }

            System.out.println("Ошибка! введите либо (h), либо (v)");
        }
    }

    public static int askInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + " (от " + min + " до " + max + "): ");
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Ошибка! Число должно быть от " + min + " до " + max);
            }
            catch (NumberFormatException e) {
                System.out.println("Ошибка! не верное число.\nВведите от " + min + " до " + max);
            }
        }
    }

    public static boolean askYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) {
                return true;
            }
            if (input.equals("n")) {
                return false;
            }
            System.out.println("Ошибка! введите (y) или (n)");
        }
    }

    public static String askString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static void waitForEnter() {
        System.out.print("Нажмите Enter чтобы продолжить...");
        scanner.nextLine();
    }
}