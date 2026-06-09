package ui;

import model.Board;
import model.CellStatus;

public class ConsoleRenderer {

    public static void renderBoards(Board myBoard, Board enemyBoard, boolean revealEnemy){
        System.out.println("                         ВАШЕ ПОЛЕ                          ПОЛЕ ПРОТИВНИКА");

        System.out.print("    ");
        for (char c = 'A'; c <= 'P'; c++) {
            System.out.print(" " + c + " ");
        }
        System.out.print("        ");
        for (char c = 'A'; c <= 'P'; c++) {
            System.out.print(" " + c + " ");
        }

        System.out.println();

        for (int row = 0; row < 16; row++) {
            System.out.printf("%2d  ", row + 1);

            for (int col = 0; col < 16; col++) {
                System.out.print(" " + getSymbol(myBoard.getCell(row, col), true) + " ");
            }

            System.out.print("     ");

            System.out.printf("%2d  ", row + 1);

            for (int col = 0; col < 16; col++) {
                CellStatus status = enemyBoard.getCell(row, col);

                if (!revealEnemy && status == CellStatus.SHIP) {
                    System.out.print(" ~ ");
                } else {
                    System.out.print(" " + getSymbol(status, false) + " ");
                }
            }
            System.out.println();
        }
    }

    private static String getSymbol(CellStatus status, boolean isMyBoard) {
        switch (status) {
            case EMPTY:
                return "~";
            case SHIP:
                return isMyBoard ? "■" : "~";
            case MISS:
                return "•";
            case HIT:
                return "x";
            case SUNK:
                return "x";
            default:
                return "?";
        }
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}

