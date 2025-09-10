package com.eventostec.api.domain.event;

import java.util.Date;
import java.util.UUID;

public class EventResponseDTO 
{
    private UUID Id;
    private String title;
    private String description;
    private Date date;
    private String city;
    private String state;
    private Boolean remote;
    private String eventUrl;
    private String imageUrl;
    
    public EventResponseDTO(){}
    public EventResponseDTO(UUID Id, String city, String state, String title, String description, Date date, String eventUrl, String imgUrl, Boolean remote) 
    {
        this.Id = Id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.city = city;
        this.state = state;
        this.remote = remote;
        this.eventUrl = eventUrl;
        this.imageUrl = imageUrl;
    }

    public UUID getId() {
        return Id;
    }

    public void setId(UUID Id) {
        this.Id = Id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getRemote() {
        return remote;
    }

    public void setRemote(Boolean remote) {
        this.remote = remote;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
}
    