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
import java.util.prefs.Preferences;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.persildatamodel;
import utils.ConnectionUtil;


/**
 * FXML Controller class
 *
 * @author Rian03
 */
public class persil implements Initializable {

    /**
     * Initializes the controller class.
     */
    
     @FXML
    private Button tambah_data;
     
     @FXML
    private AnchorPane parent_content;

    @FXML
    public TextField filter;

    @FXML
    private TableView<persildatamodel> tab_persil;

    @FXML
    private TableColumn<?, ?> persil_nomor;

    @FXML
    private TableColumn<?, ?> persil_nama;

    @FXML
    private TextField nama_pemilik;

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
    private Button detail;
    
    @FXML
    private Text komen;
    
    @FXML
    private TextField nomor_pemilik;

    
    private ObservableList<persildatamodel> tabel_persil;
    
    String sql;
    Connection con = null;
    PreparedStatement ps;
    Statement st;
    ResultSet resultSet;
    persildatamodel cek;
    
    public persil() {
        con = ConnectionUtil.conDB();
    }
    
     private void settable()
    {
        persil_nomor.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        persil_nama.setCellValueFactory(new PropertyValueFactory<>("nama"));
    }
     
    private void load()
    {
        try {
            int i = 0;
            ps = con.prepareStatement("select * from persil");
            resultSet = ps.executeQuery();
            while(resultSet.next()){
                tabel_persil.add(new persildatamodel(resultSet.getInt(1), resultSet.getInt(3) ,resultSet.getString(2)));
            }
            ps.close();
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(kelasdesa.class.getName()).log(Level.SEVERE, null, ex);
        }
        tab_persil.setItems(tabel_persil);
        search();
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
        nama_pemilik.setText("");
        detail.setDisable(true);
        nomor_pemilik.setText("");
    }
    
    @FXML
    void btn_detail(ActionEvent event) throws IOException {
        String sql = "Update passing set id='"+cek.getId()+"', nama='"+cek.getNama()+"', nomor='"+cek.getNomor()+"' where set_id='pass';";
        try {
            st = con.createStatement();
            st.execute(sql);
            st.closeOnCompletion();
            AnchorPane kelas = FXMLLoader.load(getClass().getResource("/fxml/subpersil.fxml"));
            parent_content.getChildren().setAll(kelas);
        } catch (SQLException ex) {
            Logger.getLogger(persil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void btn_refresh(ActionEvent event) {
        tabel_persil.clear();
        batalkan();
        load();
        filter.setText("");
    }

    @FXML
    void hapus_data(ActionEvent event) {
        String nm = nama_pemilik.getText();
        if(nm.isEmpty())
        {
            report.setText("Nama Kelas Kosong");
        }
        else
        {
            Alert al = new Alert(Alert.AlertType.NONE, "Hapus data ini ?", ButtonType.YES, ButtonType.NO);
            al.initStyle(StageStyle.UNDECORATED);
            al.showAndWait()
                .filter(response -> response == ButtonType.YES)
                .ifPresent(response -> { 
                    String sql_sub = "delete from subpersil where id_persil = '"+cek.getId()+"';";
                    try {
                        st = con.createStatement();
                        st.execute(sql_sub);
                        st.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(persil.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    String sql = "delete from persil where id = '"+cek.getId()+"';";
                    try {
                        st = con.createStatement();
                        st.execute(sql);
                        tabel_persil.clear();
                        nama_pemilik.setText("");
                        report.setText("Berhasil Dihapus");
                        batalkan();
                        load();
                    } catch (SQLException ex) {
                        Logger.getLogger(persil.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
        }
    }

    @FXML
    void perbaharui_data(ActionEvent event) {
        String nm = nama_pemilik.getText();
        String no = nomor_pemilik.getText();
        if(nm.isEmpty())
        {
            report.setText("Nama Pemilik Kosong");
        }
        else if(no.isEmpty()){
            report.setText("Nomor Pemilik Kosong");
        }
        else
        {
            String sql = "Update persil set nama = '"+nm+"',nomor = '"+no+"' where id = '"+cek.getId()+"';";
            try {
                st = con.createStatement();
                st.execute(sql);
                tabel_persil.clear();
                nama_pemilik.setText("");
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
    void tambah_data(ActionEvent event) {
        String nm = nama_pemilik.getText();
        String no = nomor_pemilik.getText();
        if(nm.isEmpty())
        {
            report.setText("Nama Kelas Kosong");
        }
        else if(no.isEmpty()){
            report.setText("Nomor Pemilik Kosong");
        }
        else
        {
            String sql = "INSERT INTO persil(nama,nomor) VALUES('"+nm+"','"+no+"');";
            try {
                st = con.createStatement();
                st.execute(sql);
                tabel_persil.clear();
                nama_pemilik.setText("");
                nomor_pemilik.setText("");
                report.setText("Berhasil Ditambahkan");
                settable();
                load();
            } catch (SQLException ex) {
                Logger.getLogger(kelasdesa.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void pilihpersil()
    {
        tab_persil.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                tambah_data.setDisable(true);
                perbaharui_data.setDisable(false);
                hapus_data.setDisable(false);
                batal.setDisable(false);
                detail.setDisable(false);
                report.setText("");
                
                tabel_persil = FXCollections.observableArrayList();
                persildatamodel pl=tab_persil.getItems().get(tab_persil.getSelectionModel().getSelectedIndex());
                cek = pl;
                nomor_pemilik.setText(Integer.toString(cek.getNomor()));
                nama_pemilik.setText(cek.getNama());
            }
        });
    }
    
    void search()
    {
        FilteredList<persildatamodel> filteredData = new FilteredList<>(tabel_persil, p -> true);
		
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
                        else if(Integer.toString(person.getNomor()).toLowerCase().contains(lowerCaseFilter)){
                            return true;
                        }
                        return false; // Does not match.
                });
        });

        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<persildatamodel> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tab_persil.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tab_persil.setItems(sortedData);
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
        tabel_persil = FXCollections.observableArrayList();
        settable();
        load();
        pilihpersil();
        cek = null;
    }    
    
}
