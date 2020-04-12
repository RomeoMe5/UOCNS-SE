package ru.stepanov.uocns.common.exceptions;

public class CommonException extends Exception {

    private final String details;

    public CommonException(String message){
        super(message);
        details = null;
    }

    public CommonException(String message, String details){
        super(message);
        this.details = details;
    }

    public String staticMessage(){
        return "error";
    }
}
