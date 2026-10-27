package nonprofit.view;

import java.util.List;
import java.util.Scanner;

/**
 * MVC - the View. Only input/output lives here, no business logic.
 *
 * TODO [PHASE 1 - Person C] USER CONTROL PANEL: build a Swing window (ControlPanelView)
 *      with the same operations; controllers should work with either view.
 */
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
