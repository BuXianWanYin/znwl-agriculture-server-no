package com.frog.common.utils;/*
 * @author 不羡晚吟
 * @version 1.0
 */
import lombok.extern.slf4j.Slf4j;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

@Slf4j
public class AudioPlayer {

    private static final String ALARM_AUDIO_PATH = "D:\\qkl-zhny\\znwl-agriculture-server-no\\wav\\报警音响.wav";
    private static Clip clip;
    private static boolean isPlaying = false;

    public static void playAlarmSound() {
        if (isPlaying) {
            return; // 如果已经在播放，则不重复启动
        }

        try {
            File audioFile = new File(ALARM_AUDIO_PATH);
            if (!audioFile.exists()) {
                log.error("报警音频文件不存在: {}", ALARM_AUDIO_PATH);
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            // 添加循环播放监听器
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP && isPlaying) {
                    clip.setFramePosition(0); // 重置到开始位置
                    clip.start(); // 重新开始播放
                }
            });

            clip.start();
            isPlaying = true;
            log.info("开始循环播放报警音频");
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            log.error("播放报警音频失败: " + e.getMessage());
            isPlaying = false;
        }
    }

    public static void stopAlarmSound() {
        if (clip != null && isPlaying) {
            isPlaying = false; // 先设置标志，这样监听器就不会重新开始播放
            clip.stop();
            clip.close();
            log.info("停止播放报警音频");
        }
    }

}
