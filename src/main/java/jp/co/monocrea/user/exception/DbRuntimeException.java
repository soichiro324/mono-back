package jp.co.monocrea.user.exception;

public class DbRuntimeException extends RuntimeException  {
    
    public DbRuntimeException(String message, Exception e) {
        super(message, e);
    }

}
