package com.huerto.hogar.frontend.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final FrontendSession session;

    public GlobalModelAttributes(FrontendSession session) {
        this.session = session;
    }

    @ModelAttribute("session")
    public FrontendSession session() {
        return session;
    }
}
