USE checktransparente;

insert into cliente (id, nome, cpf) 
values               (1, 'Helena', '02676365479'), 
                     (2, 'Ligia', '02676365479'), 
                     (3, 'Sofia', '02676365479');

insert into produto (id, descricao , preco_unitario) 
values              (1, 'NoteBook', 5000.12), 
                    (2, 'TV SAMSUNG', 3000.79), 
                    (3, 'IPhone', 8000.00), 
                    (4, 'Home Theater', 1700.02), 
                    (5, 'Geladeira', 1000.21), 
                    (6, 'Fogão', 1200.66), 
                    (7, 'Micro Ondas', 800.90);

insert into endereco (cliente_id, cep, logradouro, numero, complemento, bairro, cidade, estado) 
values               (1, '50070-480', 'Rua Antonio Gomes de Freitas', 111, null, 'Ilha do Leite', 'Recife', 'Pernambuco'), 
                     (1, '50070-480', 'Rua Jose de Alencar', 1412, null, 'Santo Amaro', 'Recife', 'Pernambuco'), 
                     (2, '50070-480', 'Avenida Norte ', 13, 'proximo a praça', 'IPSEP', 'São Luís', 'Pernambuco'), 
                     (3, '50070-480', 'Avenida SUL', 5315, null, 'casa Forte', 'Recife', 'Maranhão'), 
                     (3, '50070-480', 'Rua Paraguatá', 75, 'Risort Alto', 'Casa Amarela', 'Recife', 'Pernambuco');


insert into transportadora (id, nome, taxa_frete) 
values (1, 'Super Rapido', 100.81), 
       (2, 'Ultra Raido', 90.15), 
       (3, 'Peta Rapido', 147.10);

INSERT INTO pagamento(quantidade_parcelas, tipo_pagamento, valor)
VALUES (1, 1, 0),
       (1, 2, 0),
       (1, 3, 0),
       (1, 3, 0)              