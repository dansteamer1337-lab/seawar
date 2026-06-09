package admin;

import java.io.*;
import java.util.*;

public class GameReplayer {

    public static void replay(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            int moveStartIndex = -1;
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).contains("=== ХОДЫ ПО ВРЕМЕНИ ===")) {
                    moveStartIndex = i + 2;
                    break;
                }
            }

            if (moveStartIndex == -1) {
                System.out.println("Не удалось найти ходы в файле");
                return;
            }

            List<String> moves = new ArrayList<>();
            for (int i = moveStartIndex; i < lines.size(); i++) {
                String currentLine = lines.get(i).trim();
                if (currentLine.isEmpty()) continue;
                if (currentLine.contains("ПОЛЕ")) break;
                moves.add(currentLine);
            }

            Scanner scanner = new Scanner(System.in);
            int moveNum = 1;

            System.out.println("\nREPLAY ИГРЫ");
            System.out.println("Нажимайте Enter для следующего хода");
            System.out.println("Введите exit для выхода\n");

            for (String move : moves) {
                System.out.println("\nХОД " + moveNum);
                System.out.println(move);
                System.out.println();

                String input = scanner.nextLine().trim().toLowerCase();
                if (input.equals("exit")) {
                    System.out.println("Выход из replay");
                    return;
                }
                moveNum++;
            }

            System.out.println("\nКОНЕЦ REPLAY");
            System.out.print("Нажмите Enter...");
            scanner.nextLine();

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }
}