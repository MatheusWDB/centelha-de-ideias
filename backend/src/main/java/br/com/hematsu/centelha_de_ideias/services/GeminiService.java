package br.com.hematsu.centelha_de_ideias.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.Models;
import com.google.genai.types.GenerateContentResponse;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class GeminiService {

    // API KEY nas variáveis de ambiente
    @Value("${api.key}")
    private String apiKey;

    private Models modelsClient;

    private Client geminiClient;

    public GeminiService() {
        System.out.println("Inicializando serviço Gemini com API Key...");
    }

    @PostConstruct
    public void init() {
        if (this.apiKey == null || this.apiKey.trim().isEmpty() || "null".equalsIgnoreCase(this.apiKey.trim())) {
            System.err.println(
                    ">>>> ERRO: API Key não foi configurada ou é nula/vazia após injeção. Verifique .env e application.properties.");

            throw new RuntimeException("API Key para o serviço Gemini não encontrada ou é inválida.");
        }

        try {
            this.geminiClient = Client.builder()
                    .apiKey(apiKey)
                    .build();

            this.modelsClient = geminiClient.models;

            System.out.println("Cliente e acesso aos Models Gemini inicializados com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro ao inicializar o cliente/modelo Gemini: " + e.getMessage());
            e.printStackTrace();
            this.modelsClient = null;
            this.geminiClient = null;
        }
    }

    @PreDestroy
    public void cleanup() {
        if (geminiClient != null) {
            try {
                geminiClient.close();
                System.out.println("Cliente Gemini fechado.");
            } catch (Exception e) {
                System.err.println("Erro ao fechar o cliente Gemini: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public String extractKeyword(String input, String keywordType) {
        String prompt = "Você é um assistente especializado em identificar Pontos-Chave de um texto.\n" +
                "Sua tarefa é extrair o(as) [" + keywordType + "] principal(ais) do seguinte texto fornecido pelo usuário:\n" +
                "```\n" + input + "\n```\n\n" +
                "Instruções:\n" +
                "1. Leia atentamente o texto.\n" +
                "2. Identifique o(as) [" + keywordType + "] solicitado(as) de forma concisa.\n" +
                "3. Responda APENAS com o(as) [" + keywordType + "] identificado(as).\n" +
                "4. NÃO inclua qualquer comentário adicional, introdução, saudação, explicação ou pontuação extra antes ou depois da resposta (ex: 'O tema é:', '-', '.', etc.).\n"
                +
                "5. A resposta deve ser o mais curta possível, idealmente uma palavra ou frase curta.\n" +
                "6. Se o texto estiver vazio ou não mencionar explicitamente o(as) [" + keywordType
                + "], fica ao seu critério defini-lo(as).\n\n" +
                "Resposta (APENAS o [" + keywordType + "]):";

        try {
            GenerateContentResponse response = modelsClient.generateContent("gemini-2.5-flash-preview-04-17-thinking", prompt,
                    null);

            String extractedKeyword = response.text();

            if (extractedKeyword == null || extractedKeyword.trim().isEmpty()) {
                return "O Gemini não gerou uma resposta válida para o seu pedido (resposta vazia).";
            }

            return extractedKeyword;

        } catch (Exception e) {
            System.err.println("Erro ao chamar a API do Gemini durante a geração de conteúdo: " + e.getMessage());
            e.printStackTrace();
            return "Ocorreu um erro ao gerar a ideia. Por favor, tente novamente mais tarde.";
        }
    }

    public String generateIdeaFromInput(String userInput) {
        if (modelsClient == null) {
            System.err.println("Tentativa de usar Models API cliente não inicializado.");

            return "Desculpe, o serviço de geração de ideias não está disponível no momento devido a um problema de inicialização.";
        }

        String theme = extractKeyword(userInput, "tema principal");
        String projectType = extractKeyword(userInput, "tipo de projeto");
        String userInterests = extractKeyword(userInput, "interesse do usuário");
        String techs = extractKeyword(userInput, "tecnologias preferidas");

        String generatedIdea = generateProjectIdea(theme, projectType, userInterests, techs);
        return generatedIdea;
    }

    public String generateProjectIdea(String theme, String projectType, String userInterests, String techs) {
        System.out.println(theme + projectType + userInterests + techs);

        String prompt = "Você é um Gerador de Ideias de Projetos Criativos e Estruturados.\n" +
                "Sua missão é desenvolver UMA ÚNICA ideia de projeto inovadora, interessante e coerente, baseada nos seguintes elementos fornecidos:\n"
                +
                "- Tema Principal: " + theme + "\n" +
                "- Tipo de Projeto: " + projectType + "\n" +
                "- Interesses do Usuário: " + userInterests + "\n\n" +
                "Apresente a ideia de projeto no seguinte FORMATO BEM ESTRUTURADO, usando cabeçalhos claros para cada seção:\n\n"
                +
                "Nome do Projeto:\n" +
                "[Sugira um nome criativo e relevante para o projeto, alinhado com o tema e tipo.]\n\n" +
                "Descrição:\n" +
                "[Escreva uma descrição breve (aprox. 2 a 4 frases) explicando a essência do projeto, o que ele faz, qual problema resolve ou valor entrega, combinando os elementos dados. Mantenha um tom criativo e inspirador.]\n\n"
                +
                "Tecnologias Chaves Sugeridas:\n" +
                "[Sugira tecnologias, frameworks, linguagens ou ferramentas fundamentais e relevantes que poderiam ser usadas para implementar este projeto. Seja específico se possível. Dê preferência as sguintes tecnologias '" + techs + "'. Caso não haja tecnologias mencionadas, fica ao seu critério escolhe-las.]\n\n"
                +
                "Requisitos Principais:\n" +
                "[Liste entre 5 e 10 requisitos essenciais (funcionais ou não funcionais) que o projeto deve atender. Apresente-os como uma lista de tópicos concisos, utilizando marcadores (como '-' ou '*').]\n\n"
                +
                "Regras de Negócio Essenciais:\n" +
                "[Liste entre 5 e 10 regras de negócio fundamentais que definem como o projeto opera ou interage. Apresente-as como uma lista de tópicos concisos, utilizando marcadores (como '-' ou '*').]\n\n"
                +
                "Certifique-se de que todos os elementos da ideia (Nome, Descrição, Tecnologia, Requisitos, Regras) estejam alinhados e integrem de forma significativa o Tema Principal, o Tipo de Projeto e os Interesses do Usuário fornecidos.\n"
                +
                "Não inclua introduções, conclusões ou texto fora das seções especificadas.";

        try {
            GenerateContentResponse response = modelsClient.generateContent("gemini-2.0-flash", prompt,
                    null);

            String ideaContent = response.text();

            if (ideaContent == null || ideaContent.trim().isEmpty()) {
                return "O Gemini não gerou uma resposta válida para o seu pedido (resposta vazia).";
            }

            return ideaContent;

        } catch (Exception e) {
            System.err.println("Erro ao chamar a API do Gemini durante a geração de conteúdo: " + e.getMessage());
            e.printStackTrace();
            return "Ocorreu um erro ao gerar a ideia. Por favor, tente novamente mais tarde.";
        }
    }
}
