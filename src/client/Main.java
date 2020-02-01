package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    Controller c;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();

        Parent root = loader.load(getClass().getResourceAsStream("sample.fxml"));
        primaryStage.setTitle("//CODE");
        primaryStage.getIcons().add(new Image("img/icon1.png"));
        c = loader.getController();
        c.loginField.setTooltip(new Tooltip("Укажите свой логин"));
        c.passwordField.setTooltip(new Tooltip("Укажите свой пароль"));

        Scene scene = new Scene(root, 650, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            c.Dispose();
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
