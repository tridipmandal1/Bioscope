package com.bioscope.backend.v01.models;

import lombok.Data;

import java.util.Map;

@Data
public class EmailModel {

    private String to;
    private String subject;
    private String template;
    private Map<String, Object> variables;
}