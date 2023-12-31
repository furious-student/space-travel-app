package app.model.users;

public abstract class Staff extends User {
    private static int accessToken;
    public Staff(String username, int birthYear, int password, UserType userView) {
        super(username, birthYear, password, userView);
    }

    public static int getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(int accessToken) {
        Staff.accessToken = accessToken;
    }
}
