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
import lps.vacinacao.Paciente;
import utils.Persistencia;

/**
 *
 * @author Vitor
 */
public class telaPaciente extends javax.swing.JFrame {
    
    private SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM/yyyy");
    private ArrayList<Paciente> arrayPaciente = new ArrayList();
    Paciente paciente;
    char tipoEdicao;
    
    PreparedStatement ps =  null;
    ResultSet resultado = null;
    
    public telaPaciente() {
        initComponents();
        camposFormatado();
        ativaForm(false);
        //carregarPaciente();
    }
    
    public void excluiPaciente() throws SQLException{
        try {
        int codigo;
        if(tblPaciente.getSelectedRow()!= -1){
           codigo = Integer.parseInt(tblPaciente.getValueAt(tblPaciente.getSelectedRow(), 0).toString());
           paciente = buscarPaciente(codigo);
           preencheFormulario();
            ativaForm(false);
            if(JOptionPane.showConfirmDialog(rootPane,"Deseja excluir o(a):" + paciente.getNome(),"Sistema de Vacinacao",JOptionPane.YES_NO_OPTION)== 0){
                 ps = Persistencia.conexao().prepareStatement("DELETE FROM `vacinacao`.`pessoa` WHERE codigo_paciente = ?");
                 ps.setString(1, String.valueOf(paciente.getCodigo()));
                 ps.executeUpdate();
                carregarPaciente();
               
            }
        }else
           JOptionPane.showMessageDialog(rootPane, "Selecione algum paciente");
        } catch (ParseException ex) {
            Logger.getLogger(telaPaciente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void alteraPaciente(){
        int codigo;
        if(tblPaciente.getSelectedRow()!= -1){
           codigo = Integer.parseInt(tblPaciente.getValueAt(tblPaciente.getSelectedRow(), 0).toString());
           paciente = buscarPaciente(codigo);
           preencheFormulario();
        }else
           JOptionPane.showMessageDialog(rootPane, "Selecione algum paciente");
    }
    
    public Paciente buscarPaciente (int codigo){
        for (int i =0; i<arrayPaciente.size(); i++){
           if(arrayPaciente.get(i).getCodigo()== codigo){
               return arrayPaciente.get(i);
           } 
        }
        return null;
    }
    
    public void preencheFormulario(){
        edtCodigo.setText(String.valueOf(paciente.getCodigo()));
        edtNome.setText(paciente.getNome());
        ftxtDataNascimento.setText(formatarData.format(paciente.getDataNasc()));
        ftxtCPF.setText(paciente.getCpf());
        if(paciente.getSexo()== 'M')
            cbxSexo.setSelectedIndex(0);
        else
            cbxSexo.setSelectedIndex(1);
        
        
    }
    
    public void salvarPaciente() throws SQLException{
        try {
        if(tipoEdicao == 'I'){    
            paciente = new Paciente();
            paciente.setCodigo(Integer.parseInt(edtCodigo.getText()));
        }
        
        paciente.setNome(edtNome.getText());
        paciente.setDataNasc(formatarData.parse(ftxtDataNascimento.getText()));
        paciente.setCpf(ftxtCPF.getText());
        paciente.setSexo(cbxSexo.getItemAt(cbxSexo.getSelectedIndex()).toCharArray()[0]);
        
        if(tipoEdicao == 'I'){
            
            ps = Persistencia.conexao().prepareStatement("INSERT INTO `vacinacao`.`pessoa`(nome,data_nascimento,cpf,sexo,codigo_paciente) values (?, ?, ?, ?, ?)"); 


            ps.setString(1, paciente.getNome());
            ps.setString(2, formatarData.format(paciente.getDataNasc()));
            ps.setString(3, paciente.getCpf());
            ps.setString(4, String.valueOf(paciente.getSexo()));
            ps.setString(5, String.valueOf(paciente.getCodigo()));
            
            ps.executeUpdate();
            
        } else if(tipoEdicao == 'E'){
            ps = Persistencia.conexao().prepareStatement("UPDATE `vacinacao`.`pessoa` SET nome=?,data_nascimento=?,cpf=?,sexo=? WHERE codigo_paciente = ?"); 


            ps.setString(1, paciente.getNome());
            ps.setString(2, formatarData.format(paciente.getDataNasc()));
            ps.setString(3, paciente.getCpf());
            ps.setString(4, String.valueOf(paciente.getSexo()));
            ps.setString(5, String.valueOf(paciente.getCodigo()));
            
            ps.executeUpdate();
            carregarPaciente();
        }
            
        } catch (ParseException ex) {
            Logger.getLogger(telaPaciente.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    public void carregarPaciente() throws ParseException{
        String [] colunas = {"Codigo", "Nome", "Data Nascimento", "CPF", "Sexo"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        
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
            
            arrayPaciente.add(paciente);
            
            Object [] linha  = {paciente.getCodigo(),paciente.getNome(),formatarData.format(paciente.getDataNasc()),paciente.getCpf(),paciente.getSexo()};
            modelo.addRow(linha);
        }
        tblPaciente.setModel(modelo);
        
        } catch (SQLException ex) {
            Logger.getLogger(telaPaciente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void limparDados(){
        edtCodigo.setText("");
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
        
        if(edtCodigo.getText().replace(" ", "").matches("[0-9],{1,}")){
            JOptionPane.showMessageDialog(rootPane,"Preencha o Codigo corretamente (somente numeros)");
            edtCodigo.requestFocus();
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
        } else
            if(!verificaCPF(ftxtCPF.getText())){
            JOptionPane.showMessageDialog(rootPane, "CPF Inválido");
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        pnlForm = new javax.swing.JPanel();
        edtCodigo = new javax.swing.JTextField();
        lblCodigo = new javax.swing.JLabel();
        lblNome = new javax.swing.JLabel();
        edtNome = new javax.swing.JTextField();
        lblCPF = new javax.swing.JLabel();
        lblNascimento = new javax.swing.JLabel();
        lblSexo = new javax.swing.JLabel();
        cbxSexo = new javax.swing.JComboBox<>();
        ftxtCPF = new javax.swing.JFormattedTextField();
        ftxtDataNascimento = new javax.swing.JFormattedTextField();
        btnSalvar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        btnAdicionar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        btnConsultar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPaciente = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlForm.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        edtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edtCodigoActionPerformed(evt);
            }
        });

        lblCodigo.setText("Código:");

        lblNome.setText("Nome:");

        edtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edtNomeActionPerformed(evt);
            }
        });

        lblCPF.setText("CPF:");

        lblNascimento.setText("Data Nascimento:");

        lblSexo.setText("Sexo:");

        cbxSexo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Feminino" }));
        cbxSexo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxSexoActionPerformed(evt);
            }
        });

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
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFormLayout.createSequentialGroup()
                        .addComponent(lblNascimento)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxtDataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblCPF)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxtCPF, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(64, 64, 64)
                        .addComponent(lblSexo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbxSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlFormLayout.createSequentialGroup()
                        .addComponent(lblCodigo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblNome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edtNome)))
                .addContainerGap())
        );
        pnlFormLayout.setVerticalGroup(
            pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCodigo)
                    .addComponent(lblNome)
                    .addComponent(edtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFormLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNascimento)
                            .addComponent(lblSexo)
                            .addComponent(cbxSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ftxtDataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCPF)
                            .addComponent(ftxtCPF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFormLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSalvar)
                        .addContainerGap())))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConsultar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        tblPaciente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tblPaciente);

        jLabel1.setFont(new java.awt.Font("Consolas", 1, 36)); // NOI18N
        jLabel1.setText("          CADASTRO DE PACIENTES");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE)
                .addGap(143, 143, 143))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlForm, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void edtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edtCodigoActionPerformed

    private void edtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edtNomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edtNomeActionPerformed

    private void cbxSexoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxSexoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbxSexoActionPerformed

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        tipoEdicao = 'I';
        ativaForm(true);
        limparDados();
        
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        tipoEdicao = 'E';
        ativaForm(true);
        limparDados();
        alteraPaciente();
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        ativaForm(false);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        try {
            excluiPaciente();
        } catch (SQLException ex) {
            Logger.getLogger(telaPaciente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        try {
        if(validacaoCampos()){
           
                //JOptionPane.showMessageDialog(rootPane, "Dados preenchidos corretamente");
           salvarPaciente();

           limparDados();
           ativaForm(false);
           JOptionPane.showMessageDialog(rootPane, "Dados salvos com sucesso"); 

        }
        } catch (SQLException ex) {
                Logger.getLogger(telaPaciente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        try {
            carregarPaciente();
        } catch (ParseException ex) {
            Logger.getLogger(telaPaciente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnConsultarActionPerformed

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
            java.util.logging.Logger.getLogger(telaPaciente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(telaPaciente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(telaPaciente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(telaPaciente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new telaPaciente().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConsultar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JComboBox<String> cbxSexo;
    private javax.swing.JTextField edtCodigo;
    private javax.swing.JTextField edtNome;
    private javax.swing.JFormattedTextField ftxtCPF;
    private javax.swing.JFormattedTextField ftxtDataNascimento;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblCPF;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblNascimento;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblSexo;
    private javax.swing.JPanel pnlForm;
    private javax.swing.JTable tblPaciente;
    // End of variables declaration//GEN-END:variables
}
