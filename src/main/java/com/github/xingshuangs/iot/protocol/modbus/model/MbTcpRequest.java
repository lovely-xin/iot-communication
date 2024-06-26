/*
 * MIT License
 *
 * Copyright (c) 2021-2099 Oscura (xingshuang) <xingshuang_cool@163.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.xingshuangs.iot.protocol.modbus.model;


import com.github.xingshuangs.iot.exceptions.ModbusCommException;
import com.github.xingshuangs.iot.common.IObjectByteArray;
import com.github.xingshuangs.iot.common.buff.ByteWriteBuff;
import lombok.Data;

/**
 * TCP的modbus请求
 *
 * @author xingshuang
 */
@Data
public class MbTcpRequest implements IObjectByteArray {

    /**
     * 报文头， 报文头为 7 个字节长
     */
    private MbapHeader header;

    /**
     * 协议数据单元
     */
    private MbPdu pdu;

    @Override
    public int byteArrayLength() {
        return this.header.byteArrayLength() + this.pdu.byteArrayLength();
    }

    @Override
    public byte[] toByteArray() {
        return ByteWriteBuff.newInstance(this.byteArrayLength())
                .putBytes(this.header.toByteArray())
                .putBytes(this.pdu.toByteArray())
                .getData();
    }

    /**
     * 自我数据校验
     */
    public void selfCheck() {
        if (this.header == null) {
            throw new ModbusCommException("header is null");
        }
        if (this.pdu == null) {
            throw new ModbusCommException("pdu is null");
        }
        this.header.setLength(this.pdu.byteArrayLength() + 1);
    }

    /**
     * 创建默认请求对象
     *
     * @return MbTcpRequest
     */
    public static MbTcpRequest createDefault() {
        MbTcpRequest request = new MbTcpRequest();
        request.header = new MbapHeader(MbapHeader.getNewNumber());
        return request;
    }
}
