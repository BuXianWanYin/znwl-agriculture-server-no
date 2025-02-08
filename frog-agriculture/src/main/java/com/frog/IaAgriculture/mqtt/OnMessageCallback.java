package com.frog.IaAgriculture.mqtt;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.frog.IaAgriculture.config.MqttConfig;
import com.frog.IaAgriculture.domain.SensorValue;
import com.frog.IaAgriculture.mapper.SensorValueMapper;
import com.frog.IaAgriculture.model.IaPasture;
import com.frog.IaAgriculture.service.DeviceService;
import com.frog.IaAgriculture.service.SmartWarning;
import com.frog.agriculture.service.IWarningInfoService;
import com.frog.agriculture.domain.WarningInfo;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.frog.IaAgriculture.domain.Device;
import com.frog.IaAgriculture.mapper.DeviceMapper;
import com.frog.IaAgriculture.mapper.IaPastureMapper;
import vip.blockchain.agriculture.model.bo.PlatformCheckInputBO;
import vip.blockchain.agriculture.service.PlatformService;
import vip.blockchain.agriculture.utils.BaseUtil;

import javax.annotation.Resource;
import javax.sound.sampled.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * MQTT消息回调类，用于处理设备推送的消息
 */
@Component
public class OnMessageCallback implements MqttCallback {
    private static final double TEMP_THRESHOLD = 35.0;
    private static final double SOIL_THRESHOLD = 30.0;
    private static final int FIRE_ALERT = 0;
    private static final int AUTH_INTERVAL_MINUTES = 5; // 认证间隔时间为5分钟
    private Long currentWarningId = null;
    private Clip currentClip;

    @Autowired
    private IWarningInfoService iWarningInfoService;
    @Autowired
    private MqttConfig mqttConfig;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private IaPastureMapper iaPastureMapper;
    @Autowired
    private SensorValueMapper sensorValueMapper;
    @Resource
    private PlatformService platformService;
    @Resource
    private DeviceService deviceService;
    @Autowired
    private Client client;
    private final SmartWarning smartWarning;

    private Map<String, LocalDateTime> deviceAuthTimeMap = new HashMap<>();


//    任务描述：
//    实现 checkTempHumidityAndFire
//    方法和 handleAlert
//    方法。其主要作用是接收传感器设备传递的温湿度、火灾报警等数据，进行阈值检查和报警处理。
//    当触发报警时，调用相关方法进行报警信息处理、区块链存证，并发布警告信息。


    @Autowired
    public OnMessageCallback(Client client) throws ContractException {
        this.smartWarning = SmartWarning.deploy(client, client.getCryptoSuite().getCryptoKeyPair());
    }

    private IaPasture getPastureByDevicePastureId() {
        List<String> pastureIds = deviceMapper.selectList(new QueryWrapper<Device>().select("pasture_id")).stream().map(Device::getPastureId).filter(Objects::nonNull).collect(Collectors.toList());
        if (pastureIds.isEmpty()) {
            return null;
        }
        return iaPastureMapper.selectList(new QueryWrapper<IaPasture>().in("id", pastureIds)).stream().findFirst().orElse(null);
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println(throwable);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        DeviceMessage deviceMessage = JSON.parseObject(mqttMessage.toString(), DeviceMessage.class);
        fiter(deviceMessage);

        boolean alertTriggered = false;
        WarningInfo warningInfo = new WarningInfo();
        StringBuilder thresholdValueBuilder = new StringBuilder();

        alertTriggered = checkTempHumidityAndFire(deviceMessage, alertTriggered, thresholdValueBuilder);

        if (alertTriggered) {
            handleAlert(deviceMessage, warningInfo, thresholdValueBuilder);
        } else if (currentWarningId != null) {
            endAlert();
        }
    }

//    任务3：实现 checkTempHumidityAndFire 方法
//    功能描述：
//    该方法负责接收设备上传的温度、湿度和火灾报警信息，并根据定义的阈值对数据进行检测。
//    当温度或湿度超过阈值时，触发报警，并记录具体的阈值信息。
//    如果检测到火灾报警信号，则触发报警。
//    在每次报警时，调用 playAlertSound 播放不同类型的报警声音。

//    方法细节：
//    解析温度、湿度和火灾报警数据，并判断是否超过阈值。
//    如果温度超过阈值，追加报警信息，并播放温度报警声。
//    如果湿度超过阈值，追加湿度报警信息，并播放湿度报警声。
//    如果检测到火灾报警，追加火灾报警信息，并播放火灾报警声。
//    返回 alertTriggered，表示是否触发了报警。

