package com.courcework.pharmacyclient;

import com.courcework.pharmacyclient.api.*;
import com.courcework.pharmacyclient.controller.MainController;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class PharmacyClientApplication extends Application {

    private static final int MAX_ATTEMPTS = 20;
    private static final long DELAY_MILLIS = 500;

    @Override
    public void start(Stage stage){
        Label loadingLabel = new Label("Подключение к серверу....");
        StackPane loadingRoot = new StackPane(loadingLabel);

        stage.setTitle("Аптечный помощник");
        stage.setScene(new Scene(loadingRoot, 420, 220));
        stage.show();

        Task<Boolean> waitForServer = new Task<>() {
            @Override
            protected Boolean call()  {
                PharmacyApiClient apiClient = new PharmacyApiClient();
                return apiClient.waitingForServer(MAX_ATTEMPTS, DELAY_MILLIS);
            }
        };

        waitForServer.setOnSucceeded(event -> {
            boolean serverIsUp = Boolean.TRUE.equals(waitForServer.getValue());
            if(serverIsUp) openMainWindow(stage);
            else showConnectionError(stage, loadingLabel);
        });

        new Thread(waitForServer).start();
    }

    private void openMainWindow(Stage stage){
        try {
            URL fxmlURL = getClass().getResource("/com/courcework/pharmacyclient/pharmacy-client.fxml");
            if(fxmlURL == null) throw new IOException("Файл не найден по ожидаемому пути");

            FXMLLoader loader = new FXMLLoader(fxmlURL);
            Scene scene = new Scene(loader.load(), 900, 600);

            MainController controller = loader.getController();
            controller.setHostServices(getHostServices());

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.WARNING, "Не удалось загрузить интерфейс приложения: " + e.getMessage());
            alert.showAndWait();
            stage.close();
        }
    }

    private void showConnectionError(Stage stage, Label label){
        label.setText("Ошибка...");
        Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось подключится к серверу. \nУбедитесь, что сервер запущен и попробуйте снова.");
        alert.showAndWait();
        stage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
