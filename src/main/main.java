/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Rian03
 */
public class main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/persil.fxml"));
        stage.initStyle(StageStyle.DECORATED);
        stage.setMaximized(false);
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        Image gambar = new Image("/images/logo.png");
        stage.getIcons().add(gambar);
        stage.setTitle("DPK");
        stage.sizeToScene();
        stage.setResizable(false);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
