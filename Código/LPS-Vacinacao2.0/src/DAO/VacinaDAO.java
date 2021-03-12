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
import model.Vacina;
import utils.Persistencia;

/**
 *
 * @author Vitor
 */
public class VacinaDAO implements InterfaceDAO {
    
    private SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM/yyyy");
    PreparedStatement ps =  null;
    ResultSet resultado = null;
    
    @Override
    public void adicionar(Object obj) {
        
        try {
            Vacina vacina = (Vacina) obj;
            ps = Persistencia.conexao().prepareStatement("INSERT INTO `vacinacao`.`vacina`(nome_vacina,validade,lote) values (?, ?, ?)");
            
            ps.setString(1, vacina.getNome());
            ps.setString(2, formatarData.format(vacina.getValidade()));
            ps.setString(3, String.valueOf(vacina.getLote()));
            
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(VacinaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void excluir(Object obj) {
        try {
            Vacina vacina = (Vacina) obj;
            ps = Persistencia.conexao().prepareStatement("DELETE FROM `vacinacao`.`vacina` WHERE lote = ?");
            
            ps.setString(1, String.valueOf(vacina.getLote()));
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(VacinaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void alterar(Object obj) {
        try {
            Vacina vacina = (Vacina) obj;
            ps = Persistencia.conexao().prepareStatement("UPDATE `vacinacao`.`vacina` SET nome_vacina=?,validade=? WHERE lote = ?");
            
            ps.setString(1, vacina.getNome());
            ps.setString(2, formatarData.format(vacina.getValidade()));
            ps.setString(3, String.valueOf(vacina.getLote()));
            
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(VacinaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ArrayList<Object> listar() {
        Vacina vacina;
        ArrayList<Object> listaVacina = new ArrayList();
        try {
            ps = Persistencia.conexao().prepareStatement("SELECT * FROM vacinacao.vacina;");
            resultado = ps.executeQuery();
            
            while (resultado.next()){
                vacina = new Vacina();
                vacina.setLote(resultado.getInt("lote"));
                vacina.setNome(resultado.getString("nome_vacina"));
                vacina.setValidade(formatarData.parse(resultado.getString("validade")));
                listaVacina.add(vacina);
            }   
        } catch (Exception ex) {
            Logger.getLogger(VacinaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    return listaVacina;
    }

}    

