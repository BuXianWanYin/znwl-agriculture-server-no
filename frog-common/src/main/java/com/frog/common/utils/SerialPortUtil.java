package com.frog.common.utils;/*
 * @author 不羡晚吟
 * @version 1.0
 */
import com.fazecast.jSerialComm.SerialPort;
import java.util.Arrays;

public class SerialPortUtil {
    private SerialPort serialPort;

    public SerialPortUtil() {
        // 配置串口：下面以 COM3 为例，波特率 9600
        serialPort = SerialPort.getCommPort("COM3");
        serialPort.setBaudRate(9600);
        serialPort.setNumDataBits(8);
        serialPort.setParity(SerialPort.NO_PARITY);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);
        if (!serialPort.openPort()) {
            throw new RuntimeException("打开串口失败");
        }
    }

    public synchronized void writeBytes(byte[] data) {
        serialPort.writeBytes(data, data.length);
        System.out.println("Sent: " + bytesToHex(data));
    }

    public synchronized byte[] readBytes() {
        byte[] buffer = new byte[256];
        int numRead = serialPort.readBytes(buffer, buffer.length);
        byte[] result = Arrays.copyOf(buffer, numRead);
        System.out.println("Received (Hex): " + bytesToHex(result));
        return result;
    }

    public void close() {
        if (serialPort != null) {
            serialPort.closePort();
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}
