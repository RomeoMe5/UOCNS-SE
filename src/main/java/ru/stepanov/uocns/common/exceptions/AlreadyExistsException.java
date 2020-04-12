package ru.stepanov.uocns.common.exceptions;

public class AlreadyExistsException extends CommonException {

    public AlreadyExistsException() {
        super("Объект существует.");
    }

    @Override
    public String staticMessage(){
        return "object_already_exists";
    }
}
