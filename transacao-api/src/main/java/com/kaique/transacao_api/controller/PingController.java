package com.kaique.transacao_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class PingController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
