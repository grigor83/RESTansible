package mtel;

import jakarta.validation.constraints.NotBlank;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(@NotBlank String s) {
    }
}
