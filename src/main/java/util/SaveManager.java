package util;

import exception.SaveGameNotFoundException;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.apache.commons.io.FileUtils.deleteDirectory;

@NoArgsConstructor(access = PRIVATE)
public class SaveManager {
    public static final String SAVE_DIR = getSaveDir() + "\\NoitaSaver";
    private static final File NOITA_MAIN_SAVE_FILE = new File(getSaveDir() + "\\save00");
    private static final String SAVE_FORMAT = "%s\\%s";

    public static List<File> getSavedGames() {
        File folder = new File(SAVE_DIR);
        File[] files = folder.listFiles();
        if (files == null) {
            return emptyList();
        }

        return Arrays.stream(files)
                .filter(File::isDirectory)
                .collect(toList());
    }

    @SneakyThrows
    public static void saveGame(String saveName) {
        File saveFile = new File(String.format(SAVE_FORMAT, SAVE_DIR, saveName));
        deleteDirectory(saveFile);
        copyDirectory(NOITA_MAIN_SAVE_FILE, saveFile);
    }

    @SneakyThrows({IOException.class})
    public static void loadGame(String saveName) {
        File saveFile = new File(String.format(SAVE_FORMAT, SAVE_DIR, saveName));
        if (!saveFile.exists()) {
            throw new SaveGameNotFoundException(saveName);
        }
        deleteDirectory(NOITA_MAIN_SAVE_FILE);
        copyDirectory(saveFile, NOITA_MAIN_SAVE_FILE);
    }

    @SneakyThrows
    public static void deleteSavedGame(String saveName) {
        File saveFile = new File(String.format(SAVE_FORMAT, SAVE_DIR, saveName));
        deleteDirectory(saveFile);
    }

    private static String getSaveDir() {
        return new File(System.getenv("APPDATA")).getParent() + "\\LocalLow\\Nolla_Games_Noita";
    }
}
