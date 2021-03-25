/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Date;

/**
 *
 * @author Vitor
 */
public class PessoaModel {

    private String nome;
    private String cpf;
    private char sexo;
    private Date dataNasc;

    public PessoaModel(String nome, String cpf, char sexo, Date dataNasc) {
        this.nome = nome;
        this.cpf = cpf;
        this.sexo = sexo;
        this.dataNasc = dataNasc;
    }
    
    public PessoaModel() {
        //To change body of generated methods, choose Tools | Templates.
    }
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public char getSexo() {
        return sexo;
    }

    public void setSexo(char sexo) {
        this.sexo = sexo;
    }

    public Date getDataNasc() {
        return dataNasc;
    }

    public void setDataNasc(Date dataNasc) {
        this.dataNasc = dataNasc;
    }
    

    
}