    private boolean checkTempHumidityAndFire(DeviceMessage deviceMessage, boolean alertTriggered, StringBuilder thresholdValueBuilder) {
        double temp = parseTemperature(deviceMessage.getTemp());
        if (temp > TEMP_THRESHOLD) {
            alertTriggered = true;
            thresholdValueBuilder.append("温度阈值: ").append(TEMP_THRESHOLD).append("℃, 实际温度: ").append(temp).append("℃; ");
            playAlertSound("alert.wav");
        }

        double soilHumidity = parseHumidity(deviceMessage.getSoil());
        if (soilHumidity > SOIL_THRESHOLD) {
            alertTriggered = true;
            thresholdValueBuilder.append("湿度阈值: ").append(SOIL_THRESHOLD).append("%, 实际湿度: ").append(soilHumidity).append("%; ");
            playAlertSound("soil.wav");
        }

//        if (deviceMessage.getFire() == FIRE_ALERT) {
//            alertTriggered = true;
//            thresholdValueBuilder.append("火灾报警; ");
//            playAlertSound("Fire.wav");
//        }
        return alertTriggered;
    }

//    任务4：实现 handleAlert 方法
//    功能描述：
//    该方法用于处理当报警触发时的后续操作，包括存储报警信息到区块链和数据库中。
//    当报警首次触发时，调用 addSensorDataToBlockchain 方法将传感器数据存入区块链，并将预警信息保存到数据库。
//    如果报警仍然存在，则更新当前报警信息的状态和时间戳。

//    方法细节：
//    如果是新的报警（即 currentWarningId 为空），则获取牧场信息，调用 addSensorDataToBlockchain 将传感器数据写入区块链。
//    检查区块链交易是否成功。如果成功，将报警信息存入数据库，并将预警 ID 保存到 currentWarningId。
//    通过 mqttConfig.publishAsync() 方法发布报警消息。
//    如果报警已经存在，则更新报警信息的状态和更新时间。
    private void handleAlert(DeviceMessage deviceMessage, WarningInfo warningInfo, StringBuilder thresholdValueBuilder) throws ContractException {
        if (currentWarningId == null) {
            IaPasture iaPasture = this.getPastureByDevicePastureId();
            if (iaPasture == null) return;

            TransactionReceipt receipt = addSensorDataToBlockchain(deviceMessage, iaPasture);

            if (receipt.isStatusOK()) {
                fillWarningInfo(warningInfo, iaPasture, thresholdValueBuilder, receipt);
                iWarningInfoService.insertWarningInfo(warningInfo);
                currentWarningId = warningInfo.getId();
            } else {
                throw new RuntimeException("Transaction failed.");
            }

            mqttConfig.publishAsync("warning", buildWarningMessage(warningInfo));
        } else {
            warningInfo.setId(currentWarningId);
            warningInfo.setUpdatedAt(new Date());
            iWarningInfoService.updateWarningInfo(warningInfo);
        }
    }

    private TransactionReceipt addSensorDataToBlockchain(DeviceMessage deviceMessage, IaPasture iaPasture) throws ContractException {
        BigInteger tempC = new BigInteger(deviceMessage.getTemp().replace("℃", "").trim());
        BigInteger hum = new BigInteger(deviceMessage.getHum().replace("%", "").trim());
        BigInteger p = deviceMessage.getP().toBigInteger();
        BigInteger lux = new BigDecimal(deviceMessage.getLux().replace(" lux", "").trim()).toBigInteger();
        BigInteger soil = new BigInteger(deviceMessage.getSoil().replace("%", "").trim());
        BigInteger air = deviceMessage.getAir().toBigInteger();
        BigInteger relay = BigInteger.valueOf(deviceMessage.getRelay());
        BigInteger fire = BigInteger.valueOf(deviceMessage.getFire());

        return smartWarning.addAndCheckSensorData(iaPasture.getAddress(), tempC, hum, p, lux, soil, air, relay, fire);
    }

    private void fillWarningInfo(WarningInfo warningInfo, IaPasture iaPasture, StringBuilder thresholdValueBuilder, TransactionReceipt receipt) {
        warningInfo.setCropBatch("模拟农作物批次001");
        warningInfo.setResponsiblePerson("张三");
        warningInfo.setPartitionInfo(iaPasture.getAddress());
        warningInfo.setGreenhouse(iaPasture.getName());
        warningInfo.setThresholdValue(thresholdValueBuilder.toString());
        warningInfo.setWarningStatus("未处理");
        warningInfo.setHandler(receipt.getTransactionHash());
        warningInfo.setStartTime(new Date());
        warningInfo.setCreatedAt(new Date());
        warningInfo.setUpdatedAt(new Date());
    }

    private String buildWarningMessage(WarningInfo warningInfo) {
        return "预警触发: 预警分区: " + warningInfo.getPartitionInfo() + "，预警温室: " + warningInfo.getGreenhouse() + "，预警详情: " + warningInfo.getThresholdValue();
    }

