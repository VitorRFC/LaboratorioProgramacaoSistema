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
public class EnfermeiroModel extends PessoaModel{
    
    private int matricula;

    public EnfermeiroModel(int matricula, String nome, String cpf, char sexo, Date dataNasc) {
        super(nome, cpf, sexo, dataNasc);
        this.matricula = matricula;
    }

    public EnfermeiroModel() {
        //To change body of generated methods, choose Tools | Templates.
    }
   
    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }
    
    
    
}
