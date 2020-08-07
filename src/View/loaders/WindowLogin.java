package View.loaders;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class WindowLogin {

    public void startModal(){
        try {
            FXMLLoader loader = new FXMLLoader();
            Pane pane = loader.load(getClass().getResource("/View/fxml/WindowLogin.fxml").openStream());

            Stage stage = new Stage();

            stage.setTitle("Login");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(pane, 350, 318));
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}