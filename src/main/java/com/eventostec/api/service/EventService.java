package com.eventostec.api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.eventostec.api.domain.event.Event;
import com.eventostec.api.domain.event.EventRequestDTO;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.io.FileOutputStream;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EventService 
{
    @Autowired
    private AmazonS3 s3Client;
    @Value("${aws.bucket.name}")
    private String bucketName;
    public Event createEvent(EventRequestDTO data)
    {
        String imgURL = null;
        if(data.getImage() != null)
        {
           imgURL =  this.uploadImg(data.getImage());
        }
        Event newEvent = new Event();
        newEvent.setTitle(data.getTitle());
        newEvent.setDescription(data.getDescription());
        newEvent.setDate(new Date(data.getDate()));
        newEvent.setEventUrl(data.getEventUrl());
        newEvent.setImgUrl(imgURL);
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
        catch(Exception e)
        {
            System.out.println("Falha ao fazer upload do arquivo");
            return null;
        }
    }
    private File convertMultipartToFile(MultipartFile multipartFile) throws IOException
    {
        File convFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        return convFile;
    }
}
