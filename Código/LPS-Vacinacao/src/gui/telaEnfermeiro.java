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
import lps.vacinacao.Enfermeiro;
import utils.Persistencia;

/**
 *
 * @author Vitor
 */
public class telaEnfermeiro extends javax.swing.JFrame {

    private SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM/yyyy");
    private ArrayList<Enfermeiro> arrayEnfermeiro = new ArrayList();
    Enfermeiro enfermeiro;
    char tipoEdicao;
    
    PreparedStatement ps =  null;
    ResultSet resultado = null;
    
    public telaEnfermeiro() {
        initComponents();
        camposFormatado();
        ativaForm(false);
        //carregarEnfermeiro();
    }
    
    public void excluiEnfermeiro() throws SQLException, ParseException{
        int matricula;
        if(tblEnfermeiro.getSelectedRow()!= -1){
           matricula = Integer.parseInt( tblEnfermeiro.getValueAt(tblEnfermeiro.getSelectedRow(), 0).toString());
           enfermeiro = buscarEnfermeiro(matricula);
           preencheFormulario();
           ativaForm(false);
           if(JOptionPane.showConfirmDialog(rootPane,"Deseja excluir o(a):" + enfermeiro.getNome(),"Sistema de Vacinacao",JOptionPane.YES_NO_OPTION)== 0){
                ps = Persistencia.conexao().prepareStatement("DELETE FROM `vacinacao`.`pessoa` WHERE matricula_enfermeiro = ?");
                ps.setString(1, String.valueOf(enfermeiro.getMatricula())); 
                ps.executeUpdate();
               arrayEnfermeiro.remove(enfermeiro);
               carregarEnfermeiro();
            }
        }else
            JOptionPane.showMessageDialog(rootPane, "Selecione algum(a) Enfermeiro(a)");
    }
    
    
    public void alteraEnfermeiro(){
        int matricula;
        if(tblEnfermeiro.getSelectedRow()!= -1){
           matricula = Integer.parseInt( tblEnfermeiro.getValueAt(tblEnfermeiro.getSelectedRow(), 0).toString());
           enfermeiro = buscarEnfermeiro(matricula);
           preencheFormulario();
        }else
            JOptionPane.showMessageDialog(rootPane, "Selecione algum(a) Enfermeiro(a)");
    }
    
    public Enfermeiro buscarEnfermeiro (int matricula){
        for (int i =0; i<arrayEnfermeiro.size(); i++){
           if(arrayEnfermeiro.get(i).getMatricula()== matricula){
               return arrayEnfermeiro.get(i);
           } 
        }
        return null;
    }
    
    public void preencheFormulario(){
        edtMatricula.setText(String.valueOf(enfermeiro.getMatricula()));
        edtNome.setText(enfermeiro.getNome());
        ftxtDataNascimento.setText(formatarData.format(enfermeiro.getDataNasc()));
        ftxtCPF.setText(enfermeiro.getCpf());
        if(enfermeiro.getSexo()== 'M')
            cbxSexo.setSelectedIndex(0);
        else
            cbxSexo.setSelectedIndex(1);
        
        
    }
    
