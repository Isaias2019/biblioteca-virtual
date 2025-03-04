CREATE TABLE IF NOT EXISTS livro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(60) NOT NULL,
    autor VARCHAR(30) NOT NULL,
    descricao VARCHAR(80) NOT NULL,
    nome_arquivo VARCHAR(255),
    tipo_arquivo VARCHAR(255),
    capa MEDIUMBLOB,
    conteudo LONGBLOB
); 