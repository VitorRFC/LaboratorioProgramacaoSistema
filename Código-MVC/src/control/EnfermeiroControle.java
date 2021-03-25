/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import DAO.EnfermeiroDAO;
import DAO.InterfaceDAO;
import gui.TelaEnfermeiro;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.EnfermeiroModel;


/**
 *
 * @author Vitor
 */
public class EnfermeiroControle extends BaseControle {
    
    private TelaEnfermeiro tela;
    private EnfermeiroModel enfermeiro;
    private InterfaceDAO enfermeiroDAO = new EnfermeiroDAO();
    private ArrayList<Object> arrayEnfermeiro = new ArrayList();
    private char tipoEdicao;
    
    
    public EnfermeiroControle(){
        tela = new TelaEnfermeiro(this);
        tela.setVisible(true);
        iniciar();
    }
    
    public void iniciar(){
        camposFormatadoData(tela.getFtxtDataNascimento());
        camposFormatadoCPF(tela.getFtxtCPF());
        ativaForm(false);
    }
    
    public void limparDados(){
        tela.getEdtMatricula().setText("");
        tela.getEdtNome().setText("");
        tela.getFtxtDataNascimento().setText("");
        tela.getFtxtCPF().setText("");
        tela.getCbxSexo().setSelectedIndex(0);
    }
    
    public boolean validacaoCampos(){
        
        if(tela.getEdtMatricula().getText().replace(" ", "").matches("[0-9],{1,}")){
            JOptionPane.showMessageDialog(tela,"Preencha o Codigo corretamente (somente numeros)");
            tela.getEdtMatricula().requestFocus();
            return false;
        }
        if(tela.getEdtNome().getText().replace(" ", "").matches("[A-Za-z],{3,}")){
            JOptionPane.showMessageDialog(tela,"Preencha o Nome corretamente (somente as letras sem acentos)");
            tela.getEdtNome().requestFocus();
            return false;
        }
        if(tela.getFtxtDataNascimento().getText().replace(" ", "").length() < 10){
            JOptionPane.showMessageDialog(tela,"Preencha a Data de Nascimento corretamente (deve conter 8 digitos)");
            tela.getFtxtDataNascimento().requestFocus();
            return false;
        }
        if(tela.getFtxtCPF().getText().replace(" ", "").length() < 13){
            JOptionPane.showMessageDialog(tela,"Preencha o CPF corretamente (deve conter 11 digitos)");
            tela.getFtxtCPF().requestFocus();
            return false;
        } else
            if(!verificaCPF(tela.getFtxtCPF().getText())){
            JOptionPane.showMessageDialog(tela, "CPF Inválido");
            return false;
            }
       
        return true;
    }
    
    public void carregarEnfermeiro() {
        String [] colunas = {"Matricula", "Nome", "Data Nascimento", "CPF", "Sexo"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);

        arrayEnfermeiro = enfermeiroDAO.listar();
        for(int i=0; i<arrayEnfermeiro.size(); i++){
            enfermeiro = (EnfermeiroModel) arrayEnfermeiro.get(i);
            Object [] linha  = {enfermeiro.getMatricula(),enfermeiro.getNome(),formatarData(enfermeiro.getDataNasc()),enfermeiro.getCpf(),enfermeiro.getSexo()};
            modelo.addRow(linha);
        }
        tela.getTblEnfermeiro().setModel(modelo);
        
    }
    
    public void salvarEnfermeiro()throws SQLException{
        try {
            if(tipoEdicao == 'I'){    
                enfermeiro = new EnfermeiroModel();
                enfermeiro.setMatricula(Integer.parseInt(tela.getEdtMatricula().getText()));
            }
            enfermeiro.setNome(tela.getEdtNome().getText());
            enfermeiro.setDataNasc(stringToDate(tela.getFtxtDataNascimento().getText()));
            enfermeiro.setCpf(tela.getFtxtCPF().getText());
            enfermeiro.setSexo(tela.getCbxSexo().getItemAt(tela.getCbxSexo().getSelectedIndex()).toCharArray()[0]);

            if(tipoEdicao == 'I'){
                enfermeiroDAO.adicionar(enfermeiro);
            }else if(tipoEdicao == 'E'){
                enfermeiroDAO.alterar(enfermeiro);
                carregarEnfermeiro();
            }

        } catch (Exception ex) {
            Logger.getLogger(EnfermeiroControle.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    public void excluiEnfermeiro(){
        int matricula;
        if(tela.getTblEnfermeiro().getSelectedRow()!= -1){
           matricula = Integer.parseInt(tela.getTblEnfermeiro().getValueAt(tela.getTblEnfermeiro().getSelectedRow(), 0).toString());
           enfermeiro = buscarEnfermeiro(matricula);
           preencheFormulario();
           ativaForm(false);
           if(JOptionPane.showConfirmDialog(tela,"Deseja excluir o(a):" + enfermeiro.getNome(),"Sistema de Vacinacao",JOptionPane.YES_NO_OPTION)== 0){
               enfermeiroDAO.excluir(enfermeiro);
               arrayEnfermeiro.remove(enfermeiro);
               carregarEnfermeiro();
            }
        }else
            JOptionPane.showMessageDialog(tela, "Selecione algum(a) Enfermeiro(a)");
    }
        
    public void alteraEnfermeiro(){
        int matricula;
        if(tela.getTblEnfermeiro().getSelectedRow()!= -1){
           matricula = Integer.parseInt(tela.getTblEnfermeiro().getValueAt(tela.getTblEnfermeiro().getSelectedRow(), 0).toString());
           enfermeiro = buscarEnfermeiro(matricula);
           preencheFormulario();
        }else
            JOptionPane.showMessageDialog(tela, "Selecione algum(a) Enfermeiro(a)");
    }
    
    public EnfermeiroModel buscarEnfermeiro (int matricula){
        for (int i =0; i<arrayEnfermeiro.size(); i++){
           enfermeiro = (EnfermeiroModel) arrayEnfermeiro.get(i); 
           if(enfermeiro.getMatricula()== matricula){
               return enfermeiro;
           } 
        }
        return null;
    }
    
    public void preencheFormulario(){
        tela.getEdtMatricula().setText(String.valueOf(enfermeiro.getMatricula()));
        tela.getEdtNome().setText(enfermeiro.getNome());
        tela.getFtxtDataNascimento().setText(formatarData(enfermeiro.getDataNasc()));
        tela.getFtxtCPF().setText(enfermeiro.getCpf());
        if(enfermeiro.getSexo()== 'M')
            tela.getCbxSexo().setSelectedIndex(0);
        else
            tela.getCbxSexo().setSelectedIndex(1);
        
        
    }
    
    public void ativaForm(boolean ativaDesativa){
    for(int i=0; i<tela.getPnlFormEnf().getComponents().length; i++){
            tela.getPnlFormEnf().getComponent(i).setEnabled(ativaDesativa);
        }
    }
    
    public void salvar(){
        try {
            if(validacaoCampos()){
                salvarEnfermeiro();
                limparDados();
                ativaForm(false);
                JOptionPane.showMessageDialog(tela, "Dados salvos com sucesso");
            }
        } catch (SQLException ex) {
                Logger.getLogger(EnfermeiroControle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void editar(){
        tipoEdicao = 'E';
        ativaForm(true);
        limparDados();
        alteraEnfermeiro();
    }
    
    public void adicionar(){
        tipoEdicao = 'I';
        ativaForm(true);
        limparDados();
    }
}