    private void endAlert() {
        WarningInfo existingWarning = new WarningInfo();
        existingWarning.setId(currentWarningId);
        existingWarning.setEndTime(new Date());
        existingWarning.setWarningStatus("已处理");
        existingWarning.setUpdatedAt(new Date());
        iWarningInfoService.updateWarningInfo(existingWarning);
        currentWarningId = null;

        stopCurrentSound();
    }

    private void stopCurrentSound() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            currentClip.flush();
            currentClip.close();
            currentClip = null;
        }
    }

    private void playAlertSound(String soundFileName) {
        try {
            URL soundFileUrl = getClass().getClassLoader().getResource(soundFileName);
            if (soundFileUrl == null) {
                System.out.println("找不到 " + soundFileName + " 文件");
                return;
            }
            stopCurrentSound();
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFileUrl);
            currentClip = AudioSystem.getClip();
            currentClip.open(audioStream);
            currentClip.start();
            currentClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    stopCurrentSound();
                }
            });
        } catch (Exception e) {
            System.out.println("播放声音失败: " + e.getMessage());
        }
    }

    private double parseTemperature(String temp) {
        return Double.parseDouble(temp.replace("℃", ""));
    }

    private double parseHumidity(String humidity) {
        return Double.parseDouble(humidity.replace("%", ""));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    public void fiter(DeviceMessage deviceMessage) {
        String deviceId = deviceMessage.getId();
        LocalDateTime lastAuthTime = deviceAuthTimeMap.get(deviceId);
        LocalDateTime now = LocalDateTime.now();

        // 如果上次认证时间不存在或认证时间距离现在超过5分钟，则重新认证
        if (lastAuthTime == null || lastAuthTime.plusMinutes(AUTH_INTERVAL_MINUTES).isBefore(now)) {
            // 进行设备认证
            PlatformCheckInputBO platformCheckInputBO = new PlatformCheckInputBO(deviceId);
            LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Device::getDeviceId, deviceId);
            Device device = deviceService.getOne(queryWrapper);
            if (device == null) {
                System.out.println("设备 " + deviceId + " 不存在");
                return; // 如果设备不存在，终止操作
            }

            CallResponse check = platformService.check(platformCheckInputBO);
            Boolean checkResult = (Boolean) JSON.parseArray(check.getValues()).get(0);

            if (!checkResult) {
                System.out.println("设备 " + deviceId + " 认证失败，跳过后续处理");
                return; // 如果认证失败，终止处理，不执行后续操作
            }

            // 认证成功，更新上次认证时间
            deviceAuthTimeMap.put(deviceId, now);
        }

        // 如果认证通过或者在5分钟内，则执行后续逻辑
        Device device = deviceService.getOne(new LambdaQueryWrapper<Device>().eq(Device::getDeviceId, deviceId));
        if (device == null) return;

        SensorValue sensorValue = createSensorValue(deviceMessage, device);
        sensorValueMapper.insert(sensorValue);

        device.setUpdateTime(new Date());
        deviceService.updateById(device);
    }


    private SensorValue createSensorValue(DeviceMessage deviceMessage, Device device) {
        SensorValue sensorValue = new SensorValue();
        sensorValue.setDeviceId(device.getDeviceId());
        sensorValue.setPastureId(StrUtil.isNotBlank(device.getPastureId()) ? device.getPastureId() : null);
        sensorValue.setTemp(deviceMessage.getTemp());
        sensorValue.setHum(deviceMessage.getHum());
        sensorValue.setP(String.valueOf(deviceMessage.getP()));
        sensorValue.setLux(deviceMessage.getLux());
        sensorValue.setTime(deviceMessage.getTime());
        sensorValue.setDate(deviceMessage.getDate());
        sensorValue.setFire(String.valueOf(deviceMessage.getFire()));
        sensorValue.setSoil(deviceMessage.getSoil());
        sensorValue.setAir(String.valueOf(deviceMessage.getAir()));
        sensorValue.setRelay(String.valueOf(deviceMessage.getRelay()));
        sensorValue.setDateTime(LocalDateTime.now());
        sensorValue.setId(BaseUtil.getSnowflakeId());
        sensorValue.setTemperature(new BigDecimal(deviceMessage.getTemp().replace("℃", "")));
        sensorValue.setHumidity(new BigDecimal(deviceMessage.getHum().replace("%", "")));
        sensorValue.setAirquality(new BigDecimal(String.valueOf(deviceMessage.getAir())));
        sensorValue.setPressure(new BigDecimal(String.valueOf(deviceMessage.getP())));
        return sensorValue;
    }
}
