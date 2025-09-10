package com.eventostec.api.domain.event;

import org.springframework.web.multipart.MultipartFile;

public class EventRequestDTO 
{
    private String title;
    private String description;
    private Long date;
    private String city;
    private String state;
    private Boolean remote;
    private String eventUrl;
    private MultipartFile image;
    
    // Construtor padrão obrigatório
    public EventRequestDTO() {}

    public EventRequestDTO(String title, String description, Long date, String city, String state, Boolean remote, String eventUrl, MultipartFile image) 
    {
        this.title = title;
        this.description = description;
        this.date = date;
        this.city = city;
        this.state = state;
        this.remote = remote;
        this.eventUrl = eventUrl;
        this.image = image;
    }
  
    
    // Getters e Setters obrigatórios
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getDate() { return date; }
    public void setDate(Long date) { this.date = date; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public Boolean getRemote() { return remote; }
    public void setRemote(Boolean remote) { this.remote = remote; }
    
    public String getEventUrl() { return eventUrl; }
    public void setEventUrl(String eventUrl) { this.eventUrl = eventUrl; }
    
    public MultipartFile getImage() { return image; }
    public void setImage(MultipartFile image) { this.image = image; }
}