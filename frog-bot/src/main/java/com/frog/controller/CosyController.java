package com.frog.controller;

import com.frog.config.BotConfig;
import com.frog.utils.HttpClientUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * cosy接口
 */
@RestController
@RequestMapping("/cosy")
public class CosyController {

    private static final int SAMPLE_RATE = 22050; // 采样率

    private static final int BITS_PER_SAMPLE = 16; // 采样位数

    private static final int CHANNELS = 1; // 单声道

    private static final String wavPath = "wav";

    private static final Logger log = LogManager.getLogger(CosyController.class);

    /**
     * 语音合成接口
     */
    @PostMapping(value = "/voice")
    public String cosyVoice(String text) {
        log.info(text);

        Map<String, String> payload = new HashMap<>();
        String url = BotConfig.getTtsUrl();  // Adjust this to the correct URL for your service.

        String mode = BotConfig.getTtsMode();
        payload.put("tts_text", text);
        switch (mode) {
            case "sft":
                payload.put("spk_id", BotConfig.getSpkId());
                break;
            case "zero_shot":
                payload.put("prompt_text", BotConfig.getPromptText());
                break;
            case "cross_lingual":
                break;
            default: // Handle default case
                payload.put("spk_id", BotConfig.getSpkId());
                payload.put("instruct_text", BotConfig.getInstructText());
                break;
        }

       try {
           byte[] response = HttpClientUtil.doPostRestTemplateByte(url + mode, payload, BotConfig.getPromptWav(), mode);
           if (response != null && response.length > 0) {
               // 将字节数组转换为int16数组
               short[] audioData = new short[response.length / 2];
               ByteBuffer.wrap(response).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(audioData);

               // 创建WAV文件格式
               AudioFormat format = new AudioFormat(
                       SAMPLE_RATE,          // 采样率
                       BITS_PER_SAMPLE,      // 采样位数
                       CHANNELS,             // 声道数
                       true,                 // 是否有符号（signed）
                       false                 // 字节序（false=小端序）
               );

               // 创建音频流
               byte[] wavData = new byte[audioData.length * 2];
               ByteBuffer.wrap(wavData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(audioData);

               AudioInputStream audioInputStream = new AudioInputStream(
                       new ByteArrayInputStream(wavData),
                       format,
                       audioData.length
               );

               File file = new File(wavPath);
               if (!file.exists()){
                   file.mkdir();
               }

               // 保存为WAV文件
               AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(wavPath + "/" + "output.wav"));
               System.out.println("音频文件已保存到 output.wav");
           }
       }catch (Exception e){
          log.error(e.getMessage());
       }
        return "成功";
    }
}