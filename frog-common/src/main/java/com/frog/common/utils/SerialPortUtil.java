package com.frog.common.utils;

import com.fazecast.jSerialComm.SerialPort;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class SerialPortUtil {
    private SerialPort serialPort;
    private static final Log log = LogFactory.getLog(SerialPortUtil.class);

    public SerialPortUtil() {
        // 配置串口
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

    // 根据实际情况调整的等待时间（毫秒）
    private static final int SHORT_DELAY_MS = 100;
    private static final int PUSH_ROD_RETRACTION_DELAY_MS = 9000; //等待9秒 推杆缩回

    // 以下各方法为独立的发送命令方法，调用时直接执行相应方法即可

    /**
     * 打开全部设备：
     * 1. 打开红灯
     * 2. 打开电风扇
     * 3. 伸出推杆
     */
    public void openAllDevices() {
        openRedLight();
        delay(SHORT_DELAY_MS);
        extendPushRod();
    }

    /**
     * 关闭全部设备：
     * 1. 关闭红灯
     * 2. 关闭电风扇
     * 3. 停止伸出推杆
     * 4. 激活推杆缩回
     * 5. 等待推杆缩回结束
     * 6. 关闭推杆缩回继电器
     */
    public void closeAllDevices() {
        closeRedLight();
        delay(SHORT_DELAY_MS);
        stopExtendPushRod();
        delay(SHORT_DELAY_MS);
        activatePushRodRetraction();
        // 等待推杆完成缩回，具体延时需依据实际动作时间调整
        delay(PUSH_ROD_RETRACTION_DELAY_MS);
        deactivatePushRodRetraction();
    }

    /**
     * 打开红灯（第1个继电器）
     */
    public void openRedLight() {
        writeBytes(hexStringToByteArray("1F 05 00 00 FF 00 8F 84"));
    }


    /**
     * 关闭红灯（第1个继电器）
     */
    public void closeRedLight() {
        writeBytes(hexStringToByteArray("1F 05 00 00 00 00 CE 74"));
    }


    /**
     * 伸出推杆（打开第3个继电器）
     */
    public void extendPushRod() {
        writeBytes(hexStringToByteArray("1F 05 00 02 FF 00 2E 44"));
        delay(PUSH_ROD_RETRACTION_DELAY_MS); //等待伸出完毕后 关闭继电器
        stopExtendPushRod();
    }

    /**
     * 停止伸出推杆（关闭第3个继电器）
     */
    public void stopExtendPushRod() {
        writeBytes(hexStringToByteArray("1F 05 00 02 00 00 6F B4"));
    }

    /**
     * 激活推杆缩回（打开控制推杆缩回的继电器）
     */
    public void activatePushRodRetraction() {
        writeBytes(hexStringToByteArray("1F 05 00 03 FF 00 7F 84"));
    }

    /**
     * 关闭推杆缩回（关闭控制推杆缩回的继电器）
     *
     */
    public void deactivatePushRodRetraction() {
        writeBytes(hexStringToByteArray("1F 05 00 03 00 00 3E 74"));
    }

    /**
     * 同时打开红灯、电风扇和推杆（按顺序发送三条指令，中间插入延时）
     */
    public void openMultipleDevices() {
        openRedLight();
        delay(SHORT_DELAY_MS);
        extendPushRod();
    }

    /**
     * 简单延时方法，使用 Thread.sleep 实现
     */
    private void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
            System.out.println("1: 打开全部设备");
            System.out.println("2: 关闭全部设备");
            System.out.println("3: 打开红灯");
            System.out.println("5: 伸出推杆");
            System.out.println("6: 缩回推杆");
            System.out.println("7: 同时打开红灯、推杆");
            System.out.println("q: 退出");
            System.out.print("请输入选项: ");

            try {
                String input = reader.readLine().trim();
                switch (input) {
                    case "1":
                        util.openAllDevices();
                        break;
                    case "2":
                        util.closeAllDevices();
                        break;
                    case "3":
                        util.openRedLight();
                        break;
                    case "5":
                        util.extendPushRod();
                        break;
                    case "6":
                        // 对于推杆缩回，先停止伸出，再启动缩回操作
                        util.activatePushRodRetraction();
                        util.delay(PUSH_ROD_RETRACTION_DELAY_MS);
                        util.deactivatePushRodRetraction();
                        break;
                    case "7":
                        util.openMultipleDevices();
                        break;
                    case "q":
                        util.quit();
                        return; // 退出程序
                    default:
                        System.out.println("无效的选项，请重新选择。");
                }
            } catch (Exception e) {
                System.err.println("读取输入出错：" + e.getMessage());
            }
        }
    }
}