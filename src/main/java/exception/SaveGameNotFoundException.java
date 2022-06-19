package exception;

public class SaveGameNotFoundException extends RuntimeException {
    public SaveGameNotFoundException(String saveName) {
        super(String.format("Save game %s not found!", saveName));
    }
}
