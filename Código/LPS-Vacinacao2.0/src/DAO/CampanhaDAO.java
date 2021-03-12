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
import model.CampanhaVacina;
import utils.Persistencia;

/**
 *
 * @author Vitor
 */
public class CampanhaDAO implements InterfaceDAO{

    private SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM/yyyy");
    PreparedStatement ps =  null;
    ResultSet resultado = null;
    
    @Override
    public void adicionar(Object obj) {
        try {
            CampanhaVacina campanhaVacina = (CampanhaVacina) obj;
            
            ps = Persistencia.conexao().prepareStatement("INSERT INTO `vacinacao`.`campanha`(nome_campanha,lote,validade_vacina,data_inicial,data_final) values (?, ?, ?, ?, ?)");
            
            ps.setString(1, campanhaVacina.getNome());
            ps.setString(2, String.valueOf(campanhaVacina.getLote()));
            ps.setString(3, formatarData.format(campanhaVacina.getValidade()));
            ps.setString(4, formatarData.format(campanhaVacina.getDataInicial()));
            ps.setString(5, formatarData.format(campanhaVacina.getDataFinal()));
            
            ps.executeUpdate();
        } 
        catch (SQLException ex) {
            Logger.getLogger(CampanhaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void excluir(Object obj) {
        try {
            CampanhaVacina campanhaVacina = (CampanhaVacina) obj;
            
            ps = Persistencia.conexao().prepareStatement("DELETE FROM `vacinacao`.`campanha` WHERE lote = ?");
            
            ps.setString(1, String.valueOf(campanhaVacina.getLote()));
            ps.executeUpdate();
        } 
        catch (SQLException ex) {
            Logger.getLogger(CampanhaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void alterar(Object obj) {
        try {
            CampanhaVacina campanhaVacina = (CampanhaVacina) obj;
            ps = Persistencia.conexao().prepareStatement("UPDATE `vacinacao`.`campanha` SET nome_campanha=?, validade_vacina=?, data_inicial=?, data_final=? WHERE lote = ?");
            
            ps.setString(1, campanhaVacina.getNome());
            ps.setString(2, formatarData.format(campanhaVacina.getValidade()));
            ps.setString(3, formatarData.format(campanhaVacina.getDataInicial()));
            ps.setString(4, formatarData.format(campanhaVacina.getDataFinal()));
            ps.setString(5, String.valueOf(campanhaVacina.getLote()));
            ps.executeUpdate();
        } 
        catch (SQLException ex) {
            Logger.getLogger(CampanhaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ArrayList<Object> listar() {
        CampanhaVacina campanhaVacina;
        ArrayList<Object> listaCampanha = new ArrayList();
        try {
            
            ps = Persistencia.conexao().prepareStatement("SELECT * FROM vacinacao.campanha;");
            resultado = ps.executeQuery();
            
            while(resultado.next()){
                
                campanhaVacina = new CampanhaVacina();
                campanhaVacina.setLote(resultado.getInt("lote"));
                campanhaVacina.setNome(resultado.getString("nome_campanha"));
                campanhaVacina.setValidade(formatarData.parse(resultado.getString("validade_vacina")));
                campanhaVacina.setDataInicial(formatarData.parse(resultado.getString("data_inicial")));
                campanhaVacina.setDataFinal(formatarData.parse(resultado.getString("data_final")));
                
                listaCampanha.add(campanhaVacina);
            }   
        } catch (Exception ex) {
            Logger.getLogger(CampanhaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    return listaCampanha;
    } 
}
