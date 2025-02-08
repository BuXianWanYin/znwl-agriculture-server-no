package com.frog.agriculture.service.impl;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.frog.agriculture.domain.TraceCodeTemplate;
import com.frog.agriculture.domain.TraceSellpro;
import com.frog.agriculture.domain.TraceVersion;
import com.frog.agriculture.mapper.TraceCodeTemplateMapper;
import com.frog.common.config.RuoYiConfig;
import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import com.frog.common.utils.uuid.UUID;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.frog.agriculture.mapper.TraceCodeMapper;
import com.frog.agriculture.domain.TraceCode;
import com.frog.agriculture.service.ITraceCodeService;
import com.frog.agriculture.mapper.TraceVersionMapper;
import com.frog.agriculture.mapper.TraceSellproMapper;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

/**
 * 溯源码Service业务层处理
 * 
 * @author nealtsiao
 * @date 2023-08-04
 */
@Service
public class TraceCodeServiceImpl implements ITraceCodeService
{
    @Autowired
    private TraceCodeMapper traceCodeMapper;
    @Autowired
    private TraceVersionMapper traceVersionMapper;
    @Autowired
    private TraceSellproMapper traceSellproMapper;

    @Autowired
    private TraceCodeTemplateMapper traceCodeTemplateMapper;

    /**
     * 查询溯源码
     * 
     * @param codeId 溯源码主键
     * @return 溯源码
     */
    @Override
    public TraceCode selectTraceCodeByCodeId(Long codeId)
    {
        return traceCodeMapper.selectTraceCodeByCodeId(codeId);
    }

    /**
     * 查询溯源码列表
     * 
     * @param traceCode 溯源码
     * @return 溯源码
     */
    @Override
    public List<TraceCode> selectTraceCodeList(TraceCode traceCode)
    {
        return traceCodeMapper.selectTraceCodeList(traceCode);
    }

    /**
     * 查询异溯源码列表
     *
     * @param traceCode 溯源码
     * @return
     */
    @Override
    public List<TraceCode> selectAlertTraceCodeList(TraceCode traceCode) {
        return traceCodeMapper.selectAlertTraceCodeList(traceCode);
    }

    /**
     * 新增溯源码
     * 
     * @param traceCode 溯源码
     * @return 结果
     */
    @Override
    public int insertTraceCode(TraceCode traceCode)
    {
        traceCode.setCreateBy(SecurityUtils.getUserId().toString());
        traceCode.setCreateTime(DateUtils.getNowDate());
        return traceCodeMapper.insertTraceCode(traceCode);
    }

    /**
     * 批量生成溯源码
     * @param versionId
     * @param codeNum
     * @return
     */
    @Override
    public void batchInsertTraceCode(Long versionId,Long codeNum)
    {
       TraceVersion traceVersion = traceVersionMapper.selectTraceVersionByVersionId(versionId);
       TraceSellpro traceSellpro = traceSellproMapper.selectTraceSellproBySellproId(traceVersion.getSellproId());
        for(int i=1;i<=codeNum;i++){
            TraceCode traceCode = new TraceCode();
            traceCode.setVersionId(versionId);
            traceCode.setSellproId(traceSellpro.getSellproId());
            traceCode.setTraceCode(UUID.randomUUID().toString());
//            traceCode.setTraceUrl(traceSellpro.getH5Url()+"?tracecode="+traceCode.getTraceCode());
            traceCode.setStatus("1");
            traceCode.setCreateBy(SecurityUtils.getUserId().toString());
            traceCode.setCreateTime(DateUtils.getNowDate());
            traceCodeMapper.insertTraceCode(traceCode);
        }
    }
    /**
     * 修改溯源码
     * 
     * @param traceCode 溯源码
     * @return 结果
     */
    @Override
    public int updateTraceCode(TraceCode traceCode)
    {
        traceCode.setUpdateBy(SecurityUtils.getUserId().toString());
        traceCode.setUpdateTime(DateUtils.getNowDate());
        return traceCodeMapper.updateTraceCode(traceCode);
    }

    /**
     * 批量删除溯源码
     * 
     * @param codeIds 需要删除的溯源码主键
     * @return 结果
     */
    @Override
    public int deleteTraceCodeByCodeIds(Long[] codeIds)
    {
        return traceCodeMapper.deleteTraceCodeByCodeIds(codeIds);
    }

