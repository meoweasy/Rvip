package com.example.providerService.Controller;

import com.example.providerService.DTO.ProviderDTO;
import com.example.providerService.Repository.ProviderRepository;
import com.example.providerService.Service.ProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.env.Environment;

import java.util.List;

@RestController
@RequestMapping("/provider_view")
public class ProviderController {
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private ProviderService providerService;

    private final String reportContainer;
    private  final String port;
    private static final Logger log = LoggerFactory.getLogger(ProviderController.class);

    public ProviderController(Environment environment){
        this.reportContainer = environment.getProperty("my.reportContainer");
        this.port = environment.getProperty("my.port");
    }

    @GetMapping("/getAllProviders")
    public ResponseEntity<List<ProviderDTO>> getAllProviders(){
        List<ProviderDTO> providers = providerService.getAllProviders();
        return ResponseEntity.ok(providers);
    }

    @PostMapping("/createProvider")
    public ResponseEntity<String> createProvider(@RequestBody ProviderDTO provider){
        providerService.saveUser(provider);
        return ResponseEntity.ok("Поставщик добавлен");
    }

    @GetMapping("/provider/{id}")
    public ResponseEntity<ProviderDTO> getProviderById(@PathVariable("id") Long id){
        ProviderDTO provider = providerService.findUserById(id);
        return ResponseEntity.ok(provider);
    }

    @PutMapping("/updateProvider/{id}")
    public ResponseEntity<ProviderDTO> updateProvider(@PathVariable("id") Long id,
                                              @RequestBody ProviderDTO provider){
        ProviderDTO updatedUser = providerService.editUser(provider, id);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/deleteProvider/{id}")
    public ResponseEntity<String> deleteProvider(@PathVariable("id") Long id){
        providerService.deleteUserById(id);
        return ResponseEntity.ok("Поставщик удален");
    }

    @GetMapping("/report")
    public String getActiveProviders(@RequestHeader("correlation-id") String corId) {
        try{
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("correlation-id", "corrid: %s /%s %s".formatted(corId, "provider_view", "report"));
            log.info("corrid: %s /%s %s".formatted(corId, "provider_view", "report"));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            return restTemplate.exchange("http://"+reportContainer+":"+port+"/report_providers", HttpMethod.GET, entity, String.class).getBody();
            //return restTemplate.getForObject("http://"+reportContainer+":"+port+"/report_providers", String.class);
        }catch (Exception ex){
            return ex.getMessage();
        }
    }

}
