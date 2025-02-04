# TECH CHALLENGE 4

## DESCRIÇÃO
Deverá criar um Sistema de Gerenciamento de Pedidos
Integrado com Spring e Microsserviços.

O projeto deverá conter um sistema de gerenciamento de pedidos
altamente eficiente, que explore profundamente a arquitetura de microsserviços
utilizando o ecossistema Spring. Este sistema deverá abranger desde a gestão
de clientes e produtos até o processamento e entrega de pedidos, enfatizando a
autonomia dos serviços, comunicação eficaz e persistência de dados isolada.

## OBJETIVO

O objetivo é criar um sistema modular onde cada microsserviço
desempenha um papel no gerenciamento de pedidos. Este sistema não apenas
facilitará a gestão eficiente de pedidos, mas também servirá como um exemplo
prático do uso de tecnologias de ponta em um cenário realista de
desenvolvimento de software.

## MICROSERVIÇOS

### CLIENTES
Este serviço será responsável por todas as
operações relacionadas aos clientes, incluindo a criação, leitura, atualização e
exclusão de registros de clientes (CRUD).

### CATALOGO DE PRODUTOS
Este microsserviço gerenciará o catálogo de
produtos, incluindo informações detalhadas dos produtos e o controle de
estoque. Uma característica chave será a funcionalidade de carga de produtos,
permitindo a importação em massa de informações de produtos para o sistema.

### GESTAO DE PEDIDOS
Centralizará o processamento de todos os pedidos,
desde a criação até a conclusão. Isso inclui receber pedidos dos clientes,
processar pagamentos (se aplicável) e coordenar com o microsserviço de
logística de entrega para garantir a entrega eficiente dos produtos.

### LOGISTICA DE ENTREGA
Este serviço cuidará de toda a logística
relacionada à entrega de pedidos, desde a atribuição de entregadores até o
rastreamento das entregas em tempo real. A função inclui calcular as rotas mais
eficientes, estimar tempos de entrega e fornecer atualizações de status aos
clientes.

## EQUIPE
A equipe que trabalhou nesse projeto.

- FABIO DA SILVA RIBEIRO *fsribeirorj@gmail.com*
- André Marcondes de Assis *dede.marcondes@gmail.com*
- Jonas Torquatro Oliveira da silva *jonastorquatro@gmail.com*