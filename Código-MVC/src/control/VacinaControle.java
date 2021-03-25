/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import DAO.InterfaceDAO;
import DAO.VacinaDAO;
import gui.TelaVacina;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.VacinaModel;

/**
 *
 * @author Vitor
 */
public class VacinaControle extends BaseControle{
    
    private TelaVacina tela;
    private ArrayList<Object> arrayVacina = new ArrayList();
    private VacinaModel vacina;
    private char tipoEdicao;
    private InterfaceDAO vacinaDAO = new VacinaDAO();
    
    public VacinaControle(){
        tela = new TelaVacina(this);
        tela.setVisible(true);

        iniciar();
    }
    
    public void iniciar(){
        camposFormatadoData(tela.getFtxtValidade());
        ativaForm(false);
    } //Concluido
    
    public void excluiVacina(){

        int lote;
        if(tela.getTblVacina().getSelectedRow()!= -1){
               lote = Integer.parseInt(tela.getTblVacina().getValueAt(tela.getTblVacina().getSelectedRow(), 0).toString());
               vacina = buscarVacina(lote);
               preencheFormulario();
                ativaForm(false);
                if(JOptionPane.showConfirmDialog(tela,"Deseja excluir o(a:" + vacina.getNome(),"Sistema de Vacinacao",JOptionPane.YES_NO_OPTION)== 0){

                    vacinaDAO.excluir(vacina);
                    arrayVacina.remove(vacina);
                    carregarVacina();
            }
        }else
            JOptionPane.showMessageDialog(tela, "Selecione alguma Vacina");

    } //Concluido
    
    public void alteraVacina(){
        int lote;
        if(tela.getTblVacina().getSelectedRow()!= -1){
           lote = Integer.parseInt(tela.getTblVacina().getValueAt(tela.getTblVacina().getSelectedRow(), 0).toString());
           vacina = buscarVacina(lote);
           preencheFormulario();
        }else
            JOptionPane.showMessageDialog(tela, "Selecione alguma Vacina");
    } //Concluido
    
    public VacinaModel buscarVacina (int lote){
        for (int i =0; i<arrayVacina.size(); i++){
            vacina = (VacinaModel) arrayVacina.get(i);
           if(vacina.getLote()== lote){
               return vacina;
           } 
        }
        return null;
    } //Concluido
    
    public void preencheFormulario(){
        tela.getEdtLote().setText(String.valueOf(vacina.getLote()));
        tela.getEdtNome().setText(vacina.getNome());
        tela.getFtxtValidade().setText(formatarData(vacina.getValidade()));     
        
    } //Concluido
    
    public void salvarVacina() throws SQLException{
        try {
            if(tipoEdicao == 'I'){
            vacina = new VacinaModel();
            vacina.setLote(Integer.parseInt(tela.getEdtLote().getText()));
            }
            vacina.setNome(tela.getEdtNome().getText());
            vacina.setValidade(stringToDate(tela.getFtxtValidade().getText()));

            if(tipoEdicao == 'I'){
                vacinaDAO.adicionar(vacina);
            }else if(tipoEdicao == 'E'){
                vacinaDAO.alterar(vacina);
                carregarVacina();
            }
            } catch (Exception ex) {
                Logger.getLogger(VacinaControle.class.getName()).log(Level.SEVERE, null, ex);
        }
    } //Concluido
    
    public void carregarVacina(){
        String [] colunas = {"Lote", "Nome", "Validade"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        
                
        arrayVacina = vacinaDAO.listar();
        for(int i=0; i<arrayVacina.size(); i++){
            vacina = (VacinaModel) arrayVacina.get(i);
            Object [] linha  = {vacina.getLote(),vacina.getNome(),formatarData(vacina.getValidade())};
            modelo.addRow(linha);
        }
        
        tela.getTblVacina().setModel(modelo);

    } //Concluido
    
    public void limparDados(){
        tela.getEdtLote().setText("");
        tela.getEdtNome().setText("");
        tela.getFtxtValidade().setText("");
    } //Concluido
   
    public boolean validacaoCampos(){
        
        if(tela.getEdtLote().getText().replace(" ", "").matches("[0-9],{1,}")){
            JOptionPane.showMessageDialog(tela,"Preencha o Lote corretamente (somente numeros)");
            tela.getEdtLote().requestFocus();
            return false;
        }
        if(tela.getEdtNome().getText().replace(" ", "").matches("[A-Za-z],{2,}")){
            JOptionPane.showMessageDialog(tela,"Preencha o Nome corretamente (somente as letras sem acentos)");
            tela.getEdtNome().requestFocus();
            return false;
        }
        if(tela.getFtxtValidade().getText().replace(" ", "").length() < 10){
            JOptionPane.showMessageDialog(tela,"Preencha a Data de Validade corretamente (deve conter 8 digitos)");
            tela.getFtxtValidade().requestFocus();
            return false;
        }
      
        return true;
    } //Concluido
   
    public void ativaForm(boolean ativaDesativa){
        
        for(int i=0; i<tela.getPnlForm().getComponents().length; i++){
            tela.getPnlForm().getComponent(i).setEnabled(ativaDesativa);
        }
        
    }
    
    public void salvar(){
        try {
            if(validacaoCampos()){
                salvarVacina();
                limparDados();
                ativaForm(false);
                JOptionPane.showMessageDialog(tela, "Dados salvos com sucesso");
            }
        } catch (SQLException ex) {
                Logger.getLogger(VacinaControle.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public void editar(){
        tipoEdicao = 'E';
        ativaForm(true);
        limparDados();
        alteraVacina(); 
    }
    
    public void adicionar(){
        tipoEdicao = 'I';
        ativaForm(true);
        limparDados();
    }
}
