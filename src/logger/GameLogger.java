package logger;

import model.Board;
import model.CellStatus;
import model.Coordinate;
import model.ShotResult;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GameLogger {
    private final List<GameMove> moves;
    private final String player1Name;
    private final String player2Name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String winner;

    public GameLogger(String player1Name, String player2Name) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.moves = new ArrayList<>();
        this.startTime = LocalDateTime.now();
    }

    public void addMove(String playerName, Coordinate shot, ShotResult result, int shotsFired, int hits) {
        moves.add(new GameMove(playerName, shot, result, shotsFired, hits, LocalDateTime.now()));
    }

    public void setEndTime(String winner) {
        this.endTime = LocalDateTime.now();
        this.winner = winner;
    }

    public void saveToFile(Board player1Board, Board player2Board) {
        String fileName = "games/game_" + startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".txt";

        File directory = new File("games");
        if (!directory.exists()) {
            directory.mkdir();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("МОРСКОЙ БОЙ - ЛОГ ИГРЫ");
            writer.println();
            writer.println("Время начала: " + startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println("Время окончания: " + endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println("Победитель: " + winner);
            writer.println();

            writer.println("ИГРОКИ: ");

            writer.println("ХОДЫ ПО ВРЕМЕНИ");
            writer.println();
            for (GameMove move : moves) {
                writer.println(move.getTime() + " - " + move.getPlayerName() +
                        " стреляет в " + move.getShot() + " → " + move.getResult().getMessage() +
                        " (ходов: " + move.getShotsFired() + ", попаданий: " + move.getHits() + ")");
            }
            writer.println();

            writer.println("ПОЛЕ " + player1Name.toUpperCase());
            writer.println();
            printBoard(writer, player1Board);
            writer.println();

            writer.println("ПОЛЕ " + player2Name.toUpperCase());
            writer.println();
            printBoard(writer, player2Board);

            System.out.println("Игра сохранена в файл: " + fileName);

        } catch (IOException e) {
            System.err.println("Ошибка при сохранении игры: " + e.getMessage());
        }
    }

    private void printBoard(PrintWriter writer, Board board) {
        writer.print("     ");
        for (char c = 'A'; c <= 'P'; c++) {
            writer.print(" " + c + " ");
        }
        writer.println();

        for (int row = 0; row < 16; row++) {
            writer.printf("%2d  ", row + 1);
            for (int col = 0; col < 16; col++) {
                writer.print(" " + getSymbol(board.getCell(row, col)) + " ");
            }
            writer.println();
        }
    }

    private String getSymbol(CellStatus status) {
        switch (status) {
            case EMPTY: return "~";
            case SHIP: return "■";
            case MISS: return "•";
            case HIT: return "x";
            case SUNK: return "x";
            default: return "?";
        }
    }

    private static class GameMove {
        private final String playerName;
        private final Coordinate shot;
        private final ShotResult result;
        private final int shotsFired;
        private final int hits;
        private final LocalDateTime time;

        public GameMove(String playerName, Coordinate shot, ShotResult result, int shotsFired, int hits, LocalDateTime time) {
            this.playerName = playerName;
            this.shot = shot;
            this.result = result;
            this.shotsFired = shotsFired;
            this.hits = hits;
            this.time = time;
        }

        public String getPlayerName() { return playerName; }
        public Coordinate getShot() { return shot; }
        public ShotResult getResult() { return result; }
        public int getShotsFired() { return shotsFired; }
        public int getHits() { return hits; }
        public String getTime() { return time.format(DateTimeFormatter.ofPattern("HH:mm:ss")); }
    }
}