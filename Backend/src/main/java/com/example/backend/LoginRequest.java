package com.example.backend;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    
    @NotBlank
    private String ownerName;

    @NotBlank
    private String password;
    
    //--GETTERS--

    public String getOwnerName(){return ownerName;}
    public String getPassword() {return password;}

    //--SETTERS--

    public void setOwnerName(String ownerName) {this.ownerName = ownerName;}
    public void setPassword(String password) {this.password = password;}
}

