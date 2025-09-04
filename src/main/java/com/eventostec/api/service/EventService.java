package com.eventostec.api.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.eventostec.api.domain.event.Event;
import com.eventostec.api.domain.event.EventRequestDTO;
import com.eventostec.api.repositories.EventRepository;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/*@Service
public class EventService 
{
    @Autowired
    private AmazonS3 s3Client;
    @Autowired
    private EventRepository repository;
    @Value("${aws.bucket.name}")
    private String bucketName;
    public Event createEvent(EventRequestDTO data)
    {
        String imgUrl = null;
        if(data.getImage() != null)
        {
           imgUrl =  this.uploadImg(data.getImage());
        }
        Event newEvent = new Event();
        newEvent.setTitle(data.getTitle());
        newEvent.setDescription(data.getDescription());
        newEvent.setDate(new Date(data.getDate()));
        newEvent.setEventUrl(data.getEventUrl());
        newEvent.setImgUrl(imgUrl);
        newEvent.setRemote(data.getRemote());
        repository.save(newEvent);
        return newEvent;
    }
    private String uploadImg(MultipartFile multipartFile)
    {
        String filename = UUID.randomUUID()+"-"+multipartFile.getOriginalFilename();
        try
        {
            File file = this.convertMultipartToFile(multipartFile);
            s3Client.putObject(bucketName, filename, file);
            file.delete();
            return s3Client.getUrl(bucketName, filename).toString();
        }
        catch (SdkClientException | IOException e)
        {
            System.out.println("Falha ao processar arquivo: " + e.getMessage());
            return "Imagem não definida";
        } 
    }
    private File convertMultipartToFile(MultipartFile multipartFile) throws IOException
    {
        File convFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try(FileOutputStream fos = new FileOutputStream(convFile))
        {
            
            fos.write(multipartFile.getBytes());
        }
        return convFile;
    }
}*/
@Service
public class EventService 
{
    
    @Autowired
    private AmazonS3 s3Client;
    
    @Autowired
    private EventRepository repository;
    
    @Value("${aws.bucket.name}")
    private String bucketName;
    
    // Lista de tipos MIME permitidos
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
        "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml"
    );
    
    // Tamanho máximo do arquivo (2MB)
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;

    public Event createEvent(EventRequestDTO data) 
    {
        String imgUrl = "default-image.jpg";
        if(data.getImage() != null) 
        {
            // Validação antes do upload
            validateImageFile(data.getImage());
            imgUrl = this.uploadImg(data.getImage());
        }
        
        Event newEvent = new Event();
        newEvent.setTitle(data.getTitle());
        newEvent.setDescription(data.getDescription());
        newEvent.setDate(new Date(data.getDate()));
        newEvent.setEventUrl(data.getEventUrl());
        newEvent.setImgUrl(imgUrl);
        newEvent.setRemote(data.getRemote());
        
        return repository.save(newEvent);
    }

    private void validateImageFile(MultipartFile file) {
        // Verificar tamanho do arquivo
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Arquivo muito grande. Tamanho máximo: 2MB");
        }
        
        // Verificar tipo de conteúdo
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Tipo de arquivo não permitido. Use apenas imagens.");
        }
        
        // Verificar se o arquivo não está vazio
        if (file.isEmpty()) 
        {
            throw new IllegalArgumentException("Arquivo vazio");
        }
    }

    private String uploadImg(MultipartFile multipartFile) {
        String filename = UUID.randomUUID() + "-" + sanitizeFilename(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        
        try 
        {
            if (multipartFile.isEmpty()) 
            {
                return "imagem-padrao.jpg";
            }
            // Upload direto do InputStream sem criar arquivo temporário
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(multipartFile.getContentType());
            metadata.setContentLength(multipartFile.getSize());
            
            s3Client.putObject(bucketName, filename, multipartFile.getInputStream(), metadata);
            
            return s3Client.getUrl(bucketName, filename).toString();
            
        } 
        catch (SdkClientException | IOException e) 
        {
            System.out.println("Falha ao processar arquivo: " + e.getMessage());
            if (e instanceof IOException) 
            {
                return "erro-leitura-arquivo";
            } 
            else if (e instanceof SdkClientException) 
            {
                return "erro-conexao-s3";
            } 
            else 
            {
                return "imagem-indisponivel";
            }
        }
    }

    private String sanitizeFilename(String originalFilename) {
        // Remove caminhos e caracteres especiais
        String safeName = originalFilename.replaceAll(".*[/\\\\]", ""); // Remove paths
        safeName = safeName.replaceAll("[^a-zA-Z0-9.-]", "_"); // Substitui caracteres inválidos
        return safeName;
    }
}