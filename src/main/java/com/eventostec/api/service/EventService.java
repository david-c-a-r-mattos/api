package com.eventostec.api.service;

import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import com.eventostec.api.domain.event.Event;
import com.eventostec.api.domain.event.EventRequestDTO;
import com.eventostec.api.domain.event.EventResponseDTO;
import com.eventostec.api.repositories.EventRepository;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EventService 
{
    private final S3Client s3Client;
    private final EventRepository repository;
    private AddressService addressService;
  
    @Value("${aws.s3.bucket:default-bucket}") // valor padrão
    private String bucketName;
    
    public EventService(S3Client s3Client, EventRepository repository, AddressService addressService) 
    {
        this.s3Client = s3Client;
        this.repository = repository;
        this.addressService = addressService;
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
        
        repository.save(newEvent);
        
        if(!data.getRemote())
        {
            this.addressService.createAddress(data, newEvent);
        } 
        return newEvent;
        
        
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
    public List<EventResponseDTO> getUpcomingEvents(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPage = this.repository.findUpcomingEvents(new Date(), pageable);
        String city = "";
        String state = "";

        return eventsPage.map(event -> new EventResponseDTO(
            event.getId(), 
            city, 
            state, 
            event.getTitle(), 
            event.getDescription(), 
            event.getDate(), 
            event.getEventUrl(), 
            event.getImgUrl(), 
            event.getRemote()
        )).getContent(); // Use getContent() para obter a lista
    }
}