
package lps.vacinacao;

import java.util.Date;


public class CampanhaVacina extends Vacina{
    
    private Date dataInicial;
    private Date dataFinal;

    public CampanhaVacina(Date dataInicial, Date dataFinal, String nome, Date validade, int lote) {
        super(nome, validade, lote);
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
    }

    public CampanhaVacina() {
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
