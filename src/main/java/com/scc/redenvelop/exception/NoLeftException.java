package com.scc.redenvelop.exception;

public class NoLeftException extends Exception {
    public NoLeftException() {
        super("手慢了，红包领完了");
    }

    public NoLeftException(String message) {
        super(message);
    }
}
