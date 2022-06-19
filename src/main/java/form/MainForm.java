package form;

import exception.SaveGameNotFoundException;
import org.codehaus.plexus.util.FileUtils;
import util.PopupManager;
import util.SaveManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import static org.apache.maven.shared.utils.StringUtils.isBlank;
import static util.PopupManager.Severity.ERROR;
import static util.SaveManager.SAVE_DIR;

public class MainForm {
    private JPanel mainPanel;
    private JButton exitButton;
    private JList<String> savesList;
    private JButton saveButton;
    private JButton loadButton;
    private JTextField saveNameTextField;
    private JButton deleteButton;

    private MainForm() {
        exitButton.addActionListener(this::onClickExitButton);
        saveButton.addActionListener(this::onClickSaveButton);
        loadButton.addActionListener(this::onClickLoadButton);
        deleteButton.addActionListener(this::onClickDeleteButton);
        savesList.addListSelectionListener(this::onClickSavesListItem);
        loadSaves();
    }

    public static void main(String[] args) {
        FileUtils.mkdir(SAVE_DIR);
        JFrame frame = new JFrame("MainForm");
        MainForm mainForm = new MainForm();
        frame.setContentPane(mainForm.mainPanel);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(300, 500));
        frame.pack();
        frame.setTitle("Noita saver");
        setFrameLocation(frame);
        frame.setVisible(true);
    }

    private void loadSaves() {
        var savesNames = SaveManager.getSavedGames().stream()
                .map(this::getSaveName)
                .collect(Collectors.toList());
        var model = new DefaultListModel<String>();
        savesNames.forEach(model::addElement);
        savesList.setModel(model);
    }

    private String getSaveName(File saveFile) {
        return String.format("%s / %s", saveFile.getName(), LocalDateTime.ofInstant(Instant.ofEpochMilli(saveFile.lastModified()), ZoneId.systemDefault()).toString());
    }

    private void onClickSaveButton(ActionEvent event) {
        boolean confirmed = PopupManager.confirm("Save game \"" + saveNameTextField.getText() + "\"?", "Confirm save");
        if (confirmed) {
            runWithBusyCursor(() -> {
                SaveManager.saveGame(saveNameTextField.getText());
                loadSaves();
            });
        }
    }

    private void onClickLoadButton(ActionEvent event) {
        if (!isBlank(saveNameTextField.getText())) {
            boolean confirmed = PopupManager.confirm("Load game \"" + saveNameTextField.getText() + "\"?", "Confirm load");
            if (confirmed) {
                runWithBusyCursor(() -> {
                    try {
                        SaveManager.loadGame(saveNameTextField.getText());
                    } catch (SaveGameNotFoundException e) {
                        PopupManager.show(e.getMessage(), "Load failed", ERROR);
                    }
                });
            }
        }
    }

    private void onClickDeleteButton(ActionEvent event) {
        if (!isBlank(saveNameTextField.getText())) {
            boolean confirmed = PopupManager.confirm("Delete game \"" + saveNameTextField.getText() + "\"?", "Confirm delete");
            if (confirmed) {
                runWithBusyCursor(() -> {
                    SaveManager.deleteSavedGame(saveNameTextField.getText());
                    loadSaves();
                });
            }
        }
    }

    private void onClickSavesListItem(ListSelectionEvent event) {
        if (!event.getValueIsAdjusting() && savesList.getSelectedValue() != null) {
            saveNameTextField.setText(savesList.getSelectedValue().split("/")[0].trim());
        }
    }

    private void onClickExitButton(ActionEvent event) {
        System.exit(0);
    }

    private static void setFrameLocation(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        frame.setLocation((width / 2) - 300, (height / 2) - 200);
    }

    private void runWithBusyCursor(Runnable function) {
        mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        function.run();
        mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
