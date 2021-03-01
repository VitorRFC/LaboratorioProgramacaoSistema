/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

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
import lps.vacinacao.Vacina;
import utils.Persistencia;

/**
 *
 * @author Vitor
 */
public class telaVacina extends javax.swing.JFrame {

    private SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM/yyyy");
    private ArrayList<Vacina> arrayVacina = new ArrayList();
    Vacina vacina;
    char tipoEdicao;
    
    PreparedStatement ps =  null;
    ResultSet resultado = null;
    
    
    public telaVacina() {
        initComponents();
        camposFormatado();
        ativaForm(false);
        //carregarVacina();
    }
    
    public void excluiVacina() throws ParseException{
        try {
        int lote;
        if(tblVacina.getSelectedRow()!= -1){
           lote = Integer.parseInt(tblVacina.getValueAt(tblVacina.getSelectedRow(), 0).toString());
           vacina = buscarVacina(lote);
           preencheFormulario();
            ativaForm(false);
            if(JOptionPane.showConfirmDialog(rootPane,"Deseja excluir o(a:" + vacina.getNome(),"Sistema de Vacinacao",JOptionPane.YES_NO_OPTION)== 0){
               
                ps = Persistencia.conexao().prepareStatement("DELETE FROM `vacinacao`.`vacina` WHERE lote = ?");

                ps.setString(1, String.valueOf(vacina.getLote())); 
                ps.executeUpdate();
               
                arrayVacina.remove(vacina);
                carregarVacina();
            }
        }else
            JOptionPane.showMessageDialog(rootPane, "Selecione alguma Vacina");
        } catch (SQLException ex) {
            Logger.getLogger(telaVacina.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void alteraVacina(){
        int lote;
        if(tblVacina.getSelectedRow()!= -1){
           lote = Integer.parseInt(tblVacina.getValueAt(tblVacina.getSelectedRow(), 0).toString());
           vacina = buscarVacina(lote);
           preencheFormulario();
        }else
            JOptionPane.showMessageDialog(rootPane, "Selecione alguma Vacina");
    }
    public Vacina buscarVacina (int lote){
        for (int i =0; i<arrayVacina.size(); i++){
           if(arrayVacina.get(i).getLote()== lote){
               return arrayVacina.get(i);
           } 
        }
        return null;
    }
        public void preencheFormulario(){
        edtLote.setText(String.valueOf(vacina.getLote()));
        edtNome.setText(vacina.getNome());
        ftxtValidade.setText(formatarData.format(vacina.getValidade()));     
        
    }
    
    public void salvarVacina() throws SQLException{
        try {
        if(tipoEdicao == 'I'){
        vacina = new Vacina();
        vacina.setLote(Integer.parseInt(edtLote.getText()));
        }
        
        vacina.setNome(edtNome.getText());
        vacina.setValidade(formatarData.parse(ftxtValidade.getText()));
        
        if(tipoEdicao == 'I'){
            ps = Persistencia.conexao().prepareStatement("INSERT INTO `vacinacao`.`vacina`(nome_vacina,validade,lote) values (?, ?, ?)"); 

            ps.setString(1, vacina.getNome());
            ps.setString(2, formatarData.format(vacina.getValidade()));
            ps.setString(3, String.valueOf(vacina.getLote()));
            
            ps.executeUpdate();
        }else if(tipoEdicao == 'E'){
            ps = Persistencia.conexao().prepareStatement("UPDATE `vacinacao`.`vacina` SET nome_vacina=?,validade=? WHERE lote = ?"); 

            ps.setString(1, vacina.getNome());
            ps.setString(2, formatarData.format(vacina.getValidade()));
            ps.setString(3, String.valueOf(vacina.getLote()));
            
            ps.executeUpdate();
            carregarVacina();
        }
        } catch (ParseException ex) {
            Logger.getLogger(telaPaciente.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    public void carregarVacina() throws ParseException{
        String [] colunas = {"Lote", "Nome", "Validade"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        
        try {
            ps = Persistencia.conexao().prepareStatement("SELECT * FROM vacinacao.vacina;");
            resultado = ps.executeQuery();
        
            while (resultado.next()){
                vacina = new Vacina();
                vacina.setLote(resultado.getInt("lote"));
                vacina.setNome(resultado.getString("nome_vacina"));
                vacina.setValidade(formatarData.parse(resultado.getString("validade")));
                arrayVacina.add(vacina);
                
                Object [] linha  = {vacina.getLote(),vacina.getNome(),formatarData.format(vacina.getValidade())};
                modelo.addRow(linha);
            }
        tblVacina.setModel(modelo);
        } catch (SQLException ex) {
            Logger.getLogger(telaVacina.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void limparDados(){
        edtLote.setText("");
        edtNome.setText("");
        ftxtValidade.setText("");
    }
    
    public void camposFormatado(){
        try {
            MaskFormatter maskData = new MaskFormatter("##/##/####");            
            maskData.install(ftxtValidade);
                       
            
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
        btnSalvar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVacina = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Consolas", 1, 24)); // NOI18N
        jLabel1.setText("                 Cadastro de Vacina");

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(119, 119, 119))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlForm.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblLote.setText("Lote:");

        lblNome.setText("Nome:");

        lblValidade.setText("Validade:");

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
                .addGap(18, 18, 18)
                .addComponent(lblNome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblValidade)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ftxtValidade, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 14, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFormLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSalvar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblVacina.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblVacina);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(pnlForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            
                //JOptionPane.showMessageDialog(rootPane, "Dados preenchidos corretamente");
                salvarVacina();
            
            limparDados();
            ativaForm(false);
            JOptionPane.showMessageDialog(rootPane, "Dados salvos com sucesso");
        }
        } catch (SQLException ex) {
                Logger.getLogger(telaVacina.class.getName()).log(Level.SEVERE, null, ex);
            }
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        try {
            carregarVacina();
        } catch (ParseException ex) {
            Logger.getLogger(telaVacina.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnConsultarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        tipoEdicao = 'E';
        ativaForm(true);
        limparDados();
        alteraVacina(); 
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        try {
            excluiVacina();        // TODO add your handling code here:
        } catch (ParseException ex) {
            Logger.getLogger(telaVacina.class.getName()).log(Level.SEVERE, null, ex);
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
            java.util.logging.Logger.getLogger(telaVacina.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(telaVacina.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(telaVacina.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(telaVacina.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new telaVacina().setVisible(true);
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
    private javax.swing.JFormattedTextField ftxtValidade;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblLote;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblValidade;
    private javax.swing.JPanel pnlForm;
    private javax.swing.JTable tblVacina;
    // End of variables declaration//GEN-END:variables
}
