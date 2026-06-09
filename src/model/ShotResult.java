package model;

public enum ShotResult {
    MISS("Мимо"),
    HIT("Ранил"),
    SUNK("Убил"),
    ALREADY_SHOT("В одну и туже точку не стреляют");
    private final String message;

    ShotResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
