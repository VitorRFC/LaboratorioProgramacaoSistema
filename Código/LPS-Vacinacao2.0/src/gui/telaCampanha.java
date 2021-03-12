/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import DAO.CampanhaDAO;
import DAO.InterfaceDAO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import model.CampanhaVacina;
import utils.Persistencia;

/**
 *
 * @author Vitor
 */
public class telaCampanha extends javax.swing.JFrame {

    private SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM/yyyy");
    private ArrayList<Object> arrayCampanha = new ArrayList();
    CampanhaVacina campanhaVacina;
    char tipoEdicao;
    
    InterfaceDAO campanhaDAO = new CampanhaDAO();
    public telaCampanha() {
        initComponents();
        camposFormatado();
        ativaForm(false);
        //carregarCampanha();
    }
    
    public void excluiCampanha() throws SQLException{
        try {
            int lote;
            if(tblCampanhaVacina.getSelectedRow()!= -1){
               lote = Integer.parseInt(tblCampanhaVacina.getValueAt(tblCampanhaVacina.getSelectedRow(), 0).toString());
               campanhaVacina = buscarCampanha(lote);
               preencheFormulario();
                ativaForm(false);
                if(JOptionPane.showConfirmDialog(rootPane,"Deseja excluir o(a:" + campanhaVacina.getNome(),"Sistema de Vacinacao",JOptionPane.YES_NO_OPTION)== 0){
                    campanhaDAO.excluir(campanhaVacina);
                    arrayCampanha.remove(campanhaVacina);
                    carregarCampanha();
                }
            }else
               JOptionPane.showMessageDialog(rootPane, "Selecione alguma Campanha");
        } catch (ParseException ex) {
                   Logger.getLogger(telaCampanha.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public void alteraCampanha(){
        int lote;
        if(tblCampanhaVacina.getSelectedRow()!= -1){
           lote = Integer.parseInt(tblCampanhaVacina.getValueAt(tblCampanhaVacina.getSelectedRow(), 0).toString());
           campanhaVacina = buscarCampanha(lote);
           preencheFormulario();
        }else
           JOptionPane.showMessageDialog(rootPane, "Selecione alguma Campanha");
    }
    
    public CampanhaVacina buscarCampanha (int lote){
        for (int i =0; i<arrayCampanha.size(); i++){
            campanhaVacina = (CampanhaVacina) arrayCampanha.get(i);
           if(campanhaVacina.getLote()== lote){
               return campanhaVacina;
           } 
        }
        return null;
    }
    public void preencheFormulario(){
        edtLote.setText(String.valueOf(campanhaVacina.getLote()));
        edtNome.setText(campanhaVacina.getNome());
        ftxtValidade.setText(formatarData.format(campanhaVacina.getValidade()));     
        ftxtDataInicial.setText(formatarData.format(campanhaVacina.getDataInicial())); 
        ftxtDataFinal.setText(formatarData.format(campanhaVacina.getDataFinal())); 
    }
    
    public void salvarCampanha() throws SQLException{
        try {
        if(tipoEdicao == 'I'){
        campanhaVacina = new CampanhaVacina();
        campanhaVacina.setLote(Integer.parseInt(edtLote.getText()));
        }
        campanhaVacina.setNome(edtNome.getText());
        campanhaVacina.setValidade(formatarData.parse(ftxtValidade.getText()));
        campanhaVacina.setDataInicial(formatarData.parse(ftxtDataInicial.getText()));
        campanhaVacina.setDataFinal(formatarData.parse(ftxtDataFinal.getText()));
        
        if(tipoEdicao == 'I'){
            campanhaDAO.adicionar(campanhaVacina);
        }else if(tipoEdicao == 'E'){
            campanhaDAO.alterar(campanhaVacina);
            carregarCampanha();
        }
        } catch (ParseException ex) {
            Logger.getLogger(telaPaciente.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    public void carregarCampanha() throws ParseException{
        String [] colunas = {"Lote", "Nome da Campanha", "Validade", "Data Inicial", "Data Final"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        
        arrayCampanha = campanhaDAO.listar();
        for(int i=0; i<arrayCampanha.size(); i++){
            campanhaVacina = (CampanhaVacina) arrayCampanha.get(i);

            Object [] linha  = {campanhaVacina.getLote(),campanhaVacina.getNome(),formatarData.format(campanhaVacina.getValidade()),formatarData.format(campanhaVacina.getDataInicial()),formatarData.format(campanhaVacina.getDataFinal())};
            modelo.addRow(linha);
        }    
        tblCampanhaVacina.setModel(modelo);

    }
    
    public void limparDados(){
        edtLote.setText("");
        edtNome.setText("");
        ftxtValidade.setText("");
        ftxtDataInicial.setText("");
        ftxtDataFinal.setText("");
    }
 
    public void camposFormatado(){
        try {
            MaskFormatter maskValidade = new MaskFormatter("##/##/####");
            MaskFormatter maskDataInicial = new MaskFormatter("##/##/####"); 
            MaskFormatter maskDataFinal = new MaskFormatter("##/##/####"); 
            maskValidade.install(ftxtValidade);
            maskDataInicial.install(ftxtDataInicial);
            maskDataFinal.install(ftxtDataFinal);
            
        } catch (ParseException ex) {
            Logger.getLogger(telaPaciente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
         
    
    public boolean validacaoCampos(){
        
        if(edtLote.getText().replace(" ", "").matches("[0-9],{1,}")){
            JOptionPane.showMessageDialog(rootPane,"Preencha o Lote corretamente (somente numeros)");
            edtLote.requestFocus();
            return false;
        }
        if(edtNome.getText().replace(" ", "").matches("[A-Za-z],{2,}")){
            JOptionPane.showMessageDialog(rootPane,"Preencha o Nome corretamente (somente as letras sem acentos)");
            edtNome.requestFocus();
            return false;
        }
        if(ftxtValidade.getText().replace(" ", "").length() < 10){
            JOptionPane.showMessageDialog(rootPane,"Preencha a Data de Validade corretamente (deve conter 8 digitos)");
            ftxtValidade.requestFocus();
            return false;
        }
        if(ftxtDataInicial.getText().replace(" ", "").length() < 10){
            JOptionPane.showMessageDialog(rootPane,"Preencha a Data Inicial corretamente (deve conter 8 digitos)");
            ftxtDataInicial.requestFocus();
            return false;
        }
        if(ftxtDataFinal.getText().replace(" ", "").length() < 10){
            JOptionPane.showMessageDialog(rootPane,"Preencha a Data Final corretamente (deve conter 8 digitos)");
            ftxtDataFinal.requestFocus();
            return false;
        }
      
        return true;
    }

     public void ativaForm(boolean ativaDesativa){
        
        for(int i=0; i<pnlForm.getComponents().length; i++){
            pnlForm.getComponent(i).setEnabled(ativaDesativa);
        }
        
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnAdicionar = new javax.swing.JToggleButton();
        btnEditar = new javax.swing.JToggleButton();
        btnCancelar = new javax.swing.JToggleButton();
        btnExcluir = new javax.swing.JToggleButton();
        btnConsultar = new javax.swing.JToggleButton();
        pnlForm = new javax.swing.JPanel();
        lblLote = new javax.swing.JLabel();
        edtLote = new javax.swing.JTextField();
        lblNome = new javax.swing.JLabel();
        edtNome = new javax.swing.JTextField();
        lblValidade = new javax.swing.JLabel();
        ftxtValidade = new javax.swing.JFormattedTextField();
        lblDataInicial = new javax.swing.JLabel();
        lblDataFinal = new javax.swing.JLabel();
        ftxtDataFinal = new javax.swing.JFormattedTextField();
        ftxtDataInicial = new javax.swing.JFormattedTextField();
        btnSalvar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCampanhaVacina = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Consolas", 1, 24)); // NOI18N
        jLabel1.setText("           Cadastro Campanha de Vacinação");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                .addGap(119, 119, 119))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icone/add.png"))); // NOI18N
        btnAdicionar.setText("Adicionar");
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        btnEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icone/edit.png"))); // NOI18N
        btnEditar.setText("Editar");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icone/cancel.png"))); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icone/e.png"))); // NOI18N
        btnExcluir.setText("Excluir");
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        btnConsultar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icone/consultar.png"))); // NOI18N
        btnConsultar.setText("Consultar");
        btnConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnConsultar)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        pnlForm.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblLote.setText("Lote:");

        lblNome.setText("Nome da Vacina:");

        lblValidade.setText("Validade:");

        lblDataInicial.setText("Data Inicial:");

        lblDataFinal.setText("Data Final:");

        btnSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icone/salvar.png"))); // NOI18N
        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFormLayout = new javax.swing.GroupLayout(pnlForm);
        pnlForm.setLayout(pnlFormLayout);
        pnlFormLayout.setHorizontalGroup(
            pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblLote)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edtLote, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFormLayout.createSequentialGroup()
                        .addComponent(lblNome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblValidade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxtValidade, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 14, Short.MAX_VALUE))
                    .addGroup(pnlFormLayout.createSequentialGroup()
                        .addComponent(lblDataInicial)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(lblDataFinal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        pnlFormLayout.setVerticalGroup(
            pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLote)
                    .addComponent(edtLote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNome)
                    .addComponent(edtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblValidade)
                    .addComponent(ftxtValidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFormLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblDataInicial)
                            .addComponent(lblDataFinal)
                            .addComponent(ftxtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ftxtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnSalvar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tblCampanhaVacina.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblCampanhaVacina);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlForm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        tipoEdicao = 'I';
        ativaForm(true);
        limparDados();
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        ativaForm(false);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        try {
        if(validacaoCampos()){
           salvarCampanha();
           limparDados();
           ativaForm(false);
           JOptionPane.showMessageDialog(rootPane, "Dados salvos com sucesso");
        }
        } catch (SQLException ex) {
                Logger.getLogger(telaCampanha.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        try {
            carregarCampanha();
        } catch (ParseException ex) {
            Logger.getLogger(telaCampanha.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnConsultarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        tipoEdicao = 'E';
        ativaForm(true);
        limparDados();
        alteraCampanha();         
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        try {
            excluiCampanha();
        } catch (SQLException ex) {
            Logger.getLogger(telaCampanha.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnExcluirActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(telaCampanha.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(telaCampanha.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(telaCampanha.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(telaCampanha.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new telaCampanha().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnAdicionar;
    private javax.swing.JToggleButton btnCancelar;
    private javax.swing.JToggleButton btnConsultar;
    private javax.swing.JToggleButton btnEditar;
    private javax.swing.JToggleButton btnExcluir;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JTextField edtLote;
    private javax.swing.JTextField edtNome;
    private javax.swing.JFormattedTextField ftxtDataFinal;
    private javax.swing.JFormattedTextField ftxtDataInicial;
    private javax.swing.JFormattedTextField ftxtValidade;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDataFinal;
    private javax.swing.JLabel lblDataInicial;
    private javax.swing.JLabel lblLote;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblValidade;
    private javax.swing.JPanel pnlForm;
    private javax.swing.JTable tblCampanhaVacina;
    // End of variables declaration//GEN-END:variables
}
