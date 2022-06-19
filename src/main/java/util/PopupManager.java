package util;

import lombok.NoArgsConstructor;

import javax.swing.*;

import static javax.swing.JOptionPane.*;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class PopupManager {

    public static void show(String message, String title, Severity severity) {
        JOptionPane.showMessageDialog(null, message, severity.name() + ": " + title, severity.level);
    }

    public static boolean confirm(String message, String title) {
        int response = JOptionPane.showConfirmDialog(null, message, "Confirm: " + title, YES_NO_OPTION);
        return response == 0;
    }

    public enum Severity {
        INFO(INFORMATION_MESSAGE), WARN(WARNING_MESSAGE), ERROR(ERROR_MESSAGE);

        private int level;

        Severity(int level) {
            this.level = level;
        }
    }
}
