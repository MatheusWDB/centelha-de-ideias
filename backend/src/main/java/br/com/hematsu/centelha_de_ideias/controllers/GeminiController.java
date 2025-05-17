package br.com.hematsu.centelha_de_ideias.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.hematsu.centelha_de_ideias.services.GeminiService;

@RestController
@RequestMapping("/centelha")
@CrossOrigin(origins = "*")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping
    public ResponseEntity<ResponseDTO> getIdea(@RequestBody String request) {
        if (request == null || request.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO("Por favor, forneça o tema, tipo de projeto ou seus interesses para gerar a ideia."));
        }

        try {
            String ideiaGerada = geminiService.organizarInformacoes(request);

            if (ideiaGerada.contains("serviço de geração de ideias não está disponível")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ResponseDTO(ideiaGerada));
            }
            if (ideiaGerada.contains("Ocorreu um erro ao gerar a ideia")
                    || ideiaGerada.contains("problema de inicialização")) { 
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(ideiaGerada));
            }
            if (ideiaGerada.contains("não gerou uma resposta válida")) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ResponseDTO("A API gerou uma resposta vazia para sua solicitação."));
            }

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(ideiaGerada));

        } catch (Exception e) {
            System.err.println("Erro inesperado no controller ao processar a requisição: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("Ocorreu um erro interno no servidor ao processar sua solicitação."));
        }
    }

    private static record ResponseDTO(String text) {
    }
}