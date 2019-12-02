package nl.vandervelden.teslaclimate.api;

public class CommandResponse {
    private CommandResult response;

    public CommandResponse() {}

    public CommandResult getResponse() {
        return response;
    }

    public void setResponse(CommandResult response) {
        this.response = response;
    }
}
