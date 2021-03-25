/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.EnfermeiroModel;
import utils.Persistencia;

/**
 *
 * @author Vitor
 */
public class EnfermeiroDAO implements InterfaceDAO {

    private SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM/yyyy");
    PreparedStatement ps =  null;
    ResultSet resultado = null;
    
    @Override
    public void adicionar(Object obj) {
        
        try {
            EnfermeiroModel enfermeiro = (EnfermeiroModel) obj;
            ps = Persistencia.conexao().prepareStatement("INSERT INTO `vacinacao`.`pessoa`(nome,data_nascimento,cpf,sexo,matricula_enfermeiro) values (?, ?, ?, ?, ?)");
            
            
            ps.setString(1, enfermeiro.getNome());
            ps.setString(2, formatarData.format(enfermeiro.getDataNasc()));
            ps.setString(3, enfermeiro.getCpf());
            ps.setString(4, String.valueOf(enfermeiro.getSexo()));
            ps.setString(5, String.valueOf(enfermeiro.getMatricula()));
            
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EnfermeiroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void excluir(Object obj) {
        
        try {
            EnfermeiroModel enfermeiro = (EnfermeiroModel) obj;
            ps = Persistencia.conexao().prepareStatement("DELETE FROM `vacinacao`.`pessoa` WHERE matricula_enfermeiro = ?");
            ps.setString(1, String.valueOf(enfermeiro.getMatricula()));
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EnfermeiroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void alterar(Object obj) {
        
        try {
            EnfermeiroModel enfermeiro = (EnfermeiroModel) obj;
            ps = Persistencia.conexao().prepareStatement("UPDATE `vacinacao`.`pessoa` SET nome=?,data_nascimento=?,cpf=?,sexo=? WHERE matricula_enfermeiro = ?");
            
            ps.setString(1, enfermeiro.getNome());
            ps.setString(2, formatarData.format(enfermeiro.getDataNasc()));
            ps.setString(3, enfermeiro.getCpf());
            ps.setString(4, String.valueOf(enfermeiro.getSexo()));
            ps.setString(5, String.valueOf(enfermeiro.getMatricula()));
            
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EnfermeiroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ArrayList<Object> listar() {
        
        EnfermeiroModel enfermeiro;
        ArrayList<Object> listaEnfermeiro = new ArrayList();
        
        try {
            ps = Persistencia.conexao().prepareStatement("SELECT * FROM vacinacao.pessoa WHERE matricula_enfermeiro is not null;");
            resultado = ps.executeQuery();
            
            while (resultado.next()){
                enfermeiro = new EnfermeiroModel();
                enfermeiro.setMatricula(resultado.getInt("matricula_enfermeiro"));
                enfermeiro.setCpf(resultado.getString("cpf"));
                enfermeiro.setDataNasc(formatarData.parse(resultado.getString("data_nascimento")));
                enfermeiro.setNome(resultado.getString("nome"));
                enfermeiro.setSexo(resultado.getString("sexo").charAt(0));
                listaEnfermeiro.add(enfermeiro);
            }   
        } catch (Exception ex) {
            Logger.getLogger(EnfermeiroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return listaEnfermeiro;
    } 
}
