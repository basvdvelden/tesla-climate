package nl.vandervelden.teslaclimate.api;

public class AuthBody {
    private String grant_type = "refresh_token";
    private String client_secret = "sec";
    private String client_id = "81527cff06843c8634fdc09e8ac0abefb46ac849f38fe1e431c2ef2106796384";
    private String refresh_token;

    public AuthBody(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public String getClient_id() {
        return client_id;
    }

    public String getRefresh_token() {
        return refresh_token;
    }
}
