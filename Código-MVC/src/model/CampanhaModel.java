
package model;

import java.util.Date;


public class CampanhaModel extends VacinaModel{
    
    private Date dataInicial;
    private Date dataFinal;

    public CampanhaModel(Date dataInicial, Date dataFinal, String nome, Date validade, int lote) {
        super(nome, validade, lote);
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
    }

    public CampanhaModel() {
       //To change body of generated methods, choose Tools | Templates.
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }
    
    
    
}
