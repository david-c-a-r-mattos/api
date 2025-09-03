package com.eventostec.api.domain.event;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDTO 
{
    private String title;
    private String description;
    private Long date;
    private String city;
    private String state;
    private Boolean remote;
    private String eventUrl;
    private MultipartFile imageUrl;
}