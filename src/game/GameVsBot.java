package game;

import bot.SmartBot;
import logger.GameLogger;
import model.Board;
import model.CellStatus;
import model.Coordinate;
import model.ShotResult;
import ui.ConsoleRenderer;
import ui.InputParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameVsBot {
    private final Player human;
    private final SmartBot bot;
    private boolean humanTurn;
    private boolean gameOver;
    private long startTime;
    private long endTime;
    private GameLogger gameLogger;

    public GameVsBot(String humanName) {
        this.human = new Player(humanName);
        this.bot = new SmartBot();
        this.human.setEnemyBoard(bot.getBoard());
        this.humanTurn = true;
        this.gameOver = false;
        this.gameLogger = new GameLogger(humanName, "Bot");
    }

    public void start() {
        startTime = System.currentTimeMillis();

        System.out.println("МОРСКОЙ БОЙ VS БОТ");

        System.out.println("Игрок " + human.getName() + ", расстановка кораблей");
        placePlayerShips();

        System.out.println("Бот расставляет свои корабли...");
        placeBotShips();

        System.out.println("Кораблей у бота: " + bot.getBoard().getShips().size());
        System.out.println("Всего клеток кораблей: " + bot.getBoard().getShips().stream().mapToInt(s -> s.getCells().size()).sum());

        System.out.println("Бот расставил свои корабли!!!");

        gameLoop();
        endGame();
    }

    private void placePlayerShips() {
        int[] shipSizes = {
                6, 5, 5, 4, 4, 4, 3, 3, 3, 3,
                2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1
        };

        int shipCounter = 1;
        Scanner scanner = new Scanner(System.in);

        for (int size : shipSizes) {
            boolean placed = false;

            while (!placed) {
                ConsoleRenderer.clearScreen();

                System.out.println("РАСТАНОВКА КОРАБЛЕЙ");
                System.out.println("Корабль " + shipCounter + " из " + shipSizes.length + " размер: " + size);

                ConsoleRenderer.renderBoards(human.getMyBoard(), human.getEnemyBoard(), false);

                System.out.println(" auto - автоматическая расстановка");
                System.out.println(" EXIT - выход из игры");
                System.out.print("Введите координату начала (например A1): ");

                String coordInput = scanner.nextLine().trim().toUpperCase();

                if (coordInput.equals("EXIT")) {
                    System.out.println("Выход из игры");
                    System.exit(0);
                }

                if (coordInput.equals("AUTO")) {
                    autoRemainingShips(shipSizes, shipCounter - 1);
                    System.out.println("Корабли расставлены в случайном порядке");
                    System.out.print("Нажмите Enter чтобы продолжить...");
                    scanner.nextLine();
                    return;
                }

                try {
                    Coordinate start = new Coordinate(coordInput);
                    System.out.println("Выберите направление:");
                    System.out.println("  w - вверх");
                    System.out.println("  s - вниз");
                    System.out.println("  a - влево");
                    System.out.println("  d - вправо");
                    System.out.print("Ваш выбор: ");

                    String dirInput = scanner.nextLine().trim().toLowerCase();

                    int row = start.getRow();
                    int col = start.getCol();
                    boolean valid = true;
                    List<Coordinate> cells = new ArrayList<>();

                    switch (dirInput) {
                        case "w":
                            if (row - size + 1 < 0) {
                                System.out.println("Ошибка: Корабль выходит за верхнюю границу!");
                                valid = false;
                            } else {
                                for (int i = 0; i < size; i++) {
                                    cells.add(new Coordinate(row - i, col));
                                }
                            }
                            break;
                        case "s":
                            if (row + size > 16) {
                                System.out.println("Ошибка: Корабль выходит за нижнюю границу!");
                                valid = false;
                            } else {
                                for (int i = 0; i < size; i++) {
                                    cells.add(new Coordinate(row + i, col));
                                }
                            }
                            break;
                        case "a":
                            if (col - size + 1 < 0) {
                                System.out.println("Ошибка: Корабль выходит за левую границу!");
                                valid = false;
                            } else {
                                for (int i = 0; i < size; i++) {
                                    cells.add(new Coordinate(row, col - i));
                                }
                            }
                            break;
                        case "d":
                            if (col + size > 16) {
                                System.out.println("Ошибка: Корабль выходит за правую границу!");
                                valid = false;
                            } else {
                                for (int i = 0; i < size; i++) {
                                    cells.add(new Coordinate(row, col + i));
                                }
                            }
                            break;
                        default:
                            System.out.println("Ошибка! Выберите w, s, a или d");
                            valid = false;
                    }

                    if (!valid) {
                        System.out.print("Нажмите Enter чтобы продолжить...");
                        scanner.nextLine();
                        continue;
                    }

                    boolean canPlace = true;

                    for (Coordinate cell : cells) {
                        if (human.getMyBoard().getCell(cell.getRow(), cell.getCol()) != CellStatus.EMPTY) {
                            System.out.println("Ошибка: Клетка " + cell + " уже занята!");
                            canPlace = false;
                            break;
                        }
                    }

                    if (canPlace) {
                        for (Coordinate cell : cells) {
                            for (int dr = -1; dr <= 1; dr++) {
                                for (int dc = -1; dc <= 1; dc++) {
                                    int nr = cell.getRow() + dr;
                                    int nc = cell.getCol() + dc;
                                    if (nr >= 0 && nr < 16 && nc >= 0 && nc < 16) {
                                        if (human.getMyBoard().getCell(nr, nc) == CellStatus.SHIP) {
                                            boolean isShipCell = false;
                                            for (Coordinate c : cells) {
                                                if (c.getRow() == nr && c.getCol() == nc) {
                                                    isShipCell = true;
                                                    break;
                                                }
                                            }
                                            if (!isShipCell) {
                                                System.out.println("Ошибка: Корабль слишком близко к другому кораблю!");
                                                canPlace = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (!canPlace) break;
                            }
                            if (!canPlace) break;
                        }
                    }

                    if (canPlace) {
                        for (Coordinate cell : cells) {
                            human.getMyBoard().forcePlaceShip(cell);
                        }
                        placed = true;
                        shipCounter++;
                        System.out.println("Корабль размещен!");
                    } else {
                        System.out.println("Не удалось разместить корабль!");
                    }

                    System.out.print("Нажмите Enter чтобы продолжить...");
                    scanner.nextLine();
                }
                catch (Exception e) {
                    System.out.println("Ошибка! " + e.getMessage());
                    System.out.print("Нажмите Enter чтобы продолжить...");
                    scanner.nextLine();
                }
            }
        }

        ConsoleRenderer.clearScreen();
        System.out.println("Все корабли расставлены!");
        ConsoleRenderer.renderBoards(human.getMyBoard(), human.getEnemyBoard(), false);
        System.out.print("Нажмите Enter чтобы продолжить...");
        scanner.nextLine();
    }

    private void autoRemainingShips(int[] shipSizes, int startIndex) {
        for (int i = startIndex; i < shipSizes.length; i++) {
            int size = shipSizes[i];
            boolean placed = false;
            int attempts = 0;

            while (!placed && attempts < 20000) {
                int row = (int) (Math.random() * 16);
                int col = (int) (Math.random() * 16);
                boolean horizontal = Math.random() < 0.5;
                attempts++;

                try {
                    Coordinate start = new Coordinate(row, col);
                    if (human.getMyBoard().canPlaceShip(start, horizontal, size)) {
                        human.getMyBoard().placeShip(start, horizontal, size);
                        placed = true;
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private void placeBotShips() {
        int[] shipSizes = {
                6, 5, 5, 4, 4, 4, 3, 3, 3, 3,
                2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1
        };

        int shipCounter = 1;
        for (int size : shipSizes) {
            boolean placed = false;
            int attempts = 0;

            while (!placed && attempts < 20000) {
                int row = (int) (Math.random() * 16);
                int col = (int) (Math.random() * 16);
                boolean horizontal = Math.random() < 0.5;
                attempts++;

                try {
                    Coordinate start = new Coordinate(row, col);
                    if (bot.getBoard().canPlaceShip(start, horizontal, size)) {
                        bot.getBoard().placeShip(start, horizontal, size);
                        placed = true;
                    }
                } catch (Exception e) {
                }
            }
            shipCounter++;
        }
    }

    private void gameLoop() {
        Scanner scanner = new Scanner(System.in);
        while (!gameOver) {
            ConsoleRenderer.clearScreen();

            System.out.println("игрок: " + human.getName() + " ход: " + (humanTurn ? "ваш" : "бота"));
            System.out.println("(Для досрочного выхода введите EXIT во время своего хода)");

            ConsoleRenderer.renderBoards(human.getMyBoard(), human.getEnemyBoard(), false);

            System.out.println("Статистика:");
            System.out.println("Сделано ходов: " + human.getShotsFired());
            System.out.println("Попаданий: " + human.getHits());
            System.out.println("Точность: " + String.format("%.1f", human.getAccuracy()) + "%");
            System.out.println("Кораблей противника: " + countRemainingShips(human.getEnemyBoard()));

            if (humanTurn) {
                humanMove(scanner);
                if (gameOver) break;
            } else {
                botMove();
                if (gameOver) break;
            }

            if (human.getEnemyBoard().allShipsSunk()) {
                gameOver = true;
                endTime = System.currentTimeMillis();
                gameLogger.setEndTime(human.getName());
                gameLogger.saveToFile(human.getMyBoard(), human.getEnemyBoard());
                System.out.println("\nПОБЕДА! Вы уничтожили все корабли!");
                System.out.print("Нажмите Enter...");
                scanner.nextLine();
            } else if (human.getMyBoard().allShipsSunk()) {
                gameOver = true;
                endTime = System.currentTimeMillis();
                gameLogger.setEndTime("Bot");
                gameLogger.saveToFile(human.getMyBoard(), human.getEnemyBoard());
                System.out.println("\nПОРАЖЕНИЕ! Бот уничтожил все ваши корабли!");
                System.out.print("Нажмите Enter...");
                scanner.nextLine();
            }
        }
    }

    private void humanMove(Scanner scanner) {
        System.out.println("\nВаш ход!");
        System.out.print("Введите координаты клетки для выстрела (например A1) или EXIT: ");
        String input = scanner.nextLine().trim().toUpperCase();

        if (checkExitCommand(input, scanner)) {
            return;
        }

        Coordinate shot;
        try {
            shot = new Coordinate(input);
        } catch (Exception e) {
            System.out.println("Ошибка! " + e.getMessage());
            System.out.print("Нажмите Enter...");
            scanner.nextLine();
            return;
        }

        ShotResult result = human.getEnemyBoard().shoot(shot);

        if (result == ShotResult.ALREADY_SHOT) {
            System.out.println("Вы уже стреляли сюда!");
            System.out.print("Нажмите Enter...");
            scanner.nextLine();
            return;
        }

        human.incrementShots();
        if (result == ShotResult.HIT || result == ShotResult.SUNK) {
            human.incrementHits();
        }

        gameLogger.addMove(human.getName(), shot, result, human.getShotsFired(), human.getHits());

        System.out.println("\n" + shot + ": " + result.getMessage());

        if (result == ShotResult.SUNK) {
            System.out.println("Корабль уничтожен!");
        } else if (result == ShotResult.HIT) {
            System.out.println("Попадание! Ещё один выстрел!");
        } else if (result == ShotResult.MISS) {
            System.out.println("Мимо! Ход переходит боту.");
        }

        System.out.print("Нажмите Enter чтобы продолжить...");
        scanner.nextLine();

        if (result == ShotResult.MISS) {
            humanTurn = false;
        }
    }

    private void botMove() {
        System.out.println("\nХод бота...");

        if (gameOver) return;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Coordinate shot = bot.makeMove(human.getMyBoard());
        ShotResult result = human.getMyBoard().shoot(shot);

        bot.processResult(shot, result);

        gameLogger.addMove("Bot", shot, result, 0, 0);

        System.out.println("Бот стреляет в " + shot + ": " + result.getMessage());

        if (result == ShotResult.SUNK) {
            System.out.println("Бот уничтожил ваш корабль!");
        } else if (result == ShotResult.HIT) {
            System.out.println("Бот попал! Он продолжит атаку.");
        } else if (result == ShotResult.MISS) {
            System.out.println("Мимо! Ход переходит вам.");
        }

        System.out.print("Нажмите Enter чтобы продолжить...");
        new Scanner(System.in).nextLine();

        if (result == ShotResult.MISS) {
            humanTurn = true;
        }
    }

    private int countRemainingShips(Board board) {
        return (int) board.getShips().stream().filter(ship -> !ship.isSunk()).count();
    }

    private void endGame() {
        ConsoleRenderer.clearScreen();

        System.out.println("Конец игры");

        System.out.println("Итоги полей:\n");
        ConsoleRenderer.renderBoards(human.getMyBoard(), human.getEnemyBoard(), true);

        long duration = (endTime - startTime) / 1000;
        long minutes = duration / 60;
        long seconds = duration % 60;

        System.out.println("\nИтоговая статистика:");
        System.out.println("Время игры: " + minutes + " мин " + seconds + " сек");
        System.out.println("\n" + human.getName() + ":");
        System.out.println("Сделано ходов: " + human.getShotsFired());
        System.out.println(" Попаданий: " + human.getHits());
        System.out.println(" Точность: " + String.format("%.1f", human.getAccuracy()) + "%");
        System.out.println("Уничтожено кораблей бота: " + (21 - countRemainingShips(human.getEnemyBoard())));
        System.out.println("\nБот:");
        System.out.println("Уничтожено ваших кораблей: " + (21 - countRemainingShips(human.getMyBoard())));

        if (human.getEnemyBoard().allShipsSunk()) {
            System.out.println("Победитель: " + human.getName() + "!");
        } else {
            System.out.println("Победитель: Бот!");
        }
    }

    private boolean checkExitCommand(String input, Scanner scanner) {
        if (input.equalsIgnoreCase("EXIT")) {
            System.out.println("\nДосрочное завершение игры...");
            gameOver = true;
            endTime = System.currentTimeMillis();
            gameLogger.setEndTime("Игра прервана (" + human.getName() + " вышел)");
            gameLogger.saveToFile(human.getMyBoard(), human.getEnemyBoard());
            System.out.println("Лог игры сохранён!");
            System.out.print("Нажмите Enter для выхода...");
            scanner.nextLine();
            return true;
        }
        return false;
    }
}