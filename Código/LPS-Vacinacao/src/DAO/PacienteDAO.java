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
import model.Paciente;
import utils.Persistencia;

/**
 *
 * @author Vitor
 */
public class PacienteDAO implements InterfaceDAO {
    
    private SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM/yyyy");
    PreparedStatement ps =  null;
    ResultSet resultado = null;

    @Override
    public void adicionar(Object obj){
        
        try {
            Paciente paciente = (Paciente) obj;
            ps = Persistencia.conexao().prepareStatement("INSERT INTO `vacinacao`.`pessoa`(nome,data_nascimento,cpf,sexo,codigo_paciente) values (?, ?, ?, ?, ?)");
            

            ps.setString(1, paciente.getNome());
            ps.setString(2, formatarData.format(paciente.getDataNasc()));
            ps.setString(3, paciente.getCpf());
            ps.setString(4, String.valueOf(paciente.getSexo()));
            ps.setString(5, String.valueOf(paciente.getCodigo()));
            
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(PacienteDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void excluir(Object obj) {
        
        try {
            Paciente paciente = (Paciente) obj;
            ps = Persistencia.conexao().prepareStatement("DELETE FROM `vacinacao`.`pessoa` WHERE codigo_paciente = ?");
            ps.setString(1, String.valueOf(paciente.getCodigo()));
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(PacienteDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void alterar(Object obj) {
        
        try {
            Paciente paciente = (Paciente) obj;
            ps = Persistencia.conexao().prepareStatement("UPDATE `vacinacao`.`pessoa` SET nome=?,data_nascimento=?,cpf=?,sexo=? WHERE codigo_paciente = ?");
            
            
            ps.setString(1, paciente.getNome());
            ps.setString(2, formatarData.format(paciente.getDataNasc()));
            ps.setString(3, paciente.getCpf());
            ps.setString(4, String.valueOf(paciente.getSexo()));
            ps.setString(5, String.valueOf(paciente.getCodigo()));
            
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(PacienteDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ArrayList<Object> listar() {
        Paciente paciente;
        
        ArrayList<Object> listaPaciente = new ArrayList();
        try {
            
            ps = Persistencia.conexao().prepareStatement("SELECT * FROM vacinacao.pessoa WHERE codigo_paciente is not null;");
            resultado = ps.executeQuery();
            
            while (resultado.next()){
                paciente = new Paciente();
                paciente.setCodigo(resultado.getInt("codigo_paciente"));
                paciente.setCpf(resultado.getString("cpf"));
                paciente.setDataNasc(formatarData.parse(resultado.getString("data_nascimento")));
                paciente.setNome(resultado.getString("nome"));
                paciente.setSexo(resultado.getString("sexo").charAt(0));
                listaPaciente.add(paciente);
            }  
        } catch (Exception ex) {
            Logger.getLogger(PacienteDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaPaciente;
    
    }

}
