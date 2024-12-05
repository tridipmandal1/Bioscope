package com.bioscope.backend.v01.controllers;

import com.bioscope.backend.v01.services.iface.EventHostService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v01/host")
public class HostController {
    private final EventHostService eventHostService;

    public HostController(
        EventHostService eventHostService
    ){
        this.eventHostService = eventHostService;
    }

}
