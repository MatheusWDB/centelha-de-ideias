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

    //API KEY nas variáveis de ambiente
    @Value("${api.key}")
    private String apiKey;

    private Models modelsApiClient;

    private Client genAiClient;

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
            this.genAiClient = Client.builder()
                    .apiKey(apiKey)
                    .build(); 

            this.modelsApiClient = genAiClient.models;

            System.out.println("Cliente e acesso aos Models Gemini inicializados com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro ao inicializar o cliente/modelo Gemini: " + e.getMessage());
            e.printStackTrace();
            this.modelsApiClient = null;
            this.genAiClient = null;
        }
    }

    @PreDestroy
    public void cleanup() {
        if (genAiClient != null) {
            try {
                genAiClient.close();
                System.out.println("Cliente Gemini fechado.");
            } catch (Exception e) {
                System.err.println("Erro ao fechar o cliente Gemini: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public String identificadorDePontosChave(String text, String ponto) {
        String prompt = "Você é um assistente identificador de Pontos-Chave. " +
                "Defina o " + ponto + " do seguinte texto: " + text +
                "\nSeja diereto e responda SOMENTE com o " + ponto + ", não acrescente nenhum tipo de comentário" +
                "\nCaso o texto não deixe claro, ou não especifique o " + ponto + ", defina um.";

        try {
            GenerateContentResponse response = modelsApiClient.generateContent("gemini-2.0-flash", prompt,
                    null);

            String pontoChave = response.text();

            if (pontoChave == null || pontoChave.trim().isEmpty()) {
                return "O Gemini não gerou uma resposta válida para o seu pedido (resposta vazia).";
            }

            return pontoChave;

        } catch (Exception e) {
            System.err.println("Erro ao chamar a API do Gemini durante a geração de conteúdo: " + e.getMessage());
            e.printStackTrace();
            return "Ocorreu um erro ao gerar a ideia. Por favor, tente novamente mais tarde.";
        }
    }

    public String organizarInformacoes(String text) {
        if (modelsApiClient == null) {
            System.err.println("Tentativa de usar Models API cliente não inicializado.");

            return "Desculpe, o serviço de geração de ideias não está disponível no momento devido a um problema de inicialização.";
        }

        String tema = identificadorDePontosChave(text, "tema principal");
        String tipo = identificadorDePontosChave(text, "tipo de projeto");
        String interesse = identificadorDePontosChave(text, "interesse do usuário");

        String ideia = gerarIdeiaProjeto(tema, tipo, interesse);
        return ideia;
    }

    public String gerarIdeiaProjeto(String tema, String tipoProjeto, String interessesUsuario) {
        System.out.println(tema + tipoProjeto + interessesUsuario);

        String promptText = "Você é um assistente criativo para gerar ideias de projetos. " +
                "Crie uma ideia de projeto inovadora combinando os seguintes elementos:\n" +
                "Tema Principal: " + tema + "\n" +
                "Tipo de Projeto: " + tipoProjeto + "\n" +
                "Interesses do Usuário: " + interessesUsuario + "\n\n" +
                "Sugira uma ideia de projeto clara, descreva brevemente o que ele faria " +
                "e talvez uma tecnologia chave que poderia ser usada. Seja conciso e criativo.\n" +
                "Sugira um nome para o projeto\n" +
                "Deixe claro os requisitos e as regras de negócio a serem implementadas, tudo bonitinho e separado por categoria";

        try {
            GenerateContentResponse response = modelsApiClient.generateContent("gemini-2.0-flash", promptText,
                    null);

            String ideia = response.text();

            if (ideia == null || ideia.trim().isEmpty()) {
                return "O Gemini não gerou uma resposta válida para o seu pedido (resposta vazia).";
            }

            return ideia;

        } catch (Exception e) {
            System.err.println("Erro ao chamar a API do Gemini durante a geração de conteúdo: " + e.getMessage());
            e.printStackTrace();
            return "Ocorreu um erro ao gerar a ideia. Por favor, tente novamente mais tarde.";
        }
    }
}