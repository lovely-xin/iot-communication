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

package com.github.xingshuangs.iot.protocol.rtcp.service;


import com.github.xingshuangs.iot.exceptions.SocketRuntimeException;
import com.github.xingshuangs.iot.net.client.UdpClientBasic;
import com.github.xingshuangs.iot.protocol.rtcp.model.RtcpBasePackage;
import com.github.xingshuangs.iot.protocol.rtcp.model.RtcpPackageBuilder;
import com.github.xingshuangs.iot.protocol.rtp.model.RtpPackage;
import com.github.xingshuangs.iot.protocol.rtsp.service.IRtspDataStream;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Rtcp udp client.
 * (RTCP协议的UDP客户端)
 *
 * @author xingshuang
 */
@Slf4j
public class RtcpUdpClient extends UdpClientBasic implements IRtspDataStream {

    /**
     * Is thread terminal.
     * (是否终止线程)
     */
    private boolean terminal = false;

    /**
     * Rtcp data statistics object.
     * (RTP和RTCP的数据统计)
     */
    private final RtcpDataStatistics statistics = new RtcpDataStatistics();

    /**
     * Communicate callback by bytes.
     * (数据收发前自定义处理接口)
     */
    private Consumer<byte[]> commCallback;

    /**
     * Completable future object.
     * (异步执行对象)
     */
    private CompletableFuture<Void> future;

    /**
     * Executor service.
     * (线程池执行服务，单线程)
     */
    private final ExecutorService executorService;

    public void setCommCallback(Consumer<byte[]> commCallback) {
        this.commCallback = commCallback;
    }


    public RtcpUdpClient() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public RtcpUdpClient(String ip, int port) {
        super(ip, port);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public CompletableFuture<Void> getFuture() {
        return future;
    }

    @Override
    public void close() {
        this.executorService.shutdown();
        if (!this.terminal) {
            // 发送byte
            byte[] receiverAndByteContent = this.statistics.createReceiverAndByteContent();
            this.sendData(receiverAndByteContent);
            this.terminal = true;
        }
        super.close();
    }

    /**
     * Trigger receive handler.
     * (触发接收)
     */
    @Override
    public void triggerReceive() {
        this.future = CompletableFuture.runAsync(this::waitForReceiveData, this.executorService);
    }

    @Override
    public void sendData(byte[] data) {
        if (this.commCallback != null) {
            this.commCallback.accept(data);
        }
        this.write(data);
    }

    private void waitForReceiveData() {
        log.debug("[RTSP + UDP] RTCP enables asynchronous data receiving thread, remote IP[/{}:{}]",
                this.serverAddress.getAddress().getHostAddress(), this.serverAddress.getPort());
        while (!this.terminal) {
            try {
                byte[] data = this.read();
                if (this.commCallback != null) {
                    this.commCallback.accept(data);
                }
                List<RtcpBasePackage> basePackages = RtcpPackageBuilder.fromBytes(data);
                this.statistics.processRtcpPackage(basePackages);
            } catch (SocketRuntimeException e) {
                // SocketRuntimeException就是IO异常，网络断开了，结束线程
                log.error(e.getMessage());
                this.terminal = true;
                break;
            } catch (Exception e) {
                if (!this.terminal) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        log.debug("[RTSP + UDP] RTCP closes asynchronous receiving thread, remote IP[/{}:{}]",
                this.serverAddress.getAddress().getHostAddress(), this.serverAddress.getPort());
    }

    /**
     * Process rtp package.
     * (处理RTP的数据包)
     *
     * @param rtpPackage RTP package
     */
    public void processRtpPackage(RtpPackage rtpPackage) {
        this.statistics.processRtpPackage(rtpPackage, this::sendData);
    }
}
