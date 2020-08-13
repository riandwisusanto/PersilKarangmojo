/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.kelasdatamodel;
import utils.ConnectionUtil;

/**
 * FXML Controller class
 *
 * @author Rian03
 */
public class kelasdesa implements Initializable {

    /**
     * Initializes the controller class.
     */
     @FXML
    private Button tambah_data;
     
     @FXML
    private AnchorPane parent_content;

    @FXML
    private TableView<kelasdatamodel> tab_kelas;

    @FXML
    private TableColumn<?, ?> kelas_nomor;

    @FXML
    private TableColumn<?, ?> kelas_nama;

    @FXML
    private TextField nama_kelas;

    @FXML
    private Button perbaharui_data;

    @FXML
    private Button hapus_data;
    
    @FXML
    private Text report;
    
    @FXML
    private Button batal;
    
    @FXML
    private Button refresh;
    
    @FXML
    private TextField filter;
    
    @FXML
    private Text komen;
    
    private ObservableList<kelasdatamodel> tabel_kelas;
    
    String sql;
    Connection con = null;
    PreparedStatement ps;
    Statement st;
    ResultSet resultSet;
    int cek;
    
    public kelasdesa() {
        con = ConnectionUtil.conDB();
    }

//    @FXML
//    void tambah_data(ActionEvent event) throws IOException {
//        Stage stage = new Stage();
//        
//        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/fxml/tambah_data.fxml")));
//        stage.setScene(scene);
//        stage.sizeToScene();
//        stage.setResizable(false);
//        stage.initStyle(StageStyle.UNDECORATED);
//        stage.showAndWait();
//    }
     private void settable()
    {
        kelas_nomor.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        kelas_nama.setCellValueFactory(new PropertyValueFactory<>("nama"));
    }
    
    private void load()
    {
        try {
            int i = 0;
            ps = con.prepareStatement("select * from kelas_desa");
            resultSet = ps.executeQuery();
            while(resultSet.next()){
                i++;
                tabel_kelas.add(new kelasdatamodel(resultSet.getInt(1), i ,resultSet.getString(2)));
            }
            ps.close();
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(kelasdesa.class.getName()).log(Level.SEVERE, null, ex);
        }
        tab_kelas.setItems(tabel_kelas);
        search();
    }

    @FXML
    void hapus_data(ActionEvent event) {
        String nm = nama_kelas.getText();
        if(nm.isEmpty())
        {
            report.setText("Nama Kelas Kosong");
        }
        else
        {
            Alert al = new Alert(Alert.AlertType.NONE, "Menghapus kelas desa akan menghapus data persil dengan kelas desa yang sama, hapus ?", ButtonType.YES, ButtonType.NO);
            al.initStyle(StageStyle.UNDECORATED);
            al.showAndWait()
                .filter(response -> response == ButtonType.YES)
                .ifPresent(response -> { 
                    String sql_sub = "delete from subpersil where id_kelas = '"+cek+"';";
                    try {
                        st = con.createStatement();
                        st.execute(sql_sub);
                        st.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(persil.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   String sql = "delete from kelas_desa where id = '"+cek+"';";
                    try {
                        st = con.createStatement();
                        st.execute(sql);
                        tabel_kelas.clear();
                        nama_kelas.setText("");
                        report.setText("Berhasil Dihapus");
                        batalkan();
                        settable();
                        load();
                    } catch (SQLException ex) {
                        Logger.getLogger(kelasdesa.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                });
        }
    }

    @FXML
    void perbaharui_data(ActionEvent event) {
        String nm = nama_kelas.getText();
        if(nm.isEmpty())
        {
            report.setText("Nama Kelas Kosong");
        }
        else
        {
            String sql = "Update kelas_desa set nama = '"+nm+"' where id = '"+cek+"';";
            try {
                st = con.createStatement();
                st.execute(sql);
                tabel_kelas.clear();
                nama_kelas.setText("");
                report.setText("Berhasil Diperbaharui");
                batalkan();
                settable();
                load();
            } catch (SQLException ex) {
                Logger.getLogger(kelasdesa.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @FXML
    void batal(ActionEvent event) {
        batalkan();
    }
    
    void batalkan()
    {
        tambah_data.setDisable(false);
        perbaharui_data.setDisable(true);
        hapus_data.setDisable(true);
        batal.setDisable(true);
        nama_kelas.setText("");
    }

    @FXML
    void tambah_data(ActionEvent event) {
        String nm = nama_kelas.getText();
        if(nm.isEmpty())
        {
            report.setText("Nama Kelas Kosong");
        }
        else
        {
            String sql = "INSERT INTO kelas_desa(nama) VALUES('"+nm+"');";
            try {
                st = con.createStatement();
                st.execute(sql);
                tabel_kelas.clear();
                nama_kelas.setText("");
                report.setText("Berhasil Ditambahkan");
                settable();
                load();
            } catch (SQLException ex) {
                Logger.getLogger(kelasdesa.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void pilihkelas()
    {
        tab_kelas.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                tambah_data.setDisable(true);
                perbaharui_data.setDisable(false);
                hapus_data.setDisable(false);
                batal.setDisable(false);
                report.setText("");

                tabel_kelas = FXCollections.observableArrayList();
                kelasdatamodel pl=tab_kelas.getItems().get(tab_kelas.getSelectionModel().getSelectedIndex());
                cek = pl.getId();
                nama_kelas.setText(pl.getNama());
            }
        });
    }
    
     @FXML
    void btn_refresh(ActionEvent event) {
        tabel_kelas.clear();
        batalkan();
        load();
        filter.setText("");
    }
    
    void search()
    {
        FilteredList<kelasdatamodel> filteredData = new FilteredList<>(tabel_kelas, p -> true);
		
		// 2. Set the filter Predicate whenever the filter changes.
        filter.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(person -> {
                        // If filter text is empty, display all persons.
                        if (newValue == null || newValue.isEmpty()) {
                                return true;
                        }

                        // Compare first name and last name of every person with filter text.
                        String lowerCaseFilter = newValue.toLowerCase();

                        if (person.getNama().toLowerCase().contains(lowerCaseFilter)) {
                                return true; // Filter matches first name.
                        }
                        return false; // Does not match.
                });
        });

        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<kelasdatamodel> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tab_kelas.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tab_kelas.setItems(sortedData);
    }
    
    @FXML
    void data_persil(ActionEvent event) throws IOException {
        AnchorPane kelas = FXMLLoader.load(getClass().getResource("/fxml/persil.fxml"));
        
        parent_content.getChildren().setAll(kelas);
    }

    @FXML
    void kelas_desa(ActionEvent event) throws IOException {
        AnchorPane kelas = FXMLLoader.load(getClass().getResource("/fxml/kelasdesa.fxml"));
        
        parent_content.getChildren().setAll(kelas);
    }
    
    @FXML
    void ie(ActionEvent event) throws IOException {
        AnchorPane kelas = FXMLLoader.load(getClass().getResource("/fxml/ie.fxml"));
        
        parent_content.getChildren().setAll(kelas);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tabel_kelas = FXCollections.observableArrayList();
        settable();
        load();
        pilihkelas();
        cek = 0;
        
        if (con == null) {
            komen.setText("Server Error : Check");
        } else {
            komen.setText("Server is up : Good to go");
        }
    }    
    
}
