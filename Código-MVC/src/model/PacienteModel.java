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
public class PacienteModel extends PessoaModel {
    
    private int codigo; 

    public PacienteModel(int codigo, String nome, String cpf, char sexo, Date dataNasc) {
        super(nome, cpf, sexo, dataNasc);
        this.codigo = codigo;
    }

    public PacienteModel() {
        //To change body of generated methods, choose Tools | Templates.
    }
    
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    
}
