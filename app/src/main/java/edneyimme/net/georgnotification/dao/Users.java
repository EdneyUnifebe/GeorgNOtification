package edneyimme.net.georgnotification.dao;

import java.io.Serializable;

public class Users implements Serializable{

    private String id;
    private String nome;
    private String fileName;

    public Users(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Users(String id, String nome, String fileName) {
        this.id = id;
        this.nome = nome;
        this.fileName = fileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
