import admin.AdminMenu;
import game.GameVsBot;
import ui.InputParser;

public class Main {
    public static void main(String[] args) {
        System.out.println("\nМорской бой");
        System.out.println("версия: уже и не счесть какая\n");

        String playerName = InputParser.askString("Введите ваше имя: ");

        System.out.println("\nВыберите режим игры:");
        System.out.println("  1 - Одиночная игра (против бота)");
        System.out.println("  2 - Игра с напарником (будет добавлено позже)");
        System.out.println("  3 - Режим администратора");

        int choice = InputParser.askInt("\nВаш выбор", 1, 3);

        switch (choice) {
            case 1:
                GameVsBot game = new GameVsBot(playerName);
                game.start();
                break;
            case 2:
                System.out.println("Режим игры с напарником в разработке");
                break;
            case 3:
                if (playerName.equalsIgnoreCase("admin")) {
                    AdminMenu.showMenu();
                } else {
                    System.out.println("Доступ запрещён! Режим администратора только для admin");
                    System.out.print("Нажмите Enter...");
                    new java.util.Scanner(System.in).nextLine();
                }
                break;
        }
    }
}