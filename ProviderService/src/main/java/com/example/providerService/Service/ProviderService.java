package com.example.providerService.Service;

import com.example.providerService.Components.ProviderMapper;
import com.example.providerService.DTO.ProviderDTO;
import com.example.providerService.Model.Provider;
import com.example.providerService.Repository.ProviderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProviderService {
    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;

    public List<ProviderDTO> getAllProviders() {
        List<Provider> providers = providerRepository.findAll();
        return providerMapper.toDTOList(providers);
    }

    public ProviderService(ProviderRepository providerRepository, ProviderMapper providerMapper) {
        this.providerRepository = providerRepository;
        this.providerMapper = providerMapper;
    }

    public void saveUser(ProviderDTO newUser){
        newUser.setStatus(true);
        providerRepository.save(providerMapper.fromDTO(newUser));
    }

    public ProviderDTO findUserById(Long id){
        Provider user = providerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Поставщика не существует, id = " + id));
        return providerMapper.toDTO(user);
    }

    public ProviderDTO editUser(ProviderDTO newUser, Long id){
        newUser.setId(id);
        return providerMapper.toDTO(providerRepository.save(providerMapper.fromDTO(newUser)));
    }

    public void deleteUserById(Long id){
        Optional<Provider> optionalProvider = providerRepository.findById(id);
        if (optionalProvider.isPresent()) {
            Provider provider = optionalProvider.get();
            provider.setStatus(false);
            providerRepository.save(provider);
        }
    }
}

