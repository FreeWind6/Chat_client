package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Controller {
/*    @FXML
    TextArea textArea;*/

    @FXML
    TextField textField;

    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADDRESS = "localhost";
    final int PORT = 8189;

    @FXML
    HBox upperPanel;

    @FXML
    HBox bottomPanel;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passwordField;

    private boolean isAuthorized;

    @FXML
    ListView<String> clientList;

    @FXML
    ListView messagesView;

    @FXML
    MenuItem logOut;

    String nick = "";
    Color color;
    Date date;
    Label message;
    Label time;
    VBox messageBox;
    ArrayList arrayListMessage = new ArrayList();

    public void setMsg(String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                message = new Label("  " + str + "  ");
                time = new Label("\n" + simpleDateFormat.format(date) + "  ", message);
                time.setFont(new Font(10));
                arrayListMessage.add(message.getText() + time.getText().replaceAll("\n", " "));
/*                BackgroundImage backgroundImage = new BackgroundImage(new Image("file:src/img/code.png"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
                Background background = new Background(backgroundImage);
                messagesView.setBackground(background);*/
                // Стартовый цвет до авторизации
                color = Color.GRAY;
                messageBox = new VBox(time);
                if (nick != "") {
                    String[] mass = str.split(":");
                    if (nick.equalsIgnoreCase(mass[0])) {
                        color = Color.rgb(0, 136, 204);
                        messageBox.setAlignment(Pos.CENTER_RIGHT);
//                        message.setText(mass[1]); и 2 параметром в split добавь лимит:2
                    } else {
                        color = Color.GRAY;
                    }
                }
                setColorMsg(message);
                setColorMsg(time);
                messagesView.getItems().add(messageBox);
            }

            private void setColorMsg(Label message) {
                BackgroundFill backgroundFill;
                Background background;
                backgroundFill = new BackgroundFill(color,
                        new CornerRadii(5), Insets.EMPTY);
                background = new Background(backgroundFill);
                message.setBackground(background);
//                message.setOpacity(0.5);
                message.setTextFill(Color.WHITE);
            }
        });
    }

    private void alertMsg(String msg, String title, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("img/icon1.png"));
        alert.showAndWait();
    }

    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
        if (!isAuthorized) {
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
            clientList.setVisible(false);
            clientList.setManaged(false);
            logOut.setVisible(false);
        } else {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
            clientList.setVisible(true);
            clientList.setManaged(true);
            logOut.setVisible(true);
        }
    }

    public void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/authok")) {
                                String[] mass = str.split(" ");
                                nick = mass[1];
                                setAuthorized(true);
                                break;
                            } else {
                                setMsg(str);
                            }
                        }

                        while (true) {
                            String str = in.readUTF();
                            if (str.equals("/serverclosed")) break;
                            if (str.startsWith("/clientlist")) {
                                String[] tokens = str.split(" ");
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        clientList.getItems().clear();
                                        for (int i = 1; i < tokens.length; i++) {
                                            clientList.getItems().add(tokens[i]);
                                        }
                                    }
                                });
                            } else {
                                setMsg(str);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setAuthorized(false);
                    }
                }
            }).start();
        } catch (IOException e) {
            setMsg("Server is not available");
            loginField.clear();
            passwordField.clear();
            e.printStackTrace();
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void commands() throws IOException {
        Stage infoStage = new Stage();

        // создаем список объектов
        ObservableList<InfoTable> info = FXCollections.observableArrayList(

                new InfoTable("/end", "Выход из программы", "/end"),
                new InfoTable("/w nickname Hi", "Отправить личное сообщение пользователю nickname", "/w Vasya Привет!"),
                new InfoTable("/blacklist nickname", "Заблокировать пользователя nickname", "/blacklist Vasya")
        );
        // определяем таблицу и устанавливаем данные
        TableView<InfoTable> table = new TableView<>(info);
        table.setPrefWidth(550);
        table.setPrefHeight(250);

        // столбец для вывода Command
        TableColumn<InfoTable, String> commandColumn = new TableColumn<>("Command");
        // определяем фабрику для столбца с привязкой к свойству command
        commandColumn.setCellValueFactory(new PropertyValueFactory<>("command"));
        // добавляем столбец
        table.getColumns().add(commandColumn);

        TableColumn<InfoTable, Integer> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        table.getColumns().add(descriptionColumn);

        TableColumn<InfoTable, Integer> exampleColumn = new TableColumn<>("Example");
        exampleColumn.setCellValueFactory(new PropertyValueFactory<>("example"));
        table.getColumns().add(exampleColumn);

        FlowPane root = new FlowPane(10, 10, table);

        Scene scene = new Scene(root, 550, 100);

        infoStage.setScene(scene);
        infoStage.setTitle("Commands");
        infoStage.getIcons().add(new Image("img/icon1.png"));
        infoStage.show();
    }

    public void about() throws IOException {
        Stage aboutStage = new Stage();

        Parent rootInfo = FXMLLoader.load(getClass().getResource("about.fxml"));
        aboutStage.setTitle("About");
        aboutStage.getIcons().add(new Image("img/icon1.png"));
        aboutStage.setScene(new Scene(rootInfo, 250, 100));
        aboutStage.show();
    }

    public void clearWindow() {
        messagesView.getItems().clear();
        arrayListMessage.clear();
    }

    public void btnClick() {
        try {
            Desktop.getDesktop().browse(new URL("https://github.com/FreeWind6/Chat_client").toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Dispose() {
        System.out.println("Отправляем сообщение на сервер о завершении работы");
        try {
            if (out != null) {
                out.writeUTF("/end");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMsg(ActionEvent actionEvent) throws IOException {
        FileWriter fileWriter = new FileWriter("text.txt");
        for (int i = 0; i < arrayListMessage.size(); i++) {
            String listMsg = arrayListMessage.get(i).toString();
            if (this.nick != "" & listMsg.startsWith("  " + this.nick)) {
                listMsg = "*" + listMsg;
            }

            fileWriter.append(listMsg + "\n");
//            System.out.println(listMsg);
        }
        fileWriter.append("\n-------------------------\n" +
                "Ваши сообщения помечены звездочкой \n" +
                "Ваш ник: " + this.nick);
        fileWriter.close();
        alertMsg("История сохранена в файл text.txt и находится в той же директории что и исходный файл.", "Файл записан!", Alert.AlertType.INFORMATION);
    }

    public void logOut(ActionEvent actionEvent) {
        try {
            out.writeUTF("/end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
