package com.eventostec.api.service;

import com.eventostec.api.domain.coupon.Coupon;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import com.eventostec.api.domain.event.Event;
import com.eventostec.api.domain.event.EventDetailsDTO;
import com.eventostec.api.domain.event.EventRequestDTO;
import com.eventostec.api.domain.event.EventResponseDTO;
import com.eventostec.api.repositories.EventRepository;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.stream.Collectors;

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
    private CouponService couponService;
    
    // Formato para converter Date para String
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  
    @Value("${aws.s3.bucket:default-bucket}")
    private String bucketName;
    
    public EventService(S3Client s3Client, EventRepository repository, AddressService addressService) {
        this.s3Client = s3Client;
        this.repository = repository;
        this.addressService = addressService;
    }
    
    public Event createEvent(EventRequestDTO data) 
    {
        String imgUrl = "default-image.jpg";
        if (data.getImage() != null && !data.getImage().isEmpty()) 
        {
           imgUrl = this.uploadImg(data.getImage());
        }
        
        Event newEvent = new Event();
        newEvent.setTitle(data.getTitle());
        newEvent.setDescription(data.getDescription());
        
        // Converter Long timestamp para Date
        if (data.getDate() != null) 
        {
            newEvent.setDate(new Date(data.getDate()));
        } 
        else 
        {
            newEvent.setDate(new Date()); // Data atual como fallback
        }
        
        newEvent.setEventUrl(data.getEventUrl());
        newEvent.setImgUrl(imgUrl);
        newEvent.setRemote(data.getRemote());
        
        repository.save(newEvent);
        
        if (!data.getRemote()) 
        {
            
            this.addressService.createAddress(
                data.getCity(), 
                data.getState(), 
                newEvent
            );
        }
        return newEvent;
    }
    
    private String uploadImg(MultipartFile multipartFile) 
    {
        if (s3Client == null) 
        {
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
        Page<Event> eventsPage = this.repository.findUpcomingEvents
        (
                new Date(), 
                pageable
        );

        return eventsPage.map(event -> new EventResponseDTO(
            event.getId(),
            (event.getAddress() != null) ? event.getAddress().getCity() : "",
            (event.getAddress() != null) ? event.getAddress().getUf() : "",
            event.getTitle(), 
            event.getDescription(), 
            event.getDate(), 
            event.getEventUrl(), 
            event.getImgUrl(), 
            event.getRemote()
        )).getContent();
    }
    
    public List<EventResponseDTO> getFilteredEvents(int page, int size, String title, String city, String uf, Date startDate, Date endDate) 
    {
        Pageable pageable = PageRequest.of(page, size);
        title = (title != null) ? title : ""; 
        city = (city != null) ? city : ""; 
        uf = (uf != null) ? uf : ""; 

        // Se startDate for nulo, buscar desde uma data muito antiga
        if (startDate == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(1900, Calendar.JANUARY, 1); // Data muito antiga
            startDate = calendar.getTime();
        }

        if (endDate == null) {
            // Data muito futura
            Calendar calendar = Calendar.getInstance();
            calendar.set(2100, Calendar.DECEMBER, 31);
            endDate = calendar.getTime();
        }

        Page<Event> eventsPage = this.repository.findFilteredEvents(title, city, uf, startDate, endDate, pageable);

        return eventsPage.map(event -> new EventResponseDTO(
            event.getId(),
            (event.getAddress() != null) ? event.getAddress().getCity() : "",
            (event.getAddress() != null) ? event.getAddress().getUf() : "",
            event.getTitle(), 
            event.getDescription(), 
            event.getDate(),
            event.getEventUrl(), 
            event.getImgUrl(), 
            event.getRemote()
        )).getContent();
    }
    public EventDetailsDTO getEventDetails(UUID eventId)
    {
        Event event = repository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Event not find!"));
        List<Coupon> coupons = couponService.consultCoupons(eventId, new Date());
        List<EventDetailsDTO.CouponDTO> couponDTOs = coupons.stream().map(coupon -> new EventDetailsDTO.CouponDTO(
                coupon.getCode(),
                coupon.getDiscount(),
                coupon.getValid()))
                .collect(Collectors.toList());
        return new EventDetailsDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                (event.getAddress() != null) ? event.getAddress().getCity() : "",
                (event.getAddress() != null) ? event.getAddress().getUf() : "",
                event.getImgUrl(),
                event.getEventUrl(),
                couponDTOs
        );
    }
}