package com.example.demo.controllers;


import com.example.demo.models.Trailer;
import com.example.demo.repo.TrailerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/json/data")
public class JsonController {

@Autowired
private TrailerRepository trailerRepository;


    @RequestMapping("/arr/trailers")
    @ResponseBody
    public List<Trailer> getArrTrailers(){
        return trailerRepository.findByOutTrcIsNull(Sort.by("inDate").ascending());


    }

    @RequestMapping("/dep/trailers")
    @ResponseBody
    public List<Trailer> getDepTrailers(){
        return trailerRepository.findTop100ByOutTrcNotNull(Sort.by("outDate").descending());


    }



}
