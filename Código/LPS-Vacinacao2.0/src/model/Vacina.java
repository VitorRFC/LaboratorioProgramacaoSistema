
package model;

import java.util.Date;

public class Vacina {
    
    private String nome;
    private Date validade;
    private int lote;

    public Vacina(String nome, Date validade, int lote) {
        this.nome = nome;
        this.validade = validade;
        this.lote = lote;
    }

    public Vacina() {
       //To change body of generated methods, choose Tools | Templates.
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    public int getLote() {
        return lote;
    }

    public void setLote(int lote) {
        this.lote = lote;
    }
    
}
