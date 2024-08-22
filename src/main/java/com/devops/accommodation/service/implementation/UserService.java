package com.devops.accommodation.service.implementation;

import com.devops.accommodation.repository.UserRepository;
import com.devops.accommodation.service.interfaces.IUserService;
import com.devops.accommodation.utils.Constants;
import ftn.devops.db.User;
import ftn.devops.log.LogType;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Enumeration;

@Service
public class UserService implements IUserService {
    @Autowired
    private LogClientService logClientService;
    @Autowired
    private UserRepository hostRepository;

    @Value("${devops.auth.url}")
    private String authUrl;

    @Override
    public User findById(long id) {
        return hostRepository.findById(id)
                .orElseThrow(() -> {
                    logClientService.sendLog(LogType.WARN, "Host not found", id);
                    throw new EntityNotFoundException(Constants.HOST_NOT_FOUND);
                });
    }

    @Override
    public User getUser(HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.add(headerName, headerValue);
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Long> result =
                restTemplate.exchange(authUrl, HttpMethod.GET, entity, Long.class);
        return findById(result.getBody());
    }
}
