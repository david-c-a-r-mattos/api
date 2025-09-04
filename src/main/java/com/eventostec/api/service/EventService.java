package com.eventostec.api.service;

import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import com.eventostec.api.domain.event.Event;
import com.eventostec.api.domain.event.EventRequestDTO;
import com.eventostec.api.repositories.EventRepository;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EventService 
{
    private final S3Client s3Client;
    private final EventRepository repository;
    
    @Value("${aws.s3.bucket:default-bucket}") // valor padrão
    private String bucketName;
    
    public EventService(S3Client s3Client, EventRepository repository) 
    {
        this.s3Client = s3Client;
        this.repository = repository;
    }
    
    public Event createEvent(EventRequestDTO data)
    {
        String imgUrl = "default-image.jpg";
        if(data.getImage() != null && !data.getImage().isEmpty())
        {
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
    
    private String uploadImg(MultipartFile multipartFile)
    {
        // Se S3Client não estiver configurado, retorna imagem padrão
        if (s3Client == null) {
            System.out.println("S3Client não configurado - usando imagem padrão");
            return "default-image.jpg";
        }
        
        String filename = UUID.randomUUID() + "-" + 
                         Objects.requireNonNull(multipartFile.getOriginalFilename());
        
        try
        {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .contentType(multipartFile.getContentType())
                .contentLength(multipartFile.getSize())
                .build();
            
            s3Client.putObject(putObjectRequest, 
                RequestBody.fromBytes(multipartFile.getBytes()));
            
            String fileUrl = s3Client.utilities()
                .getUrl(builder -> builder.bucket(bucketName).key(filename))
                .toString();
            
            return fileUrl;
            
        }
        catch (SdkException | IOException e)
        {
            System.out.println("Falha ao processar arquivo: " + e.getMessage());
            return "exception.jpg";
        } 
    }
}