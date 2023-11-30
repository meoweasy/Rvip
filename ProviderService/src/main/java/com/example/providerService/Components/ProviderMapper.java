package com.example.providerService.Components;

import com.example.providerService.DTO.ProviderDTO;
import com.example.providerService.Model.Provider;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProviderMapper  {
    ProviderDTO toDTO(Provider provider);
    Provider fromDTO(ProviderDTO providerDTO);
    List<ProviderDTO> toDTOList(List<Provider> providerList);
}
