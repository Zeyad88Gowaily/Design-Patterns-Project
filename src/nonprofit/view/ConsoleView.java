package nonprofit.view;

import java.util.List;
import java.util.Scanner;

/** Console input and output. Contains no business logic. */
public class ConsoleView {
    private Scanner in = new Scanner(System.in);

    public void show(String text) { System.out.println(text); }

    public <T> void showList(String title, List<T> items) {
        System.out.println("--- " + title + " ---");
        if (items.isEmpty()) {
            System.out.println("(empty)");
        }
        for (T item : items) {
            System.out.println(item);
        }
    }

    public String ask(String question) {
        System.out.print(question + ": ");
        return in.nextLine().trim();
    }

    public int askInt(String question) {
        while (true) {
            try {
                return Integer.parseInt(ask(question));
            } catch (NumberFormatException e) {
                show("Please enter a whole number.");
            }
        }
    }

    public double askDouble(String question) {
        while (true) {
            try {
                return Double.parseDouble(ask(question));
            } catch (NumberFormatException e) {
                show("Please enter a number.");
            }
        }
    }
}
