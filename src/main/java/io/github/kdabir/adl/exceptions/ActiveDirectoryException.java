package io.github.kdabir.adl.exceptions;

import java.io.IOException;

/**
 * This is generic exception and underlying exception is wrapped in it
 * 
 * @author kdabir
 */
public class ActiveDirectoryException extends IOException {

    public ActiveDirectoryException(String msg) {super(msg);}
    public ActiveDirectoryException(Throwable cause) {
        super ("Problem accessing the Active Directory", cause);
    }
    public ActiveDirectoryException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