    public void salvarEnfermeiro() throws SQLException{
        try {
        if(tipoEdicao == 'I'){    
            enfermeiro = new Enfermeiro();
            enfermeiro.setMatricula(Integer.parseInt(edtMatricula.getText()));
        }
        enfermeiro.setNome(edtNome.getText());
        enfermeiro.setDataNasc(formatarData.parse(ftxtDataNascimento.getText()));
        enfermeiro.setCpf(ftxtCPF.getText());
        enfermeiro.setSexo(cbxSexo.getItemAt(cbxSexo.getSelectedIndex()).toCharArray()[0]);
        
        if(tipoEdicao == 'I'){
            ps = Persistencia.conexao().prepareStatement("INSERT INTO `vacinacao`.`pessoa`(nome,data_nascimento,cpf,sexo,matricula_enfermeiro) values (?, ?, ?, ?, ?)"); 


            ps.setString(1, enfermeiro.getNome());
            ps.setString(2, formatarData.format(enfermeiro.getDataNasc()));
            ps.setString(3, enfermeiro.getCpf());
            ps.setString(4, String.valueOf(enfermeiro.getSexo()));
            ps.setString(5, String.valueOf(enfermeiro.getMatricula()));
            
            ps.executeUpdate();
        }else if(tipoEdicao == 'E'){
            ps = Persistencia.conexao().prepareStatement("UPDATE `vacinacao`.`pessoa` SET nome=?,data_nascimento=?,cpf=?,sexo=? WHERE matricula_enfermeiro = ?"); 


            ps.setString(1, enfermeiro.getNome());
            ps.setString(2, formatarData.format(enfermeiro.getDataNasc()));
            ps.setString(3, enfermeiro.getCpf());
            ps.setString(4, String.valueOf(enfermeiro.getSexo()));
            ps.setString(5, String.valueOf(enfermeiro.getMatricula()));
            
            ps.executeUpdate();
            carregarEnfermeiro();
        }
      
        
        } catch (ParseException ex) {
            Logger.getLogger(telaPaciente.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    public void carregarEnfermeiro() throws ParseException {
        String [] colunas = {"Matricula", "Nome", "Data Nascimento", "CPF", "Sexo"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        
        try {
            ps = Persistencia.conexao().prepareStatement("SELECT * FROM vacinacao.pessoa WHERE matricula_enfermeiro is not null;");
            resultado = ps.executeQuery();
        
            while (resultado.next()){
            enfermeiro = new Enfermeiro();
            enfermeiro.setMatricula(resultado.getInt("matricula_enfermeiro"));
            enfermeiro.setCpf(resultado.getString("cpf"));
            enfermeiro.setDataNasc(formatarData.parse(resultado.getString("data_nascimento")));
            enfermeiro.setNome(resultado.getString("nome"));
            enfermeiro.setSexo(resultado.getString("sexo").charAt(0));
            
            arrayEnfermeiro.add(enfermeiro);
            
            Object [] linha  = {enfermeiro.getMatricula(),enfermeiro.getNome(),formatarData.format(enfermeiro.getDataNasc()),enfermeiro.getCpf(),enfermeiro.getSexo()};
            modelo.addRow(linha);
        }
        tblEnfermeiro.setModel(modelo);
        } catch (SQLException ex) {
            Logger.getLogger(telaEnfermeiro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void limparDados(){
        edtMatricula.setText("");
        edtNome.setText("");
        ftxtDataNascimento.setText("");
        ftxtCPF.setText("");
        cbxSexo.setSelectedIndex(0);
    }

    
    
    public void camposFormatado(){
        try {
            MaskFormatter maskData = new MaskFormatter("##/##/####");
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            
            maskData.install(ftxtDataNascimento);
            maskCPF.install(ftxtCPF);
            
        } catch (ParseException ex) {
            Logger.getLogger(telaPaciente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean verificaCPF(String cpf){
        
        int digito1=0, digito2=0, calcDig1=0, calcDig2=0, x=10, y=11;
        int [] arrayCPF;
        arrayCPF = new int[9];
        boolean repetida = true; 
        
        digito1 = Integer.parseInt(cpf.substring(12, 13));
        digito2 = Integer.parseInt(cpf.substring(13, 14));
        cpf = cpf.substring(0, 3) + cpf.substring(4, 7) + cpf.substring(8, 11);
        
        for (int i=0; i<arrayCPF.length; i++){
            
            arrayCPF[i] = Integer.parseInt(cpf.substring(i, i+1));
            
            calcDig1 += x * arrayCPF[i];
            x--;
            
            calcDig2 += y * arrayCPF[i];
            y--;
        
            if(arrayCPF[0] != arrayCPF[i] && repetida)
                repetida = false;
        }
        calcDig2 += digito1 * y;
        
        calcDig1 = calcDig1 * 10 % 11;
        calcDig2 = calcDig2 * 10 % 11;
        
        if (calcDig1 == 10)
            calcDig1 = 0;
        
        if (calcDig2 == 10)
            calcDig2 = 0;
        
        if(calcDig1 != digito1 || calcDig2 != digito2 || repetida)
            return false;
        else
            return true;
    }
        
    public boolean validacaoCampos(){
        
        if(edtMatricula.getText().replace(" ", "").matches("[0-9],{1,}")){
            JOptionPane.showMessageDialog(rootPane,"Preencha a Matricula corretamente (somente numeros)");
            edtMatricula.requestFocus();
            return false;
        }
        if(edtNome.getText().replace(" ", "").matches("[A-Za-z],{3,}")){
            JOptionPane.showMessageDialog(rootPane,"Preencha o Nome corretamente (somente as letras sem acentos)");
            edtNome.requestFocus();
            return false;
        }
        if(ftxtDataNascimento.getText().replace(" ", "").length() < 10){
            JOptionPane.showMessageDialog(rootPane,"Preencha a Data de Nascimento corretamente (deve conter 8 digitos)");
            ftxtDataNascimento.requestFocus();
            return false;
        }
        if(ftxtCPF.getText().replace(" ", "").length() < 13){
            JOptionPane.showMessageDialog(rootPane,"Preencha o CPF corretamente (deve conter 11 digitos)");
            ftxtCPF.requestFocus();
            return false;
        }else
            if(!verificaCPF(ftxtCPF.getText())){
            JOptionPane.showMessageDialog(rootPane, "CPF Inválido");
            return false;
            }
      
        return true;
    }
    
    

    public void ativaForm(boolean ativaDesativa){
    for(int i=0; i<pnlFormEnf.getComponents().length; i++){
            pnlFormEnf.getComponent(i).setEnabled(ativaDesativa);
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        pnlFormEnf = new javax.swing.JPanel();
        lblMatricula = new javax.swing.JLabel();
        edtMatricula = new javax.swing.JTextField();
        lblNome = new javax.swing.JLabel();
        edtNome = new javax.swing.JTextField();
        lblDataNascimento = new javax.swing.JLabel();
        ftxtDataNascimento = new javax.swing.JFormattedTextField();
        lblCPF = new javax.swing.JLabel();
        ftxtCPF = new javax.swing.JFormattedTextField();
        lblSexo = new javax.swing.JLabel();
        cbxSexo = new javax.swing.JComboBox<>();
        btnSalvar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnAdicionar = new javax.swing.JToggleButton();
        btnEditar = new javax.swing.JToggleButton();
        btnCancelar = new javax.swing.JToggleButton();
        btnExcluir = new javax.swing.JToggleButton();
        btnConsultar = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEnfermeiro = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Consolas", 1, 36)); // NOI18N
        jLabel1.setText("      CADASTRO DE ENFERMEIROS");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlFormEnf.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblMatricula.setText("Matricula:");

        lblNome.setText("Nome:");

        lblDataNascimento.setText("Data Nascimento:");

        lblCPF.setText("CPF:");

        lblSexo.setText("Sexo:");

        cbxSexo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Feminino" }));

        btnSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icone/salvar.png"))); // NOI18N
        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFormEnfLayout = new javax.swing.GroupLayout(pnlFormEnf);
        pnlFormEnf.setLayout(pnlFormEnfLayout);
        pnlFormEnfLayout.setHorizontalGroup(
            pnlFormEnfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormEnfLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFormEnfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFormEnfLayout.createSequentialGroup()
                        .addComponent(lblMatricula)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edtMatricula, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblNome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edtNome))
                    .addGroup(pnlFormEnfLayout.createSequentialGroup()
                        .addComponent(lblDataNascimento)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxtDataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblCPF)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxtCPF, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblSexo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlFormEnfLayout.setVerticalGroup(
            pnlFormEnfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormEnfLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFormEnfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMatricula)
                    .addComponent(edtMatricula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNome)
                    .addComponent(edtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlFormEnfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDataNascimento)
                    .addComponent(ftxtDataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCPF)
                    .addComponent(ftxtCPF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSexo)
                    .addComponent(cbxSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(21, Short.MAX_VALUE))
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

        tblEnfermeiro.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblEnfermeiro);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlFormEnf, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlFormEnf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        ativaForm(false);        
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        tipoEdicao = 'I';
        ativaForm(true);
        limparDados();
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        try {
        if(validacaoCampos()){
                //JOptionPane.showMessageDialog(rootPane, "Dados preenchidos corretamente");
            salvarEnfermeiro();
            limparDados();
            ativaForm(false);
            JOptionPane.showMessageDialog(rootPane, "Dados salvos com sucesso");
        }
        } catch (SQLException ex) {
                Logger.getLogger(telaEnfermeiro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        try {
            carregarEnfermeiro();
        } catch (ParseException ex) {
            Logger.getLogger(telaEnfermeiro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnConsultarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        tipoEdicao = 'E';
        ativaForm(true);
        limparDados();
        alteraEnfermeiro();
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        try {
            excluiEnfermeiro();
        } catch (SQLException ex) {
            Logger.getLogger(telaEnfermeiro.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(telaEnfermeiro.class.getName()).log(Level.SEVERE, null, ex);
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
            java.util.logging.Logger.getLogger(telaEnfermeiro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(telaEnfermeiro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(telaEnfermeiro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(telaEnfermeiro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new telaEnfermeiro().setVisible(true);
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
    private javax.swing.JComboBox<String> cbxSexo;
    private javax.swing.JTextField edtMatricula;
    private javax.swing.JTextField edtNome;
    private javax.swing.JFormattedTextField ftxtCPF;
    private javax.swing.JFormattedTextField ftxtDataNascimento;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCPF;
    private javax.swing.JLabel lblDataNascimento;
    private javax.swing.JLabel lblMatricula;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblSexo;
    private javax.swing.JPanel pnlFormEnf;
    private javax.swing.JTable tblEnfermeiro;
    // End of variables declaration//GEN-END:variables
}
