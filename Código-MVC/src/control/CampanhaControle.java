/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import DAO.CampanhaDAO;
import DAO.InterfaceDAO;
import gui.TelaCampanha;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.CampanhaModel;

/**
 *
 * @author Vitor
 */
public class CampanhaControle extends BaseControle{
    
    private TelaCampanha tela;
    private ArrayList<Object> arrayCampanha = new ArrayList();
    private CampanhaModel campanhaVacina;
    private char tipoEdicao;
    private InterfaceDAO campanhaDAO = new CampanhaDAO();
    
    public CampanhaControle(){
        tela = new TelaCampanha(this);
        tela.setVisible(true);
        iniciar();
    }
    
    public void iniciar(){
        camposFormatadoData(tela.getFtxtValidade());
        camposFormatadoData(tela.getFtxtDataInicial());
        camposFormatadoData(tela.getFtxtDataFinal());
        ativaForm(false);
    }
    
    public void limparDados(){
        tela.getEdtLote().setText("");
        tela.getEdtNome().setText("");
        tela.getFtxtValidade().setText("");
        tela.getFtxtDataInicial().setText("");
        tela.getFtxtDataFinal().setText("");
    }
    
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
        if(tela.getFtxtDataInicial().getText().replace(" ", "").length() < 10){
            JOptionPane.showMessageDialog(tela,"Preencha a Data Inicial corretamente (deve conter 8 digitos)");
            tela.getFtxtDataInicial().requestFocus();
            return false;
        }
        if(tela.getFtxtDataFinal().getText().replace(" ", "").length() < 10){
            JOptionPane.showMessageDialog(tela,"Preencha a Data Final corretamente (deve conter 8 digitos)");
            tela.getFtxtDataFinal().requestFocus();
            return false;
        }
      
        return true;
    }
    
    public void carregarCampanha(){
        String [] colunas = {"Lote", "Nome da Campanha", "Validade", "Data Inicial", "Data Final"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        
        arrayCampanha = campanhaDAO.listar();
        for(int i=0; i<arrayCampanha.size(); i++){
            campanhaVacina = (CampanhaModel) arrayCampanha.get(i);

            Object [] linha  = {campanhaVacina.getLote(),campanhaVacina.getNome(),formatarData(campanhaVacina.getValidade()),formatarData(campanhaVacina.getDataInicial()),formatarData(campanhaVacina.getDataFinal())};
            modelo.addRow(linha);
        }    
        tela.getTblCampanhaVacina().setModel(modelo);

    }
    
    public void salvarCampanha() throws SQLException{
        try {
        if(tipoEdicao == 'I'){
        campanhaVacina = new CampanhaModel();
        campanhaVacina.setLote(Integer.parseInt(tela.getEdtLote().getText()));
        }
        campanhaVacina.setNome(tela.getEdtNome().getText());
        campanhaVacina.setValidade(stringToDate(tela.getFtxtValidade().getText()));
        campanhaVacina.setDataInicial(stringToDate(tela.getFtxtDataInicial().getText()));
        campanhaVacina.setDataFinal(stringToDate(tela.getFtxtDataFinal().getText()));
        
        if(tipoEdicao == 'I'){
            campanhaDAO.adicionar(campanhaVacina);
        }else if(tipoEdicao == 'E'){
            campanhaDAO.alterar(campanhaVacina);
            carregarCampanha();
        }
        } catch (Exception ex) {
            Logger.getLogger(CampanhaControle.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    public void excluiCampanha(){
        
        int lote;
        if(tela.getTblCampanhaVacina().getSelectedRow()!= -1){
           lote = Integer.parseInt(tela.getTblCampanhaVacina().getValueAt(tela.getTblCampanhaVacina().getSelectedRow(), 0).toString());
           campanhaVacina = buscarCampanha(lote);
           preencheFormulario();
           ativaForm(false);
           if(JOptionPane.showConfirmDialog(tela,"Deseja excluir o(a:" + campanhaVacina.getNome(),"Sistema de Vacinacao",JOptionPane.YES_NO_OPTION)== 0){
               campanhaDAO.excluir(campanhaVacina);
               arrayCampanha.remove(campanhaVacina);
               carregarCampanha();
           }
        }else
           JOptionPane.showMessageDialog(tela, "Selecione alguma Campanha");


    }
    
    public void alteraCampanha(){
        int lote;
        if(tela.getTblCampanhaVacina().getSelectedRow()!= -1){
           lote = Integer.parseInt(tela.getTblCampanhaVacina().getValueAt(tela.getTblCampanhaVacina().getSelectedRow(), 0).toString());
           campanhaVacina = buscarCampanha(lote);
           preencheFormulario();
        }else
           JOptionPane.showMessageDialog(tela, "Selecione alguma Campanha");
    }
    
    public CampanhaModel buscarCampanha (int lote){
        for (int i =0; i<arrayCampanha.size(); i++){
            campanhaVacina = (CampanhaModel) arrayCampanha.get(i);
           if(campanhaVacina.getLote()== lote){
               return campanhaVacina;
           } 
        }
        return null;
    }
    
    public void preencheFormulario(){
        tela.getEdtLote().setText(String.valueOf(campanhaVacina.getLote()));
        tela.getEdtNome().setText(campanhaVacina.getNome());
        tela.getFtxtValidade().setText(formatarData(campanhaVacina.getValidade()));     
        tela.getFtxtDataInicial().setText(formatarData(campanhaVacina.getDataInicial())); 
        tela.getFtxtDataFinal().setText(formatarData(campanhaVacina.getDataFinal())); 
    }
    
    public void ativaForm(boolean ativaDesativa){
        for(int i=0; i<tela.getPnlForm().getComponents().length; i++){
            tela.getPnlForm().getComponent(i).setEnabled(ativaDesativa);
        }
        
    }
    
    public void salvar(){
        try {
            if(validacaoCampos()){
               salvarCampanha();
               limparDados();
               ativaForm(false);
               JOptionPane.showMessageDialog(tela, "Dados salvos com sucesso");
            }
        } catch (SQLException ex) {
                Logger.getLogger(CampanhaControle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void editar(){
        tipoEdicao = 'E';
        ativaForm(true);
        limparDados();
        alteraCampanha();
    }
    
    public void adicionar(){
        tipoEdicao = 'I';
        ativaForm(true);
        limparDados();
    }
    
}
