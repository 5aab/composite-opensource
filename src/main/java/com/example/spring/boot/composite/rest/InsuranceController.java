package com.example.spring.boot.composite.rest;

import com.example.spring.boot.composite.domain.insurance.Insurance;
import com.example.spring.boot.composite.domain.insurance.InsuranceRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@AllArgsConstructor
public class InsuranceController {

    private InsuranceRepository insuranceRepository;

    @ResponseBody
    @GetMapping(value = "insurance/query",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Insurance> query() {
        return insuranceRepository.findAll();
    }


}
