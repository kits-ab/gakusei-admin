package se.kits.gakusei.gakuseiadmin.utility;

public class ParserFailureException extends RuntimeException {

    public ParserFailureException() {}

    public ParserFailureException(String message){
        super(message);
    }
}
