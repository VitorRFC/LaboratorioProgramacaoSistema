/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author Vitor
 */
public class BaseControle {
    
    private SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM/yyyy");
    
    public String formatarData(Date data){
       return formatarData.format(data);
    } 
    
    public boolean verificaCPF(String cpf){

        int digito1=0, digito2=0, calcDig1=0, calcDig2=0, x=10, y=11;
        int [] arrayCPF;
        arrayCPF = new int[9];
        boolean repetida = true; 

        digito1 = Integer.parseInt(cpf.substring(12, 13));
        digito2 = Integer.parseInt(cpf.substring(13, 14));
        cpf = cpf.substring(0, 3) + cpf.substring(4, 7) + cpf.substring(8, 11);

        for (int i=0; i<arrayCPF.length; i++){

            arrayCPF[i] = Integer.parseInt(cpf.substring(i, i+1));

            calcDig1 += x * arrayCPF[i];
            x--;

            calcDig2 += y * arrayCPF[i];
            y--;

            if(arrayCPF[0] != arrayCPF[i] && repetida)
                repetida = false;
        }
        calcDig2 += digito1 * y;

        calcDig1 = calcDig1 * 10 % 11;
        calcDig2 = calcDig2 * 10 % 11;

        if (calcDig1 == 10)
            calcDig1 = 0;

        if (calcDig2 == 10)
            calcDig2 = 0;

        if(calcDig1 != digito1 || calcDig2 != digito2 || repetida)
            return false;
        else
            return true;
    }
    
    public void camposFormatadoData(JFormattedTextField data){
        try {
            MaskFormatter maskData = new MaskFormatter("##/##/####");
            maskData.install(data);

        } catch (ParseException ex) {
            Logger.getLogger(BaseControle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void camposFormatadoCPF(JFormattedTextField cpf){
        try {
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
             
            maskCPF.install(cpf);
            
        } catch (ParseException ex) {
            Logger.getLogger(BaseControle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Date stringToDate(String data){
        try {
            return formatarData.parse(data);
        } catch (ParseException ex) {
            Logger.getLogger(BaseControle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
