package com.biblioteca.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.service.LivroService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/livros")
public class LivroController {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private LivroService livroService;

    @GetMapping
    public String listarLivros(Model model) {
        model.addAttribute("livros", livroRepository.findAll());
        return "livros/lista";
    }

    @GetMapping("/novo")
    public String formularioNovoLivro(Model model) {
        model.addAttribute("livro", new Livro());
        return "livros/form";
    }

    @PostMapping
    public String salvarLivro(@Valid @ModelAttribute Livro livro,
                             BindingResult result,
                             @RequestParam("arquivoLivro") MultipartFile arquivoLivro,
                             @RequestParam("arquivoCapa") MultipartFile arquivoCapa,
                             RedirectAttributes redirectAttributes,
                             Model model) throws IOException {
        
        // Validação dos arquivos
        if (arquivoLivro.isEmpty()) {
            result.rejectValue("conteudo", "error.livro", "O arquivo do livro é obrigatório");
        }
        if (arquivoCapa.isEmpty()) {
            result.rejectValue("capa", "error.livro", "A capa do livro é obrigatória");
        }
        
        // Se houver erros, volta para o formulário
        if (result.hasErrors()) {
            return "livros/form";
        }
        
        try {
            // Validar e processar a imagem da capa
            String contentType = arquivoCapa.getContentType();
            if (contentType != null && (contentType.equals("image/jpeg") || 
                contentType.equals("image/png") || 
                contentType.equals("image/gif"))) {
                
                livro.setCapa(arquivoCapa.getBytes());
            } else {
                model.addAttribute("erro", "Formato de imagem inválido. Use JPEG, PNG ou GIF.");
                return "livros/form";
            }

            // Processar o arquivo do livro
            livro.setConteudo(arquivoLivro.getBytes());
            livro.setNomeArquivo(arquivoLivro.getOriginalFilename());
            livro.setTipoArquivo(arquivoLivro.getContentType());
            
            livroRepository.save(livro);
            redirectAttributes.addFlashAttribute("mensagem", "Livro salvo com sucesso!");
            return "redirect:/livros";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao salvar o livro: " + e.getMessage());
            return "livros/form";
        }
    }

    @GetMapping("/download/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> downloadLivro(@PathVariable Long id) {
        return livroRepository.findById(id)
                .map(livro -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.parseMediaType(livro.getTipoArquivo()));
                    headers.setContentDispositionFormData("attachment", livro.getNomeArquivo());
                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(livro.getConteudo());
                })
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
    }

    @GetMapping("/capa/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> mostrarCapa(@PathVariable Long id) {
        return livroRepository.findById(id)
                .map(livro -> {
                    if (livro.getCapa() == null || livro.getCapa().length == 0) {
                        throw new RuntimeException("Imagem não encontrada");
                    }

                    HttpHeaders headers = new HttpHeaders();
                    headers.setCacheControl("no-cache, no-store, must-revalidate");
                    headers.setPragma("no-cache");
                    headers.setExpires(0L);
                    
                    // Detectar o tipo de imagem baseado nos bytes
                    String contentType = detectImageType(livro.getCapa());
                    headers.setContentType(MediaType.parseMediaType(contentType));
                    
                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(livro.getCapa());
                })
                .orElseThrow(() -> new RuntimeException("Capa não encontrada"));
    }

    private String detectImageType(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length < 4) {
            return "image/jpeg";
        }
        
        // Verificar assinaturas de arquivo
        if (imageBytes[0] == (byte) 0xFF && imageBytes[1] == (byte) 0xD8) {
            return "image/jpeg";
        } else if (imageBytes[0] == (byte) 0x89 && imageBytes[1] == (byte) 0x50) {
            return "image/png";
        } else if (imageBytes[0] == (byte) 0x47 && imageBytes[1] == (byte) 0x49) {
            return "image/gif";
        }
        
        return "image/jpeg";
    }

    @GetMapping("/buscar")
    @ResponseBody
    public List<Livro> buscarLivros(@RequestParam String titulo) {
        return livroService.buscarPorTitulo(titulo);
    }
} 