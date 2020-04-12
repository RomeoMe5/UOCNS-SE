package ru.stepanov.uocns.common.exceptions;

public class NotAllowedException extends CommonException {

    public NotAllowedException() {
        super("Запрещено");
    }

    @Override
    public String staticMessage(){
        return "not_allowed_error";
    }
}