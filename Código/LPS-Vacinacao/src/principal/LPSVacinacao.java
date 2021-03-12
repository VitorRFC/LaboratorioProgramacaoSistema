
package principal;


import gui.telaEnfermeiro;
import gui.telaPaciente;
import gui.telaPrincipal;
import javax.swing.JPanel;
import utils.Persistencia;


public class LPSVacinacao {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Persistencia.conexao();
        
        System.out.println(Persistencia.statusConection());
        
        
        telaPrincipal principal = new telaPrincipal();
        principal.setVisible(true);
        
    }
    
}