    /**
     * 删除溯源码信息
     * 
     * @param codeId 溯源码主键
     * @return 结果
     */
    @Override
    public int deleteTraceCodeByCodeId(Long codeId)
    {
        return traceCodeMapper.deleteTraceCodeByCodeId(codeId);
    }

    /**
     * 打包溯源码图片文件压缩ZIP，输出到HttpServletResponse
     * @param httpServletResponse
     * @param traceCode
     */
    @Override
    public void downloadTraceCodeImagesZip(HttpServletResponse httpServletResponse,TraceCode traceCode) {
        //通过traceCode.versionId找到traceVersion,再通过traceVersion的sellproId找到traceSellpro，通过traceSellprod的templateId找到traceCodeTemplate，再找到url
        TraceVersion traceVersion = traceVersionMapper.selectTraceVersionByVersionId(traceCode.getVersionId());
        if(traceVersion ==null){
           return;
        }
        TraceSellpro traceSellpro = traceSellproMapper.selectTraceSellproBySellproId(traceVersion.getSellproId());
        if(traceSellpro ==null){
            return;
        }
        TraceCodeTemplate traceCodeTemplate = traceCodeTemplateMapper.selectTraceCodeTemplateByTemplateId(traceSellpro.getCodeTemplateId());
        if(traceCodeTemplate==null){
            return;
        }
        String profile = RuoYiConfig.getProfile();
        String filepath = traceCodeTemplate.getTemplateBgi();
        String replace = filepath.replace("/profile", profile);
        try (ZipOutputStream zippedOut = new ZipOutputStream(httpServletResponse.getOutputStream())) {
            // 将第一个文件添加到zip流中
//            addFileToZip(zippedOut,replace);
            List<TraceCode> traceCodes = traceCodeMapper.selectTraceCodeList(traceCode);
            if(traceCodes.isEmpty()){
                return;
            }
            createZipWithQrCodes(zippedOut,traceCodes,replace,traceCodeTemplate.getQrcodeWidth(),traceCodeTemplate.getQrcodeHeight(),traceCodeTemplate.getQrcodeX(),traceCodeTemplate.getQrcodeY());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据traceCode查询溯源码
     * @param traceCode
     * @return
     */
    @Override
    public TraceCode selectTraceCodeByTraceCode(String traceCode) {
        return traceCodeMapper.selectTraceCodeByTraceCode(traceCode);
    }

    /**
     * 下载溯源码图片zip包
     * @param zippedOut
     * @param traceCodeList
     * @param bgImagePath
     * @param qrcodeWidth
     * @param qrcodeHeight
     * @param qrcodeX
     * @param qrcodeY
     */
    public void createZipWithQrCodes(ZipOutputStream zippedOut, List<TraceCode> traceCodeList, String bgImagePath,
                                     int qrcodeWidth, int qrcodeHeight, int qrcodeX, int qrcodeY) {
        try {
            for (TraceCode traceCode : traceCodeList) {
                // 生成二维码图片
                BufferedImage qrCodeImage = generateQRCodeImage(traceCode.getTraceUrl(), qrcodeWidth, qrcodeHeight);

                // 加载背景图片
                BufferedImage bgImage = ImageIO.read(new File(bgImagePath));

                // 在背景图片上绘制二维码
                Graphics2D graphics = bgImage.createGraphics();
                graphics.drawImage(qrCodeImage, qrcodeX, qrcodeY, null);
                graphics.dispose();

                // 将拼接后的图片写入 Zip 文件
                String qrCodeFileName = traceCode.getTraceCode() + ".png";
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bgImage, "png", baos);
                byte[] imageBytes = baos.toByteArray();
                ZipEntry zipEntry = new ZipEntry(qrCodeFileName);
                zippedOut.putNextEntry(zipEntry);
                zippedOut.write(imageBytes);
                zippedOut.closeEntry();
            }
            System.out.println("拼接图片并压缩为 ZIP 文件成功！");
        } catch (IOException | WriterException e) {
            System.err.println("处理图片和压缩 ZIP 文件时出错：" + e.getMessage());
        }
    }
    private BufferedImage generateQRCodeImage(String text, int width, int height) throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1); // 默认4个模块

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height,hints);

        // 创建BufferedImage对象来表示二维码图像
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 将BitMatrix绘制到BufferedImage中
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return bufferedImage;
    }
}
