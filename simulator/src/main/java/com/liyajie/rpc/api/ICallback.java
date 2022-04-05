package com.liyajie.rpc.api;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;

/**
 * @author Liyajie
 */
public interface ICallback {
    /**
     * 回调方法
     *
     * @param body soap的body
     */
    void run(SOAPMessage body) throws Exception;
}
