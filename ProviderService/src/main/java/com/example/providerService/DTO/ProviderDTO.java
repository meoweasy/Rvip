package com.example.providerService.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ProviderDTO {
    @Id
    @Schema(hidden = true)
    private Long id;
    private String name;
    private String surname;
    @Schema(hidden = true)
    private boolean status;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getStatus(){return status;}
    public void setStatus(Boolean status){this.status=status;}

}
