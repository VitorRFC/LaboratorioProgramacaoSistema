/* Lógico_LPS: */

CREATE TABLE Pessoa (
    Nome VARCHAR,
    CPF VARCHAR,
    DataNascimento VARCHAR,
    Sexo CHAR,
    Codigo INT,
    Matricula INT,
    Pessoa_TIPO INT,
    PRIMARY KEY (Codigo, Matricula)
);

CREATE TABLE Vacina (
    Lote INT PRIMARY KEY,
    Validade VARCHAR,
    Nome VARCHAR
);

CREATE TABLE Campanha_Vacinacao (
    Lote INT,
    NomeCampanha VARCHAR,
    Validade VARCHAR,
    DataInicial VARCHAR,
    DataFinal VARCHAR
);

CREATE TABLE CartaoVacina (
);

CREATE TABLE aplicacao (
    fk_Pessoa_Codigo INT,
    fk_Pessoa_Matricula INT,
    fk_Vacina_Lote INT
);

CREATE TABLE Existe (
    fk_Vacina_Lote INT
);
 
ALTER TABLE aplicacao ADD CONSTRAINT FK_aplicacao_1
    FOREIGN KEY (fk_Pessoa_Codigo, fk_Pessoa_Matricula)
    REFERENCES Pessoa (Codigo, Matricula)
    ON DELETE SET NULL;
 
ALTER TABLE aplicacao ADD CONSTRAINT FK_aplicacao_2
    FOREIGN KEY (fk_Vacina_Lote)
    REFERENCES Vacina (Lote)
    ON DELETE SET NULL;
 
ALTER TABLE Existe ADD CONSTRAINT FK_Existe_1
    FOREIGN KEY (fk_Vacina_Lote)
    REFERENCES Vacina (Lote)
    ON DELETE SET NULL;