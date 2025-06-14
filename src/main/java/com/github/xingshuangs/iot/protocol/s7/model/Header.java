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

package com.github.xingshuangs.iot.protocol.s7.model;


import com.github.xingshuangs.iot.common.IObjectByteArray;
import com.github.xingshuangs.iot.common.buff.ByteReadBuff;
import com.github.xingshuangs.iot.common.buff.ByteWriteBuff;
import com.github.xingshuangs.iot.protocol.s7.enums.EMessageType;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * S7 header.
 * 头部
 *
 * @author xingshuang
 */
@Data
public class Header implements IObjectByteArray {

    private static final AtomicInteger index = new AtomicInteger();

    public static final int BYTE_LENGTH = 10;

    /**
     * Protocol id.
     * 协议id <br>
     * 字节大小：1 <br>
     * 字节序数：0
     */
    protected byte protocolId = (byte) 0x32;

    /**
     * Message type.
     * pdu（协议数据单元（Protocol Data Unit））的类型 <br>
     * 字节大小：1 <br>
     * 字节序数：1
     */
    protected EMessageType messageType = EMessageType.JOB;

    /**
     * Reserved.
     * 保留 <br>
     * 字节大小：2 <br>
     * 字节序数：2-3
     */
    protected int reserved = 0x0000;

    /**
     * Pdu reference, incremental with each new transmission, big-endian.
     * pdu的参考–由主站生成，每次新传输递增，大端 <br>
     * 字节大小：2 <br>
     * 字节序数：4-5
     */
    protected int pduReference = 0x0000;

    /**
     * Parameter length.
     * 参数的长度（大端） <br>
     * 字节大小：2 <br>
     * 字节序数：6-7
     */
    protected int parameterLength = 0x0000;

    /**
     * Data length.
     * 数据的长度（大端） <br>
     * 字节大小：2 <br>
     * 字节序数：8-9
     */
    protected int dataLength = 0x0000;

    /**
     * Create new pdu number.
     * (获取新的pduNumber)
     *
     * @return new number
     */
    public static int getNewPduNumber() {
        int res = index.getAndIncrement();
        if (res >= 65536) {
            index.set(0);
            res = 0;
        }
        return res;
    }

    @Override
    public int byteArrayLength() {
        return BYTE_LENGTH;
    }

    @Override
    public byte[] toByteArray() {
        return ByteWriteBuff.newInstance(BYTE_LENGTH)
                .putByte(this.protocolId)
                .putByte(this.messageType.getCode())
                .putShort(this.reserved)
                .putShort(this.pduReference)
                .putShort(this.parameterLength)
                .putShort(this.dataLength)
                .getData();
    }

    /**
     * Parses byte array and converts it to object.
     *
     * @param data byte array
     * @return Header
     */
    public static Header fromBytes(final byte[] data) {
        if (data.length < BYTE_LENGTH) {
            throw new IndexOutOfBoundsException("header, data length < 10");
        }
        ByteReadBuff buff = new ByteReadBuff(data);
        Header header = new Header();
        header.protocolId = buff.getByte();
        header.messageType = EMessageType.from(buff.getByte());
        header.reserved = buff.getUInt16();
        header.pduReference = buff.getUInt16();
        header.parameterLength = buff.getUInt16();
        header.dataLength = buff.getUInt16();
        return header;
    }

    /**
     * Create default header.
     * (创建默认的头header)
     *
     * @return Header
     */
    public static Header createDefault() {
        Header header = new Header();
        header.protocolId = (byte) 0x32;
        header.messageType = EMessageType.JOB;
        header.reserved = 0x0000;
        header.pduReference = getNewPduNumber();
        header.parameterLength = 0;
        header.dataLength = 0;
        return header;
    }
}
