package com.util.codegenerate;

import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.stereotype.Controller
public class Controller {
    @RequestMapping({"/", "/app/**"})
    public String index() {
        return "forward:/index.html";
    }
}
