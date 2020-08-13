/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Rian03
 */
public class subdatamodel {
    int id;
    int nomor;
    String no_huruf;
    int id_kelas;
    double ha;
    long rp;
    String tgl_perubahan;
    String keterangan;

    public subdatamodel(int id, int nomor, String no_huruf, int id_kelas, double ha, long rp, String tgl_perubahan, String keterangan) {
        this.id = id;
        this.nomor = nomor;
        this.no_huruf = no_huruf;
        this.id_kelas = id_kelas;
        this.ha = ha;
        this.rp = rp;
        this.tgl_perubahan = tgl_perubahan;
        this.keterangan = keterangan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNomor() {
        return nomor;
    }

    public void setNomor(int nomor) {
        this.nomor = nomor;
    }

    public String getNo_huruf() {
        return no_huruf;
    }

    public void setNo_huruf(String no_huruf) {
        this.no_huruf = no_huruf;
    }

    public int getId_kelas() {
        return id_kelas;
    }

    public void setId_kelas(int id_kelas) {
        this.id_kelas = id_kelas;
    }

    public double getHa() {
        return ha;
    }

    public void setHa(double ha) {
        this.ha = ha;
    }

    public long getRp() {
        return rp;
    }

    public void setRp(long rp) {
        this.rp = rp;
    }

    public String getTgl_perubahan() {
        return tgl_perubahan;
    }

    public void setTgl_perubahan(String tgl_perubahan) {
        this.tgl_perubahan = tgl_perubahan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

   
}
