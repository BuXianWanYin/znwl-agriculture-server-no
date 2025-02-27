package com.frog.common.utils;

import com.fazecast.jSerialComm.SerialPort;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class SerialPortUtil {
    private SerialPort serialPort;
    private static final Log log = LogFactory.getLog(SerialPortUtil.class);

    public SerialPortUtil() {
        // 配置串口：下面以 COM3 为例，波特率 9600
        serialPort = SerialPort.getCommPort("COM4");
        serialPort.setBaudRate(9600);
        serialPort.setNumDataBits(8);
        serialPort.setParity(SerialPort.NO_PARITY);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);
        if (!serialPort.openPort()) {
            log.error(new RuntimeException("打开串口失败"));
        }
    }

    /**
     * 发送数据到串口
     */
    public synchronized void writeBytes(byte[] data) {
        serialPort.writeBytes(data, data.length);
        System.out.println("Sent: " + bytesToHex(data));
    }

    /**
     * 从串口读取数据
     */
    public synchronized byte[] readBytes() {
        byte[] buffer = new byte[256];
        int numRead = serialPort.readBytes(buffer, buffer.length);
        byte[] result = Arrays.copyOf(buffer, numRead);
        System.out.println("Received (Hex): " + bytesToHex(result));
        return result;
    }

    /**
     * 关闭串口
     */
    public void close() {
        if (serialPort != null) {
            serialPort.closePort();
        }
    }

    /**
     * 将字节数组转换为 16 进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

    /**
     * 将十六进制字符串转为字节数组（字符串可包含空格）
     */
    private byte[] hexStringToByteArray(String s) {
        s = s.replaceAll("\\s+", "");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 休眠方法（毫秒）
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    // 以下各方法为独立的发送命令方法，调用时直接执行相应方法即可

    /**
     * 发送全部打开命令
     */
    public void sendAllOpen() {
        writeBytes(hexStringToByteArray("1F 0F 00 00 00 04 01 FF FE 56"));
    }

    /**
     * 发送全部关闭命令
     */
    public void sendAllClose() {
        writeBytes(hexStringToByteArray("1F 0F 00 00 00 04 01 00 BE 16"));
//        sleep(200);
//        sendRelay4();
    }

    /**
     * 发送打开第1个继电器（亮红灯）的命令
     */
    public void sendRelay1() {
        writeBytes(hexStringToByteArray("1F 05 00 00 FF 00 8F 84"));
    }

    /**
     * 发送打开第2个继电器（电风扇）的命令
     */
    public void sendRelay2() {
        writeBytes(hexStringToByteArray("1F 05 00 01 FF 00 DE 44"));
    }

    /**
     * 发送打开第3个继电器（打开推杆）的命令
     */
    public void sendRelay3() {
        writeBytes(hexStringToByteArray("1F 05 00 02 FF 00 2E 44"));
    }

    /**
     * 发送打开第4个继电器（缩回推杆）的命令
     */
    public void sendRelay4() {
        writeBytes(hexStringToByteArray("1F 05 00 03 FF 00 7F 84"));
    }

    /**
     * 发送同时打开第1、第2、第3个继电器的命令（按顺序发送三条指令）
     */
    public void sendMultipleRelays() {
        writeBytes(hexStringToByteArray("1F 05 00 00 FF 00 8F 84"));
        sleep(100);
        writeBytes(hexStringToByteArray("1F 05 00 01 FF 00 DE 44"));
        sleep(100);
        writeBytes(hexStringToByteArray("1F 05 00 02 FF 00 2E 44"));
    }

    /**
     * 用于退出程序前关闭串口（如果你需要退出操作）
     */
    public void quit() {
        System.out.println("退出程序");
        close();
    }

    public static void main(String[] args) {
        SerialPortUtil util = new SerialPortUtil();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\n请选择要发送的命令：");
        while (true) {
            System.out.println("1: 全部打开");
            System.out.println("2: 全部关闭");
            System.out.println("3: 打开第1个继电器 (亮红灯)");
            System.out.println("4: 打开第2个继电器 (电风扇)");
            System.out.println("5: 打开第3个继电器 (打开推杆)");
            System.out.println("6: 打开第4个继电器 (缩回推杆)");
            System.out.println("7: 同时打开第1、第2、第3个继电器");
            System.out.println("q: 退出");
            System.out.print("请输入选项: ");

            try {
                String input = reader.readLine().trim();
                switch (input) {
                    case "1":
                        util.sendAllOpen();
                        break;
                    case "2":
                        util.sendAllClose();
                        break;
                    case "3":
                        util.sendRelay1();
                        break;
                    case "4":
                        util.sendRelay2();
                        break;
                    case "5":
                        util.sendRelay3();
                        break;
                    case "6":
                        util.sendRelay4();
                        break;
                    case "7":
                        util.sendMultipleRelays();
                        break;
                    case "q":
                        util.quit();
                        return; // 退出程序
                    default:
                        System.out.println("无效的选项，请重新选择。");
                }
            } catch (Exception e) {
                log.error("读取输入出错", e);
            }
        }
    }
}