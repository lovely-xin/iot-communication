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

package com.github.xingshuangs.iot.protocol.melsec.model;


import com.github.xingshuangs.iot.common.buff.ByteWriteBuff;
import com.github.xingshuangs.iot.exceptions.McCommException;
import com.github.xingshuangs.iot.protocol.melsec.enums.EMcCommand;
import com.github.xingshuangs.iot.protocol.melsec.enums.EMcFrameType;
import com.github.xingshuangs.iot.protocol.melsec.enums.EMcSeries;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Device access request data, batch write multi blocks.
 * 软元件访问多块读请求数据
 *
 * @author xingshuang
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class McWriteDeviceBatchMultiBlocksReqData extends McReqData {

    /**
     * Word content list.
     * (软元件设备地址，字访问地址列表)
     */
    private List<McDeviceContent> wordContents;

    /**
     * Bit content list.
     * (软元件设备地址，位访问地址列表）
     */
    private List<McDeviceContent> bitContents;

    public McWriteDeviceBatchMultiBlocksReqData() {
        this(EMcSeries.Q_L, new ArrayList<>(), new ArrayList<>());
    }

    public McWriteDeviceBatchMultiBlocksReqData(EMcSeries series) {
        this(series, new ArrayList<>(), new ArrayList<>());
    }

    public McWriteDeviceBatchMultiBlocksReqData(EMcSeries series,
                                                List<McDeviceContent> wordContents,
                                                List<McDeviceContent> bitContents) {
        if (series.getFrameType() == EMcFrameType.FRAME_1E) {
            throw new McCommException("Frame 1E not supported");
        }
        this.series = series;
        this.command = EMcCommand.DEVICE_ACCESS_BATCH_WRITE_MULTIPLE_BLOCKS;
        this.subcommand = series != EMcSeries.IQ_R ? 0x0000 : 0x0002;
        this.wordContents = wordContents;
        this.bitContents = bitContents;
    }

    @Override
    public int byteArrayLength() {
        return 4 + 2 + this.wordContents.stream().mapToInt(x -> x.byteArrayLengthWithPointsCount(this.series)).sum()
                + this.bitContents.stream().mapToInt(x -> x.byteArrayLengthWithPointsCount(this.series)).sum();
    }

    @Override
    public byte[] toByteArray() {
        ByteWriteBuff buff = ByteWriteBuff.newInstance(this.byteArrayLength(), true)
                .putShort(this.command.getCode())
                .putShort(this.subcommand)
                .putByte(this.wordContents.size())
                .putByte(this.bitContents.size());
        this.wordContents.forEach(x -> buff.putBytes(x.toByteArrayWithPointsCount(this.series)));
        this.bitContents.forEach(x -> buff.putBytes(x.toByteArrayWithPointsCount(this.series)));
        return buff.getData();
    }
}
