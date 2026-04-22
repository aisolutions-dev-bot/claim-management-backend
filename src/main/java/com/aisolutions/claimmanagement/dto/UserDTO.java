package com.aisolutions.claimmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String staffId;
    private String secLoginId;
    private boolean secChangePassword;
}
