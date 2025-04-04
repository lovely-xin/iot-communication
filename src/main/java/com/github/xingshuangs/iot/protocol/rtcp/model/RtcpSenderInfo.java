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
import com.github.xingshuangs.iot.utils.TimesUtil;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 发送者信息
 *
 * @author xingshuang
 */
@Data
public class RtcpSenderInfo implements IObjectByteArray {

    /**
     * High timestamp.
     * NTP时间包括两部分组成，高位32bit表示秒钟：从1970开始计算
     */
    private long mswTimestamp;

    /**
     * Low timestamp.
     * 低位表示剩余时间精度，一般按照1代表232皮秒来计算。
     */
    private long lswTimestamp;

    /**
     * NTP timestamp.
     * NTP时间（64bit）：NTP时间包括两部分组成，高位32bit表示秒钟：从1970开始计算；低位表示剩余时间精度，一般按照1代表232皮秒来计算。
     */
    private LocalDateTime ntpTimestamp;

    /**
     * Rtp timestamp.
     * RTP时间戳（32bit）：与RTP时间戳计算方式一致，是根据采样率进行递增，由于与RTP时间戳一致同时又知道当前的NTP时间，因此可以用于音视频时间同步使用。
     */
    private LocalDateTime rtpTimestamp;

    /**
     * Sender packet count.
     * 发送包数量（32bit）：计算已经发送的包的数量
     * 从开始发送包到产生这个SR包这段时间里，发送者发送的RTP数据包的总数. SSRC改变时，这个域清零。
     */
    private long senderPacketCount;

    /**
     * Sender octet count.
     * 发送字节数（32bit）：计算已经发送的字节数量
     * 从开始发送包到产生这个SR包这段时间里，发送者发送的净荷数据的总字节数（不包括头部和填充）。发送者改变其SSRC时，这个域要清零。
     */
    private long senderOctetCount;

    @Override
    public int byteArrayLength() {
        return 20;
    }

    @Override
    public byte[] toByteArray() {
        return ByteWriteBuff.newInstance(20)
                .putInteger(this.mswTimestamp)
                .putInteger(this.lswTimestamp)
                .putInteger(TimesUtil.getNTPTotalSecond(this.rtpTimestamp))
                .putInteger(this.senderPacketCount)
                .putInteger(this.senderOctetCount)
                .getData();
    }

    /**
     * Parses byte array and converts it to object.
     *
     * @param data byte array
     * @return RtcpHeader
     */
    public static RtcpSenderInfo fromBytes(final byte[] data) {
        return fromBytes(data, 0);
    }

    /**
     * Parses byte array and converts it to object.
     *
     * @param data   byte array
     * @param offset index offset
     * @return RtcpHeader
     */
    public static RtcpSenderInfo fromBytes(final byte[] data, final int offset) {
        if (data.length < 20) {
            throw new IndexOutOfBoundsException("RtcpSenderInfo, data length < 20");
        }
        ByteReadBuff buff = new ByteReadBuff(data, offset);
        RtcpSenderInfo res = new RtcpSenderInfo();
        res.mswTimestamp = buff.getUInt32();
        res.lswTimestamp = buff.getUInt32();
        res.ntpTimestamp = TimesUtil.getNTPDateTime(res.mswTimestamp);
        res.ntpTimestamp = res.ntpTimestamp.plusNanos(res.lswTimestamp * 232 / 1000);
        res.rtpTimestamp = TimesUtil.getNTPDateTime(buff.getUInt32());
        res.senderPacketCount = buff.getUInt32();
        res.senderOctetCount = buff.getUInt32();
        return res;
    }
}
