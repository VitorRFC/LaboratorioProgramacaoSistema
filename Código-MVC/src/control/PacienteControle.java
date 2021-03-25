/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import DAO.InterfaceDAO;
import DAO.PacienteDAO;
import gui.TelaPaciente;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.PacienteModel;

/**
 *
 * @author Vitor
 */
public class PacienteControle extends BaseControle{
    
    
    private TelaPaciente tela;
    private PacienteModel paciente;
    private InterfaceDAO pacienteDAO = new PacienteDAO();
    private ArrayList<Object> arrayPaciente = new ArrayList();
    private char tipoEdicao;
    
    public PacienteControle(){
        tela = new TelaPaciente(this);
        tela.setVisible(true);
        iniciar();
    }
    
    public void iniciar(){
        camposFormatadoData(tela.getFtxtDataNascimento());
        camposFormatadoCPF(tela.getFtxtCPF());
        ativaForm(false);
    }
    
    public void limparDados(){
        tela.getEdtCodigo().setText("");
        tela.getEdtNome().setText("");
        tela.getFtxtDataNascimento().setText("");
        tela.getFtxtCPF().setText("");
        tela.getCbxSexo().setSelectedIndex(0);
    }
    
    public boolean validacaoCampos(){
        
        if(tela.getEdtCodigo().getText().replace(" ", "").matches("[0-9],{1,}")){
            JOptionPane.showMessageDialog(tela,"Preencha o Codigo corretamente (somente numeros)");
            tela.getEdtCodigo().requestFocus();
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
    
    public void carregarPaciente(){
        String [] colunas = {"Codigo", "Nome", "Data Nascimento", "CPF", "Sexo"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
  
        arrayPaciente = pacienteDAO.listar();
        for(int i=0; i<arrayPaciente.size(); i++){
            paciente = (PacienteModel) arrayPaciente.get(i);
            Object [] linha  = {paciente.getCodigo(),paciente.getNome(),formatarData(paciente.getDataNasc()),paciente.getCpf(),paciente.getSexo()};
            modelo.addRow(linha);
        }
        
        tela.getTblPaciente().setModel(modelo);
        
    }
    
    public void salvarPaciente() throws SQLException{
        try {
            if(tipoEdicao == 'I'){    
                paciente = new PacienteModel();
                paciente.setCodigo(Integer.parseInt(tela.getEdtCodigo().getText()));
            }

            paciente.setNome(tela.getEdtNome().getText());
            paciente.setDataNasc(stringToDate(tela.getFtxtDataNascimento().getText()));
            paciente.setCpf(tela.getFtxtCPF().getText());
            paciente.setSexo(tela.getCbxSexo().getItemAt(tela.getCbxSexo().getSelectedIndex()).toCharArray()[0]);

            if(tipoEdicao == 'I'){

                pacienteDAO.adicionar(paciente);

            } else if(tipoEdicao == 'E'){

                pacienteDAO.alterar(paciente);
                carregarPaciente();
            }
            
        } catch (Exception ex) {
            Logger.getLogger(PacienteControle.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    public void excluiPaciente() {
        try {
            int codigo;
            if(tela.getTblPaciente().getSelectedRow()!= -1){
               codigo = Integer.parseInt(tela.getTblPaciente().getValueAt(tela.getTblPaciente().getSelectedRow(), 0).toString());
               paciente = buscarPaciente(codigo);
               preencheFormulario();
                ativaForm(false);
                if(JOptionPane.showConfirmDialog(tela,"Deseja excluir o(a):" + paciente.getNome(),"Sistema de Vacinacao",JOptionPane.YES_NO_OPTION)== 0){
                    pacienteDAO.excluir(paciente);
                    carregarPaciente();

                }
            }else
               JOptionPane.showMessageDialog(tela, "Selecione algum paciente");
        } catch (Exception ex) {
            Logger.getLogger(PacienteControle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void alteraPaciente(){
        int codigo;
        if(tela.getTblPaciente().getSelectedRow()!= -1){
           codigo = Integer.parseInt(tela.getTblPaciente().getValueAt(tela.getTblPaciente().getSelectedRow(), 0).toString());
           paciente = buscarPaciente(codigo);
           preencheFormulario();
        }else
           JOptionPane.showMessageDialog(tela, "Selecione algum paciente");
    }
    
    public PacienteModel buscarPaciente (int codigo){
        for (int i =0; i<arrayPaciente.size(); i++){
           paciente = (PacienteModel) arrayPaciente.get(i); 
           if(paciente.getCodigo()== codigo){
               return paciente;
           } 
        }
        return null;
    }
    
    public void preencheFormulario(){
        tela.getEdtCodigo().setText(String.valueOf(paciente.getCodigo()));
        tela.getEdtNome().setText(paciente.getNome());
        tela.getFtxtDataNascimento().setText(formatarData(paciente.getDataNasc()));
        tela.getFtxtCPF().setText(paciente.getCpf());
        if(paciente.getSexo()== 'M')
            tela.getCbxSexo().setSelectedIndex(0);
        else
            tela.getCbxSexo().setSelectedIndex(1);
        
        
    }
    
    public void ativaForm(boolean ativaDesativa){
        
        for(int i=0; i<tela.getPnlForm().getComponents().length; i++){
            tela.getPnlForm().getComponent(i).setEnabled(ativaDesativa);
        }
        
    }

    public void salvar() {
        try {
            if (validacaoCampos()) {

                salvarPaciente();

                limparDados();
                ativaForm(false);
                JOptionPane.showMessageDialog(tela, "Dados salvos com sucesso");

            }
        } catch (SQLException ex) {
            Logger.getLogger(PacienteControle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void editar(){
        tipoEdicao = 'E';
        ativaForm(true);
        limparDados();
        alteraPaciente();
    }
    
    public void adicionar(){
        tipoEdicao = 'I';
        ativaForm(true);
        limparDados();
    }
    
}
