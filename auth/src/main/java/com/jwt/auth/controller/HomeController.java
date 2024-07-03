package com.jwt.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/home")
public class HomeController {

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String homeRequest(){
        return "Inside HomeController";
    }
}
