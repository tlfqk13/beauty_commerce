package com.example.sampleroad.controller;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class CertificationController {

    @Value("${shop-by.client-id}")
    String clientId;

    @GetMapping("/certificate")
    public String certificate(Model model){
        model.addAttribute("clientId", clientId);
        return "certificate";
    }

    @GetMapping("/certificate/callback")
    @ResponseBody
    public ResponseEntity<?> certificateCallback(HttpServletRequest request) throws UnirestException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json;charset=UTF-8");
        String key = request.getParameter("key");
        HttpResponse<String> response = Unirest.get("https://shop-api.e-ncp.com/kcp/id-verification/response?key="+key)
                .header("version", "1.0")
                .header("clientid", clientId)
                .header("platform", "PC")
                .asString();
        if(response.getStatus()!=200){
            throw new ErrorCustomException(ErrorCode.NO_CERTIFICATION_ERROR);
        }
        return new ResponseEntity<>(response.getBody(),httpHeaders,HttpStatus.OK);
    }
}