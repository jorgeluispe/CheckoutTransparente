# checkout transparente, teste PULSE

Aplicação utilizando Spring Boot, MySQL, JPA e Hibernate.

## Requisitos

1. Java - 1.8.x
2. Maven - 3.x.x
3. Mysql - 5.x.x

## Passos para o SETUP

**1. Clone a aplicação**

```bash
git clone https://github.com/jorgeluispe/CheckoutTransparente.git
```


**2. Modifique o usuário e senha de acordo com a sua instalação**

+ abra `src/main/resources/application.properties`

+ modifique `spring.datasource.username` e `spring.datasource.password` de acordo com sua instalação


O app vai rodar nesse endereço <http://localhost:8080>.

**3. Não é necessário criar database **
o projeto está configurado pra criar o database, as tabelas porem será preciso executar o arquivo importacoes.sql que esta em src/main/resources/

**4. Aqui temos os exmplos das URLs do sistema com alguns exemplos de JSON para executalas no POSTMAN

Carregar cliente
verbo GET
http://localhost:8080/api/clientes/1


Gravar Pedido
verbo POST
http://localhost:8080/api/pedidos
{
"cliente": 1,
    "total": 100.78,
    "items": [
        {
            "produto": 2,
            "quantidade": 1,
            "vlrUni": 100.78,
            "vlrTot": 100.78
        }
    ]
}

Gravar Endereço Pedido
http://localhost:8080/api/pedidos/1/endereco
{
    "id" : 1
}

Gravar Transportadora do Pedido
http://localhost:8080/api/pedidos/1/transportadora
{
    "id" : 1
}

Gravar pagamento do Pedido
http://localhost:8080/api/pedidos/1/executar-pagamento
{
    "id" : 1,
    "tipoPagamento" : 1, 
    "quantidadeParcelas" : 1, 
    "valor" : 1000
}

## Erros da aplicação
Exite um problema no momento de pegar o Código de rastreio pois o mesmo esta retornando null no momento de persistir a informação quanto tento clonar o pedido porem em comentando o codigo ele funciona normalmente esta em um TODO
