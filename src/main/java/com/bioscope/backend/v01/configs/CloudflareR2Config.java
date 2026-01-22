package com.bioscope.backend.v01.configs;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(R2Properties.class)
public class CloudflareR2Config {

    private final R2Properties r2Properties;

    @Bean
    public S3Client s3Client() {




        return S3Client.builder()
                .httpClientBuilder(ApacheHttpClient.builder())
                .region(Region.of("auto"))
                .endpointOverride(URI.create(r2Properties.getEndpoint()))
                .credentialsProvider(credentialsProvider())
                .serviceConfiguration(s3Configuration())
                .build();

    }

    @Bean
    public S3Presigner s3Presigner(){
         return    S3Presigner.builder()
                    .endpointOverride(URI.create(r2Properties.getEndpoint()))
                    .credentialsProvider(credentialsProvider())
                    .region(Region.of("auto"))
                    .serviceConfiguration(s3Configuration())
                    .build();
    }



    private  StaticCredentialsProvider credentialsProvider() {
        return
                StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                r2Properties.getAccessKey(),
                                r2Properties.getSecretKey()
                        )
                );
    }

    private S3Configuration s3Configuration (){

        return
                S3Configuration.builder()

                        // path style required for R2
                        .pathStyleAccessEnabled(true)
                        // disable aws chunk uploads
                        .chunkedEncodingEnabled(false)
                        .build();

    }

}
