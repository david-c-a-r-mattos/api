package com.eventostec.api.config;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.URI;

@Configuration
public class AWSConfig 
{
    @Value("${aws.region:us-east-2}")
    private String awsRegion;
    
    @Value("${aws.s3.endpoint:https://s3.us-east-2.amazonaws.com}")
    private String endpoint;
    
    @Bean
    public S3Client s3Client()
    {
        return S3Client.builder()
            .region(Region.of(awsRegion))
            .endpointOverride(URI.create(endpoint)) // ENDPOINT EXPLÍCITO
            .credentialsProvider(DefaultCredentialsProvider.create())
            .serviceConfiguration(S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build())
            .build();
    }
}