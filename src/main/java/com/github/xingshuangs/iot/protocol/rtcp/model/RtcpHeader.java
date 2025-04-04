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

package com.github.xingshuangs.iot.protocol.rtcp.model;


import com.github.xingshuangs.iot.common.IObjectByteArray;
import com.github.xingshuangs.iot.common.buff.ByteReadBuff;
import com.github.xingshuangs.iot.common.buff.ByteWriteBuff;
import com.github.xingshuangs.iot.protocol.rtcp.enums.ERtcpPackageType;
import com.github.xingshuangs.iot.utils.BooleanUtil;
import lombok.Data;

/**
 * Header
 *
 * @author xingshuang
 */
@Data
public class RtcpHeader implements IObjectByteArray {

    /**
     * Version.
     * (版本（V）：2比特)
     */
    protected int version;

    /**
     * Padding.
     * (填充（P）：1比特，如果该位置为1，则该RTCP包的尾部就包含附加的填充字节。)
     */
    protected boolean padding;

    /**
     * Receive report counter
     * (接收报告计数器（RC）：5比特，该SR包中的接收报告块的数目，可以为零。)
     */
    protected int receptionCount;

    /**
     * Package type.
     * (包类型（PT）：8比特，SR包是200。)
     */
    protected ERtcpPackageType packageType;

    /**
     * Length.
     * (长度域（Length）：16比特，RTCP包的长度, 其中存放的是该SR包以32比特为单位的总长度减一, 包括填充的内容。长度代表整个数据包的大小（协议头+荷载+填充）)
     * length = 32/4-1=7
     */
    protected int length;

    @Override
    public int byteArrayLength() {
        return 4;
    }

    @Override
    public byte[] toByteArray() {
        byte res = (byte) (((this.version << 6) & 0xC0)
                | BooleanUtil.setBit(5, this.padding)
                | (this.receptionCount & 0x0F));
        return ByteWriteBuff.newInstance(4)
                .putByte(res)
                .putByte(this.packageType.getCode())
                .putShort(this.length)
                .getData();
    }

    /**
     * Parses byte array and converts it to object.
     *
     * @param data byte array
     * @return RtcpHeader
     */
    public static RtcpHeader fromBytes(final byte[] data) {
        return fromBytes(data, 0);
    }

    /**
     * Parses byte array and converts it to object.
     *
     * @param data   byte array
     * @param offset index offset
     * @return RtcpHeader
     */
    public static RtcpHeader fromBytes(final byte[] data, final int offset) {
        if (data.length < 4) {
            throw new IndexOutOfBoundsException("header, data length < 4");
        }
        ByteReadBuff buff = new ByteReadBuff(data, offset);
        RtcpHeader res = new RtcpHeader();
        byte aByte = buff.getByte();
        res.version = (aByte >> 6) & 0x03;
        res.padding = BooleanUtil.getValue(aByte, 5);
        res.receptionCount = aByte & 0x1F;
        res.packageType = ERtcpPackageType.from(buff.getByte());
        res.length = buff.getUInt16();
        return res;
    }
}
