# Centelha de Ideias

## 🚀 Sobre o Projeto

O **Centelha de Ideias** é uma aplicação web interativa desenvolvida para estimular a criatividade dos usuários, gerando sugestões de projetos com base em seus temas, tipos de projeto e interesses. A aplicação atua como um assistente de IA, utilizando a poderosa API do **Google Gemini** para analisar as entradas do usuário e fornecer ideias de projetos estruturadas e detalhadas, incluindo nome sugerido, descrição, requisitos e regras de negócio.

A interface do usuário simula um chat intuitivo, permitindo uma interação direta e natural com o assistente de IA para explorar e refinar ideias.

## ✨ Funcionalidades Principais

*   **Interface de Chat Interativa:** Uma experiência de usuário familiar e fácil de usar para interagir com a IA.
*   **Geração Inteligente de Ideias:** Processamento da entrada do usuário para gerar sugestões de projetos relevantes e criativas.
*   **Integração com Google Gemini API:** Utilização de um modelo de linguagem de ponta para garantir a qualidade e relevância das ideias geradas.
*   **Formatação de Respostas:** Apresentação das ideias geradas pela IA formatadas com Markdown para melhor clareza e organização visual.
*   **Feedback de Status em Tempo Real:** Atualização do placeholder do input para indicar o estado da aplicação (ex: processando a solicitação).
*   **Controle de Input Dinâmico:** Desabilita o campo de entrada enquanto a resposta da IA é aguardada, prevenindo múltiplos envios acidentais.
*   **Mensagens de Erro Amigáveis:** Exibição de feedback claro ao usuário em caso de problemas na comunicação com o backend ou na geração da ideia.

## 🛠️ Tecnologias Utilizadas

O projeto é estruturado em duas partes principais: Frontend (cliente) e Backend (servidor).

### Frontend

*   **HTML:** Definição da estrutura e conteúdo da página web.
*   **CSS:** Estilização da interface, com foco em design responsivo e moderno.
*   **Tailwind CSS (via CDN):** Framework utilitário para estilização rápida e eficiente.
*   **JavaScript:** Implementação da lógica de interação do chat, comunicação com o backend e manipulação do DOM.
*   **Marked.js (via CDN):** Biblioteca para renderizar conteúdo formatado em Markdown recebido do backend em HTML.

### Backend

*   **Java:** Linguagem de programação principal.
*   **Spring Boot:** Framework robusto para o desenvolvimento da API REST.
*   **Google AI Java SDK (`google-genai`):** SDK oficial para integração com a API do Google Gemini.
*   **Maven:** Ferramenta para gerenciamento de dependências, build e ciclo de vida do projeto Java.
*   **Docker:** Utilizado para conteinerizar a aplicação backend, facilitando o deploy e a execução consistente.
*   **`spring-dotenv`:** Dependência para carregar variáveis de ambiente (como a chave da API) a partir de um arquivo `.env`.
*   **CORS (`@CrossOrigin`):** Configurado para permitir requisições do frontend hospedado em domínios/portas diferentes.

## ⚙️ Como Executar a Aplicação

Para colocar o "Centelha de Ideias" em funcionamento, você precisará configurar e iniciar o backend e o frontend separadamente.

### Pré-requisitos

Certifique-se de ter os seguintes softwares instalados em sua máquina:

*   **Java Development Kit (JDK) 21 ou superior:** [Download JDK](https://www.oracle.com/java/technologies/downloads/) ou use um gerenciador como SDKMAN!.
*   **Maven:** [Instalação Maven](https://maven.apache.org/install.html).
*   **Chave de API do Google Gemini:** Obtenha uma chave no [Google AI Studio](https://aistudio.google.com/app/apikey).

### Execução do Backend

1.  **Clonar o Repositório:**
    ```bash
    git clone https://github.com/MatheusWDB/centelha-de-ideias
    cd /backend # Navegue até a raiz do diretório do backend
    ```
2.  **Configurar a Chave de API do Google Gemini:**
    Crie um arquivo na raiz do diretório do backend chamado `.env` e adicione sua chave de API:
    ```dotenv
    API_KEY=SUA_CHAVE_DE_API_DO_GEMINI_AQUI
    ```
    Substitua `SUA_CHAVE_DE_API_DO_GEMINI_AQUI` pela sua chave real. Alternativamente, você pode definir a variável de ambiente `API_KEY` diretamente no seu sistema operacional antes de executar a aplicação.
3.  **Método de Execução:**
    *   Na raiz do diretório do backend, compile e execute a aplicação Spring Boot usando o Maven:
        ```bash
        # Limpa, compila o projeto e executa a aplicação Spring Boot
        mvn clean install
        mvn spring-boot:run
        ```
        Certifique-se de que a variável de ambiente `API_KEY` está definida no seu terminal ou o arquivo `.env` está presente *antes* de executar o comando `mvn spring-boot:run`.

O backend estará rodando e escutando em `http://localhost:8080`.

### Execução do Frontend

1.  **Navegue para a Pasta do Frontend:**
    Vá para o diretório onde os arquivos `index.html`, `style.css` e `main.js` estão localizados.
2.  **Altere o valor da url:**
    Abra o main.js e altere url para o seguinte valor -> "http://localhost:8080"
3.  **Abra o Arquivo HTML:**
    Simplesmente abra o arquivo `index.html` diretamente em qualquer navegador web.

    ```bash
    # Exemplo no Linux/macOS:
    open index.html

    # Exemplo no Windows (pode variar):
    start index.html
    ```
    Certifique-se de que o backend já esteja em execução antes de abrir o `index.html`, pois o frontend tentará se comunicar com ele.