package it.unical.unijira.data.exceptions;

import lombok.Getter;
public class NonValidItemTypeException extends Exception {

    @Getter
    private String errorMessage;
    
    public NonValidItemTypeException(String errorMessage){
        this.errorMessage = errorMessage;
    }

}
