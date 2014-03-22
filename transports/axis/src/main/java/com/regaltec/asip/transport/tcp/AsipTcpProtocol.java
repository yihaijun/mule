package com.regaltec.asip.transport.tcp;

import java.io.IOException;
import java.io.InputStream;

import org.mule.transport.tcp.protocols.AbstractByteProtocol;

/**
 * 
 * <p>接口平台Tcp协议扩展类，业务需要实现自定义协议时继承该类，而不需要继承任何Mule相关的类</p>
 * <p>创建日期：2010-10-12 下午03:13:58</p>
 *
 * @author 封加华
 */
public abstract class AsipTcpProtocol extends AbstractByteProtocol {
    /**
     * 
     * 构造函数
     */
    public AsipTcpProtocol() {
        super(STREAM_OK);
    }
    /**
     * 
     * 构造函数 
     * @param streamOk 
     */
    public AsipTcpProtocol(boolean streamOk) {
        super(streamOk);
    }
    /**
     * 
     * {@inheritDoc}
     */
    public abstract Object read(InputStream stream) throws IOException;

}
