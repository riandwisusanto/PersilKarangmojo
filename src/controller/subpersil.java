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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Optional;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import model.kelasdatamodel;
import model.persildatamodel;
import model.subdatamodel;
import utils.ConnectionUtil;

/**
 * FXML Controller class
 *
 * @author Rian03
 */
public class subpersil implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private Button tambah_data;
    
    @FXML
    private Text komen;
    
    @FXML
    private AnchorPane parent_content;

    @FXML
    private TextField filter;

    @FXML
    private TableView<subdatamodel> tab_subpersil;

    @FXML
    private TableColumn<?, ?> subpersil_nomor;

    @FXML
    private TableColumn<?, ?> subpersil_no;

    @FXML
    private TableColumn<?, ?> subpersil_tgl;

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
    public Text nama_persil;

    @FXML
    public Text nomor_persil;

    @FXML
    private TextField nomor_huruf;

    @FXML
    private TextField k_ha;

    @FXML
    private ComboBox<String> kelas_desa;

    @FXML
    private TextField k_rp;

    @FXML
    private TextArea ket;

    @FXML
    private DatePicker tgl_perubahan;

    @FXML
    private Button back;
    
    @FXML
    private TextField total_rp;

    @FXML
    private TextField total_s;
    
    @FXML
    private Button tambah_kelas;
    
    private ObservableList combox;
    private ObservableList<kelasdatamodel> combo;
    private ObservableList<subdatamodel> sub;
    int id_persil;
    
    String sql;
    Connection con = null;
    PreparedStatement ps;
    Statement st;
    ResultSet resultSet;
    int cek;
    persildatamodel data;
    
    public subpersil() {
        con = ConnectionUtil.conDB();
    }

    @FXML
    void batal(ActionEvent event) {
        batalkan();
    }

    @FXML
    void btn_back(ActionEvent event) throws IOException {
        AnchorPane kelas = FXMLLoader.load(getClass().getResource("/fxml/persil.fxml"));
        
        parent_content.getChildren().setAll(kelas);
    }

    @FXML
    void btn_refresh(ActionEvent event) {
        sub.clear();
        load();
        filter.setText("");
    }

    @FXML
    void hapus_data(ActionEvent event) {
        Alert al = new Alert(AlertType.NONE, "Hapus data ini ?", ButtonType.YES, ButtonType.NO);
        al.initStyle(StageStyle.UNDECORATED);
        al.showAndWait()
            .filter(response -> response == ButtonType.YES)
            .ifPresent(response -> { hapus(); });
    }
    
    void hapus()
    {
        String sql = "delete from subpersil where id = '"+id_persil+"';";
            try {
                st = con.createStatement();
                st.execute(sql);
                sub.clear();
                batalkan();
                load();
            } catch (SQLException ex) {
                Logger.getLogger(kelasdesa.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    @FXML
    void perbaharui_data(ActionEvent event) {
        String no_huruf = nomor_huruf.getText();
        String keterangan = ket.getText();
        String kelas;
        String tgl;
        if(kelas_desa.getSelectionModel().getSelectedItem() == null)
        {
            kelas = "";
        }
        else
        {
            kelas = kelas_desa.getSelectionModel().getSelectedItem().toString();
        }
        
        if(tgl_perubahan.getValue() == null)
        {
            tgl = "";
        }
        else
        {
            tgl = tgl_perubahan.getValue().toString();
        }
        
        if(no_huruf.isEmpty()){
            Alert al = new Alert(AlertType.NONE, "Nomor & Huruf kosong", ButtonType.OK);
            al.show();
        }
        else if(kelas.isEmpty())
        {
            Alert al = new Alert(AlertType.NONE, "Pilih kelas desa", ButtonType.OK);
            al.show();
        }
        else if(tgl.isEmpty())
        {
            Alert al = new Alert(AlertType.NONE, "Tanggal Perubahan kosong", ButtonType.OK);
            al.show();
        }
        else if(k_ha.getText().isEmpty())
        {
            Alert al = new Alert(AlertType.NONE, "Ha kosong", ButtonType.OK);
            al.show();
        }
        else if(k_rp.getText().isEmpty())
        {
            Alert al = new Alert(AlertType.NONE, "Rp kosong", ButtonType.OK);
            al.show();
        }
        else if(keterangan.isEmpty())
        {
            Alert al = new Alert(AlertType.NONE, "Keterangan Perubahan kosong", ButtonType.OK);
            al.show();
        }
        else
        {
            int id = 0;
            double ha = Double.parseDouble(k_ha.getText());
            String rpp = k_rp.getText().replaceAll(",", "");
            long rp = Integer.parseInt(rpp);
            if(kelas.isEmpty() == false)
            {
                for(int i=0; i< combo.size(); i++){
                    if(combo.get(i).getNama().equals(kelas))
                    {
                        id = combo.get(i).getId();
                    }
                }
            }
//            System.out.print(combo.size());
//            System.out.print(kelas_desa.getSelectionModel().getSelectedItem().toString());
//,nopersil,id_kelas,sub_ha,sub_da,sub_rp,sub_s,sebab,tanggal_perubahan)
            String sql = "UPDATE subpersil set "
                    + "id_persil='"+data.getId()+"',"
                    + "nopersil='"+no_huruf+"',"
                    + "id_kelas='"+id+"',"
                    + "sub_ha='"+ha+"',"
                    + "sub_rp='"+rp+"',"
                    + "sebab='"+keterangan+"',"
                    + "tanggal_perubahan='"+tgl+"'"
                    + " where id='"+id_persil+"';";
            try {
                st = con.createStatement();
                st.execute(sql);
                st.close();
                Alert al = new Alert(AlertType.NONE, "Data Behasil diedit", ButtonType.OK);
                al.initStyle(StageStyle.UNDECORATED);
                al.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> { 
                        clear(); 
                        sub.clear();
                        load();
                    });
            } catch (SQLException ex) {
                Logger.getLogger(subpersil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    void tambah_data(ActionEvent event) {
        String no_huruf = nomor_huruf.getText();
        String keterangan = ket.getText();
        String kelas;
        String tgl;
        if(kelas_desa.getSelectionModel().getSelectedItem() == null)
        {
            kelas = "";
        }
        else
        {
            kelas = kelas_desa.getSelectionModel().getSelectedItem().toString();
        }
        
        if(tgl_perubahan.getValue() == null)
        {
            tgl = "";
        }
        else
        {
            tgl = tgl_perubahan.getValue().toString();
        }
        
        if(no_huruf.isEmpty()){
            Alert al = new Alert(AlertType.NONE, "Nomor & Huruf kosong", ButtonType.OK);
            al.show();
        }
        else if(kelas.isEmpty())
        {
            Alert al = new Alert(AlertType.NONE, "Pilih kelas desa", ButtonType.OK);
            al.show();
        }
        else if(tgl.isEmpty())
        {
            Alert al = new Alert(AlertType.NONE, "Tanggal Perubahan kosong", ButtonType.OK);
            al.show();
        }
        else if(k_ha.getText().isEmpty())
        {
            Alert al = new Alert(AlertType.NONE, "Ha kosong", ButtonType.OK);
            al.show();
        }
        else if(k_rp.getText().isEmpty())
        {
            Alert al = new Alert(AlertType.NONE, "Rp kosong", ButtonType.OK);
            al.show();
        }
        else if(keterangan.isEmpty())
        {
            Alert al = new Alert(AlertType.NONE, "Keterangan Perubahan kosong", ButtonType.OK);
            al.show();
        }
        else
        {
            int id = 0;
            double ha = Double.parseDouble(k_ha.getText());
            String rpp = k_rp.getText().replaceAll(",", "");
            long rp = Integer.parseInt(rpp);
            if(kelas.isEmpty() == false)
            {
                for(int i=0; i< combo.size(); i++){
                    if(combo.get(i).getNama().equals(kelas))
                    {
                        id = combo.get(i).getId();
                    }
                }
            }
//            System.out.print(combo.size());
//            System.out.print(kelas_desa.getSelectionModel().getSelectedItem().toString());
            String sql = "INSERT INTO subpersil(id_persil,nopersil,id_kelas,sub_ha,sub_rp,sebab,tanggal_perubahan)"
                    + "VALUES("
                    + "'"+data.getId()+"',"
                    + "'"+no_huruf+"',"
                    + "'"+id+"',"
                    + "'"+ha+"',"
                    + "'"+rp+"',"
                    + "'"+keterangan+"',"
                    + "'"+tgl+"'"
                    + ");";
            try {
                st = con.createStatement();
                st.execute(sql);
                st.close();
                Alert al = new Alert(AlertType.NONE, "Data Behasil disimpan", ButtonType.OK);
                al.initStyle(StageStyle.UNDECORATED);
                al.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> { 
                        clear(); 
                        sub.clear();
                        load();
                    });
            } catch (SQLException ex) {
                Logger.getLogger(subpersil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void clear()
    {
        nomor_huruf.setText("");
        kelas_desa.setValue(null);
        tgl_perubahan.setValue(null);
        k_ha.setText("");
        k_rp.setText("");
        ket.setText("");
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
    
    private void fillcombobox(){
        combo.clear();
        combox.clear();
        try {
            int i = 0;
            ps = con.prepareStatement("select * from kelas_desa");
            resultSet = ps.executeQuery();
            while(resultSet.next()){
                i++;
                combox.add(resultSet.getString(2));
                combo.add(new kelasdatamodel(resultSet.getInt("id"),i,resultSet.getString("nama")));
            }
            ps.close();
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(subpersil.class.getName()).log(Level.SEVERE, null, ex);
        }
        kelas_desa.setItems(null);
        kelas_desa.setItems(combox);
    }
    
    void loadnama()
    {
        try {
            ps = con.prepareStatement("select * from passing where set_id='pass'");
            resultSet = ps.executeQuery();
            while(resultSet.next()){
                nama_persil.setText(resultSet.getString("nama"));
                nomor_persil.setText(resultSet.getString("nomor"));
                data = new persildatamodel(resultSet.getInt("id"),resultSet.getInt("nomor"),resultSet.getString("nama"));
            }
            ps.close();
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(subpersil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void setcell()
    {
        subpersil_nomor.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        subpersil_no.setCellValueFactory(new PropertyValueFactory<>("no_huruf"));
        subpersil_tgl.setCellValueFactory(new PropertyValueFactory<>("tgl_perubahan"));
    }
    
    void load()
    {
        long jumlah_rp = 0;
        try {
            int i = 0;
            ps = con.prepareStatement("select * from subpersil where id_persil='"+data.getId()+"'");
            resultSet = ps.executeQuery();
            while(resultSet.next()){
                i++;
                sub.add(new subdatamodel(resultSet.getInt("id"), i ,resultSet.getString("nopersil"),
                        resultSet.getInt("id_kelas"),resultSet.getDouble("sub_ha"),
                        resultSet.getLong("sub_rp"),resultSet.getString("tanggal_perubahan"),
                        resultSet.getString("sebab")));
                jumlah_rp = jumlah_rp + resultSet.getLong("sub_rp");
            }
            ps.close();
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(kelasdesa.class.getName()).log(Level.SEVERE, null, ex);
        }
        tab_subpersil.setItems(sub);
        total_rp.setText(Long.toString(jumlah_rp));
        search();
    }
    
    void pilihsub()
    {
        tab_subpersil.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                tambah_data.setDisable(true);
                perbaharui_data.setDisable(false);
                hapus_data.setDisable(false);
                batal.setDisable(false);
                report.setText("");

                sub = FXCollections.observableArrayList();
                subdatamodel pl=tab_subpersil.getItems().get(tab_subpersil.getSelectionModel().getSelectedIndex());
                nomor_huruf.setText(pl.getNo_huruf());
                k_rp.setText(new DecimalFormat("#,##0").format(pl.getRp()));
                ket.setText(pl.getKeterangan());
                k_ha.setText(Double.toString(pl.getHa()));
                id_persil = pl.getId();
                for(int i=0;i<combo.size();i++){
                    if(combo.get(i).getId() == pl.getId_kelas()){
                        kelas_desa.setValue(combo.get(i).getNama());
                    }
                }
                LocalDate dt = LocalDate.parse(pl.getTgl_perubahan());
                tgl_perubahan.setValue(dt);
            }
        });
    }
    
    void batalkan()
    {
        tambah_data.setDisable(false);
        perbaharui_data.setDisable(true);
        hapus_data.setDisable(true);
        batal.setDisable(true);
        clear();
    }
    
    void search()
    {
        FilteredList<subdatamodel> filteredData = new FilteredList<>(sub, p -> true);
		
        filter.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(person -> {
                    if (newValue == null || newValue.isEmpty()) {
                            return true;
                    }

                    // Compare first name and last name of every person with filter text.
                    String lowerCaseFilter = newValue.toLowerCase();

                    if (Integer.toString(person.getNomor()).toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches first name.
                    }
                    else if(person.getNo_huruf().toLowerCase().contains(lowerCaseFilter)){
                        return true;
                    }
                    else if(person.getTgl_perubahan().toLowerCase().contains(lowerCaseFilter)){
                        return true;
                    }
                    return false; // Does not match.
                });
        });

        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<subdatamodel> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tab_subpersil.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tab_subpersil.setItems(sortedData);
    }
    
    @FXML
    void tambah_kelas(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setHeaderText("Tambah Kelas Desa");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();

        // The Java 8 way to get the response value (with lambda expression).
        result.ifPresent(name -> add_kelas(name));
    }
    
    void add_kelas(String nama){
        String sql = "INSERT INTO kelas_desa(nama) VALUES('"+nama+"');";
        try {
            st = con.createStatement();
            st.execute(sql);
            st.closeOnCompletion();
            fillcombobox();
            kelas_desa.setValue(nama);
        } catch (SQLException ex) {
            Logger.getLogger(kelasdesa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void ie(ActionEvent event) throws IOException {
        AnchorPane kelas = FXMLLoader.load(getClass().getResource("/fxml/ie.fxml"));
        
        parent_content.getChildren().setAll(kelas);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        combo = FXCollections.observableArrayList();
        combox = FXCollections.observableArrayList();
        sub = FXCollections.observableArrayList();
        loadnama();
        fillcombobox();
        setcell();
        load();
        pilihsub();
        k_rp.setOnKeyTyped(event -> {
            String typedCharacter = event.getCharacter();
            event.consume();

            if (typedCharacter.matches("\\d*")) {
                String currentText = k_rp.getText().replaceAll("\\.", "").replace(",", "");
                long longVal = Long.parseLong(currentText.concat(typedCharacter));
                k_rp.setText(new DecimalFormat("#,##0").format(longVal));
            }
        });
    }    
    
}
