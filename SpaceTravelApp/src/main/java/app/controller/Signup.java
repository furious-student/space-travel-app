package app.controller;

import app.model.SystemAdministration;
import app.model.Utility;
import app.model.access.AccessContext;
import app.model.access.TravelerAccess;
import app.model.users.Guide;
import app.model.users.Traveler;
import app.model.users.User;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * The Controller for the Signup screen
 */
public class Signup implements Initializable {
    @FXML
    private ChoiceBox<String> birthYears;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;
    @FXML
    private PasswordField accessToken;
    @FXML
    private Label informative;

    private String realPassword = "";
    private String realAccessToken = "";
    private static final SystemAdministration systemAdministration;

    static {
        systemAdministration = SystemAdministration.initialize();
    }

    @FXML
    protected void onPreviousButtonClick(Event event) throws IOException {
        AccessContext accessContext = systemAdministration.getAccessContext();
        if (accessContext.getAccessStrategy().getClass() == TravelerAccess.class) {
            ControllerUtility.switchSceneOnEvent(event, "traveller-login.fxml");
        } else {
            ControllerUtility.switchSceneOnEvent(event, "staff-login.fxml");
        }
    }

    @FXML
    protected void onNextButtonClick(Event event) throws IOException {
        String username = this.username.getText();


        int intPassword;
        int intAT;
        int birthYear;

        try {
            intPassword = Integer.parseInt(realPassword);
        } catch (NumberFormatException numberFormatException) {
            intPassword = 0;
        }
        try {
            intAT = Integer.parseInt(realAccessToken);
        } catch (NumberFormatException numberFormatException) {
            intAT = 0;
        }
        try {
            birthYear = Integer.parseInt(birthYears.getValue());
        } catch (NumberFormatException numberFormatException) {
            birthYear = 0;
        }



        String informativeText = "";
        if (username.length() <= 2) {
            informativeText += "Username Error: Must have at least 3 characters\n";
        }
        if (realPassword.length() <= 7) {
            informativeText += "Password Error: Must have at least 8 characters (numbers)\n";
        }
        if (birthYear == 0) {
            informativeText += "Birth Year Error: Must be selected value\n";
        }

        if (informativeText.length() > 0) {
            setErrorMessage(informativeText);
            return;
        }

        int responseCode = systemAdministration.signup(username, intPassword,birthYear, intAT);

        switch (responseCode) {
            case 0 -> {
                // switch scene based on user type
                User currentUser = systemAdministration.getCurrentUser();
                if (currentUser.getClass() == Traveler.class) {
                    ControllerUtility.switchSceneOnEvent(event, "traveller/traveller-main.fxml");
                } else if (currentUser.getClass() == Guide.class) {
                    ControllerUtility.switchSceneOnEvent(event, "guide/guide-main.fxml");
                } else {
                    ControllerUtility.switchSceneOnEvent(event, "owner/owner-main.fxml");
                }
                clear();
            }
            case 1 -> {
                setErrorMessage("User with name \"" + username + "\" already exists.");
            }
            case 2 -> {
                setErrorMessage("Should not get this code");
            }
            case 3 -> {
                setErrorMessage("Incorrect access token for user with name \"" + username + "\".");
            }
        }
    }

    @FXML
    protected void onPasswordFieldKeyTyped() {
        String text = password.getText();

        if (text.length() > realPassword.length()) {
            if (text.charAt(0) == '0') {
                password.setText(realPassword);
                informative.setStyle("-fx-text-fill: #ff9900");
                informative.setText("Password cannot begin with \"0\"");
                return;
            }
            realPassword += Character.toString(text.charAt(text.length()-1));
        } else if (text.length() < realPassword.length()) {
            realPassword = Utility.removeLastChar(realPassword);
        }

        if (Utility.containsNonNumericChars(realPassword)) {
            realPassword = Utility.removeNonNumericChars(realPassword);
            password.setText(realPassword);
            informative.setStyle("-fx-text-fill: #ff9900");
            informative.setText("Password can contain only numbers!");
        } else {
            password.setText(realPassword);
            informative.setText("");
        }

        password.positionCaret(realPassword.length());
    }
    @FXML
    protected void onAccessTokenFieldKeyTyped() {
        String text = accessToken.getText();

        if (text.length() > realAccessToken.length()) {
            realAccessToken += Character.toString(text.charAt(text.length()-1));
        } else if (text.length() < realAccessToken.length()) {
            realAccessToken = Utility.removeLastChar(realAccessToken);
        }

        if (Utility.containsNonNumericChars(realAccessToken)) {
            realAccessToken = Utility.removeNonNumericChars(realAccessToken);
            accessToken.setText(realAccessToken);
            informative.setStyle("-fx-text-fill: #ff9900");
            informative.setText("Access Token can contain only numbers!");
        } else {
            accessToken.setText(realAccessToken);
            informative.setText("");
        }

        accessToken.positionCaret(realAccessToken.length());
    }

    private void setErrorMessage(String message) {
        informative.setStyle("-fx-text-fill: red");
        informative.setText(message);
    }

    private void clear() {
        this.realPassword = "";
        this.realAccessToken = "";
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<String> years = new ArrayList<>();
        for (int i = 1900; i < 2015; i++) {
            years.add(i + "");
        }

        birthYears.getItems().addAll(years);
        birthYears.setValue(years.get(years.size()-18));
    }
}
