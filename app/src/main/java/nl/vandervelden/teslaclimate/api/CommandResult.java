package nl.vandervelden.teslaclimate.api;

public class CommandResult {
    private String reason;
    private boolean result;

    public CommandResult() {}

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
