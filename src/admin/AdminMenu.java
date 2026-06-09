package admin;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class AdminMenu {
    private static final String GAMES_DIR = "games";
    private static final Scanner scanner = new Scanner(System.in);

    public static void showMenu() {
        while (true) {
            System.out.println("\n\n\nРЕЖИМ АДМИНИСТРАТОРА");
            System.out.println("1. Список всех игр");
            System.out.println("2. Просмотреть игру (replay)");
            System.out.println("3. Удалить игру");
            System.out.println("4. Заархивировать игру");
            System.out.println("5. Выход");
            System.out.print("Ваш выбор: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    listGames();
                    break;
                case "2":
                    replayGame();
                    break;
                case "3":
                    deleteGame();
                    break;
                case "4":
                    archiveGame();
                    break;
                case "5":
                    System.out.println("Выход из режима администратора");
                    return;
                default:
                    System.out.println("Неверный выбор");
            }
        }
    }

    private static void listGames() {
        System.out.println("\n\n\nСПИСОК СОХРАНЁННЫХ ИГР\n");

        File dir = new File(GAMES_DIR);
        if (!dir.exists() || dir.listFiles() == null || dir.listFiles().length == 0) {
            System.out.println("Нет сохранённых игр");
            System.out.print("Нажмите Enter...");
            scanner.nextLine();
            return;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.out.println("Нет сохранённых игр");
            System.out.print("Нажмите Enter...");
            scanner.nextLine();
            return;
        }

        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

        for (int i = 0; i < files.length; i++) {
            String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(files[i].lastModified()));
            System.out.println((i + 1) + ". " + files[i].getName() + " - " + date);
        }

        System.out.print("\nНажмите Enter...");
        scanner.nextLine();
    }

    private static void replayGame() {
        System.out.println("\n\n\nПРОСМОТР ИГРЫ\n");

        File dir = new File(GAMES_DIR);
        if (!dir.exists()) {
            System.out.println("Нет сохранённых игр");
            System.out.print("Нажмите Enter...");
            scanner.nextLine();
            return;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.out.println("Нет сохранённых игр");
            System.out.print("Нажмите Enter...");
            scanner.nextLine();
            return;
        }

        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }

        System.out.print("\nВыберите номер игры: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > files.length) {
                System.out.println("Неверный выбор");
                System.out.print("Нажмите Enter...");
                scanner.nextLine();
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Неверный ввод");
            System.out.print("Нажмите Enter...");
            scanner.nextLine();
            return;
        }

        GameReplayer.replay(files[choice - 1].getPath());
    }

    private static void deleteGame() {
        System.out.println("\n\n\nУДАЛЕНИЕ ИГРЫ\n");

        File dir = new File(GAMES_DIR);
        if (!dir.exists()) {
            System.out.println("Нет сохранённых игр");
            System.out.print("Нажмите Enter...");
            scanner.nextLine();
            return;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.out.println("Нет сохранённых игр");
            System.out.print("Нажмите Enter...");
            scanner.nextLine();
            return;
        }

        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }

        System.out.print("\nВыберите номер игры для удаления: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > files.length) {
                System.out.println("Неверный выбор");
                System.out.print("Нажмите Enter...");
                scanner.nextLine();
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Неверный ввод");
            System.out.print("Нажмите Enter...");
            scanner.nextLine();
            return;
        }

        System.out.print("Вы уверены? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("y")) {
            if (files[choice - 1].delete()) {
                System.out.println("Игра удалена");
            } else {
                System.out.println("Ошибка при удалении");
            }
        } else {
            System.out.println("Удаление отменено");
        }

        System.out.print("Нажмите Enter...");
        scanner.nextLine();
    }

    private static void archiveGame() {
        System.out.println("\n\n\nАРХИВАЦИЯ ИГРЫ\n");

        File dir = new File(GAMES_DIR);
        if (!dir.exists()) {
            System.out.println("Нет сохранённых игр");
            System.out.print("Нажмите Enter...");
            scanner.nextLine();
            return;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.out.println("Нет сохранённых игр");
            System.out.print("Нажмите Enter...");
            scanner.nextLine();
            return;
        }

        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }

        System.out.print("\nВыберите номер игры для архивации: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > files.length) {
                System.out.println("Неверный выбор");
                System.out.print("Нажмите Enter...");
                scanner.nextLine();
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Неверный ввод");
            System.out.print("Нажмите Enter...");
            scanner.nextLine();
            return;
        }

        File archiveDir = new File(GAMES_DIR + "/archive");
        if (!archiveDir.exists()) {
            archiveDir.mkdirs();
        }

        String zipName = files[choice - 1].getName().replace(".txt", ".zip");
        String zipPath = GAMES_DIR + "/archive/" + zipName;

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {
            zos.putNextEntry(new ZipEntry(files[choice - 1].getName()));
            Files.copy(files[choice - 1].toPath(), zos);
            zos.closeEntry();
            System.out.println("Игра заархивирована: " + zipPath);
        } catch (IOException e) {
            System.out.println("Ошибка при архивации: " + e.getMessage());
        }

        System.out.print("Нажмите Enter...");
        scanner.nextLine();
    }
}