/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;
import model.kelasdatamodel;
import model.persildatamodel;
import model.subdatamodel;
import utils.ConnectionUtil;

/**
 * FXML Controller class
 *
 * @author Rian03
 */
public class ie implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private AnchorPane parent_content;

    @FXML
    private Text komen;
    
    @FXML
    private Text selesai_eksport;

    @FXML
    private Text selesai_import;
    
    String sql;
    Connection con = null;
    PreparedStatement ps;
    ResultSet result;
    int cek;
    
    public ie() {
        con = ConnectionUtil.conDB();
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
    
     @FXML
    void import_btn(ActionEvent event) {
        selesai_eksport.setVisible(false);
        Node node = (Node) event.getSource();
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("DB", "*.db")
        );
        chooser.setTitle("Import Data");
//        chooser.showOpenDialog(node.getScene().getWindow());
        File file = chooser.showOpenDialog(node.getScene().getWindow());
        if(file != null){
//            System.out.print(file.getAbsolutePath()+"\n");
//            System.out.print(file.getName()+"\n");
//            System.out.print(file.getPath());
            insert(file);
        }
        
    }
    
    ObservableList kls_fix = FXCollections.observableArrayList();
    ObservableList persil_fix = FXCollections.observableArrayList();
    
    void insert(File file){
        ObservableList<kelasdatamodel> kelas = FXCollections.observableArrayList();
        ObservableList<persildatamodel> persil = FXCollections.observableArrayList();
        ObservableList<subdatamodel> subpersil = FXCollections.observableArrayList();
        
        int i=0;
        String database = "jdbc:sqlite:"+file.getAbsolutePath();
        Connection cn = null;
        try {
            cn = DriverManager.getConnection(database);
            System.err.println("Database berhasil");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        try {
            PreparedStatement pss = cn.prepareStatement("select * from kelas_desa");
            ResultSet resultSet = pss.executeQuery();
            while(resultSet.next()){
                i++;
                kelas.add(new kelasdatamodel(resultSet.getInt(1), i ,resultSet.getString(2)));
//                System.err.println(" "+resultSet.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ie.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            PreparedStatement ps = cn.prepareStatement("select * from persil");
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                persil.add(new persildatamodel(resultSet.getInt(1), resultSet.getInt(3) ,resultSet.getString(2)));
            }
            ps.close();
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(ie.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            ps = cn.prepareStatement("select * from subpersil");
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                subpersil.add(new subdatamodel(resultSet.getInt("id"), resultSet.getInt("id_persil") ,resultSet.getString("nopersil"),
                        resultSet.getInt("id_kelas"),resultSet.getDouble("sub_ha"),
                        resultSet.getLong("sub_rp"),resultSet.getString("tanggal_perubahan"),
                        resultSet.getString("sebab")));
            }
            ps.close();
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(ie.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        inserttokelas(kelas);
        inserttopersil(persil);
        inserttosub(subpersil);
        
    }
    
    void inserttokelas(ObservableList<kelasdatamodel> kelas){
        ObservableList<kelasdatamodel> kls = FXCollections.observableArrayList();
        int i  = 0;
        try {
            ps = con.prepareStatement("select * from kelas_desa");
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                i++;
                kls.add(new kelasdatamodel(resultSet.getInt(1), i ,resultSet.getString(2)));
//                System.err.println(" "+resultSet.getInt(1));
            }
            ps.close();
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(ie.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(int x=0;x<kelas.size();x++){
            int s = 0;
            if(kls != null){
                for(int y=0;y<kls.size();y++){
                    if(kelas.get(x).getNama().equals(kls.get(y).getNama())){
                        s = 1;
                        break;
                    }
                }
            }
            if(s==0){
                String sql = "INSERT INTO kelas_desa(nama) VALUES('"+kelas.get(x).getNama()+"');";
                try {
                    Statement st = con.createStatement();
                    st.execute(sql);
                    st.closeOnCompletion();
                } catch (SQLException ex) {
                    Logger.getLogger(ie.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        try {
            ps = con.prepareStatement("select * from kelas_desa");
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                int id = 0;
                int cek_y = 0;
                for(int y=0;y<kelas.size();y++){
                    if(resultSet.getString("nama").equals(kelas.get(y).getNama())){
                        id = kelas.get(y).getId();
                        cek_y = 1;
                        break;
                    }
                }
                if(cek_y == 1){
//                    System.err.println(id+"/"+resultSet.getInt(1));
                    kls_fix.add(id+"/"+resultSet.getInt(1));
                }
                else{
                    kls_fix.add("baru/"+resultSet.getInt(1));
//                    System.err.println("baru/"+resultSet.getInt(1));
                }
            }
            ps.close();
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(ie.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.print("berhasil");
    }
    
    void inserttopersil(ObservableList<persildatamodel> persil){
        ObservableList<persildatamodel> per = FXCollections.observableArrayList();
        try {
            ps = con.prepareStatement("select * from persil");
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                per.add(new persildatamodel(resultSet.getInt(1), resultSet.getInt(3) ,resultSet.getString(2)));
            }
            ps.close();
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(ie.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(int x=0;x<persil.size();x++){
            int s = 0;
            if(per != null){
                for(int y=0;y<per.size();y++){
                if(persil.get(x).getNama().equals(per.get(y).getNama()) 
                            && Integer.toString(persil.get(x).getNomor()).equals(Integer.toString(per.get(y).getNomor()))){
                        s = 1;
                        break;
                    }
                }
            }
            if(s==0){
                String sql = "INSERT INTO persil(nama,nomor) VALUES('"+persil.get(x).getNama()+"','"+persil.get(x).getNomor()+"');";
                try {
                    Statement st = con.createStatement();
                    st.execute(sql);
                } catch (SQLException ex) {
                    Logger.getLogger(ie.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        try {
            ps = con.prepareStatement("select * from persil");
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                int id = 0;
                int cek_y = 0;
                for(int y=0;y<persil.size();y++){
                    if(resultSet.getString("nama").equals(persil.get(y).getNama()) 
                            && Integer.toString(resultSet.getInt("nomor")).equals(Integer.toString(persil.get(y).getNomor()))){
                        id = persil.get(y).getId();
                        cek_y = 1;
                        break;
                    }
                }
                if(cek_y == 1){
//                    System.err.println(id+"/"+resultSet.getInt(1));
                    persil_fix.add(id+"/"+resultSet.getInt(1));
                }
                else{
                    persil_fix.add("baru/"+resultSet.getInt(1));
//                    System.err.println("baru/"+resultSet.getInt(1));
                }
            }
            ps.close();
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(ie.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void inserttosub(ObservableList<subdatamodel> subper){
        ObservableList<subdatamodel> sub = FXCollections.observableArrayList();
        try {
            ps = con.prepareStatement("select * from subpersil");
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                sub.add(new subdatamodel(resultSet.getInt("id"), resultSet.getInt("id_persil") ,resultSet.getString("nopersil"),
                        resultSet.getInt("id_kelas"),resultSet.getDouble("sub_ha"),
                        resultSet.getLong("sub_rp"),resultSet.getString("tanggal_perubahan"),
                        resultSet.getString("sebab")));
            }
            ps.close();
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(ie.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(int ss=0;ss<subper.size();ss++){
            int bg = 0;
//            System.out.print("\n id sub = "+subper.get(ss).getNomor()+"\n");
            for(int jum=0;jum<persil_fix.size();jum++){
                String bgg[] = persil_fix.get(jum).toString().split("/");
                if(bgg[0].toString().equals(Integer.toString(subper.get(ss).getNomor()))){
                    bg = Integer.parseInt(bgg[1]);
                }
//                System.out.print(" - "+bgg[0]+"/"+bgg[1]+"\n");
            }
            System.out.print(" - "+bg+"\n");
            int cek = 0;
            System.out.print("sub_asli = "+subper.get(ss).getNo_huruf()+"\n");
            for(int w=0;w<sub.size();w++){
                System.out.print("sub = "+sub.get(w).getNo_huruf()+"\n");
                if(sub.get(w).getNo_huruf().equals(subper.get(ss).getNo_huruf())){
                    cek = 1;
                }
            }
            System.out.print("cek = "+cek+"\n");
            if(cek == 0){
                String kl_fix = "";
                for(int k=0;k<kls_fix.size();k++){
                    String kl[] = kls_fix.get(k).toString().split("/");
                    if(Integer.toString(subper.get(ss).getId_kelas()).equals(kl[0])){
                        kl_fix = kl[1];
                    }
                }
                String sql = "INSERT INTO subpersil(id_persil,nopersil,id_kelas,sub_ha,sub_rp,sebab,tanggal_perubahan)"
                            + "VALUES("
                            + "'"+bg+"',"
                            + "'"+subper.get(ss).getNo_huruf()+"',"
                            + "'"+kl_fix+"',"
                            + "'"+subper.get(ss).getHa()+"',"
                            + "'"+subper.get(ss).getRp()+"',"
                            + "'"+subper.get(ss).getKeterangan()+"',"
                            + "'"+subper.get(ss).getTgl_perubahan()+"'"
                            + ");";
                try {
                    Statement st = con.createStatement();
                    st.execute(sql);
                    st.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ie.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        selesai_import.setVisible(true);
    }
//        }
//    }
    
    @FXML
    void eksport_btn(ActionEvent event){
        selesai_import.setVisible(false);
        Node node = (Node) event.getSource();
        String pat[] = ie.class.getProtectionDomain().getCodeSource().getLocation().getPath().toString().split("dist");
        String p = pat[0].replaceFirst("/", "");
        p = p.replaceAll("%20", " ");
//        System.out.print(p+"persil.db");
        File file = new File(p+"persil.db");
        if(file != null){
            System.out.print(file.toPath()+" = ");
        }
        Menu mn = new Menu("File");
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Eksport data");
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("DB", "*.db")
        );
        File f = chooser.showSaveDialog(node.getScene().getWindow());
        if(f != null){
            System.out.print(f.toPath());
            try {
                Files.copy(file.toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
                selesai_eksport.setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(ie.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
