
package principal;


import gui.TelaEnfermeiro;
import gui.TelaPaciente;
import gui.TelaPrincipal;
import javax.swing.JPanel;
import utils.Persistencia;


public class LPSVacinacao {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Persistencia.conexao();
        
        System.out.println(Persistencia.statusConection());
        
        
        TelaPrincipal principal = new TelaPrincipal();
        principal.setVisible(true);
        
    }
    
}
