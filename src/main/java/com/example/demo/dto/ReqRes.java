package com.example.demo.dto;

import com.example.demo.entity.OurUsers;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRes {

    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String city;
    private String role;
    private String email;
    private String password;
    private UserDTO ourUsers;
    private List<UserDTO> ourUsersList;
    
    // Método para convertir OurUsers a UserDTO
    public void setOurUsers(OurUsers user) {
        if (user != null) {
            this.ourUsers = new UserDTO(user);
        }
    }
    
    public void setOurUsersList(List<OurUsers> users) {
        if (users != null) {
            this.ourUsersList = users.stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
        }
    }
    
    // Clase interna para representar usuarios sin problemas de serialización
    @Data
    public static class UserDTO {
        private Integer id;
        private String email;
        private String name;
        private String city;
        private String role;
        
        public UserDTO(OurUsers user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.name = user.getName();
            this.city = user.getCity();
            this.role = user.getRole();
        }
    }
}
