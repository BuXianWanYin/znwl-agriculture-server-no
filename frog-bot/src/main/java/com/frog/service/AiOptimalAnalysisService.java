package com.frog.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import com.frog.mapper.AiOptimalAnalysisMapper;
import com.frog.model.domain.AiOptimalAnalysis;
import com.frog.utils.AIModelApiUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AiOptimalAnalysisService extends ServiceImpl<AiOptimalAnalysisMapper, AiOptimalAnalysis> {

    private static final Log log = LogFactory.getLog(AiOptimalAnalysisService.class);

    public int addData(Long speciesId, Long germplasmId, String fishSpeciesName, String fishName) {
        String data = AIModelApiUtils.aiAnalysisJson(speciesId, germplasmId, fishSpeciesName, fishName);
        JSONObject jsonObject = JSON.parseObject(data);
        JSONObject contentObject = jsonObject.getJSONObject("message").getJSONObject("content");
        log.info(contentObject);
        JSONObject environmentalParams = contentObject.getJSONObject("environmental_params");
        JSONObject coreIndicators = contentObject.getJSONObject("core_indicators");
        JSONObject comprehensiveAssessment = contentObject.getJSONObject("comprehensive_assessment");
        AiOptimalAnalysis aiOptimalAnalysis = new AiOptimalAnalysis();
        if (speciesId != null) {
            JSONArray breedingSuggestions = contentObject.getJSONArray("breeding_suggestions");
            aiOptimalAnalysis.setSpeciesId(speciesId);
            aiOptimalAnalysis.setOptimalWaterTemperature(environmentalParams.get("water_temperature").toString());
            aiOptimalAnalysis.setOptimalWaterPh(environmentalParams.get("ph").toString());
            aiOptimalAnalysis.setOptimalDissolvedOxygen(environmentalParams.get("dissolved_oxygen").toString());
            aiOptimalAnalysis.setOptimalAmmonia(environmentalParams.get("ammonia_nitrogen").toString());
            aiOptimalAnalysis.setOptimalNitrite(environmentalParams.get("nitrite").toString());
            aiOptimalAnalysis.setFeedConversion((Integer) coreIndicators.get("feed_conversion"));
            aiOptimalAnalysis.setCultivationDifficulty(comprehensiveAssessment.get("breeding_difficulty").toString());

            // 按顺序填充养殖建议
            aiOptimalAnalysis.setWaterManagement(breedingSuggestions.getJSONObject(0).getString("content"));
            aiOptimalAnalysis.setFeedingManagement(breedingSuggestions.getJSONObject(1).getString("content"));
            aiOptimalAnalysis.setDiseasePrevention(breedingSuggestions.getJSONObject(2).getString("content"));
            aiOptimalAnalysis.setEnvironmentMonitoring(breedingSuggestions.getJSONObject(3).getString("content"));

        }
        if (germplasmId != null) {
            JSONArray plantingSuggestions = contentObject.getJSONArray("planting_suggestions");
            aiOptimalAnalysis.setGermplasmId(germplasmId);
            aiOptimalAnalysis.setOptimalTemperature(environmentalParams.get("temperature").toString());
            aiOptimalAnalysis.setOptimalHumidity(environmentalParams.get("humidity").toString());
            aiOptimalAnalysis.setOptimalLight(environmentalParams.get("light").toString());
            aiOptimalAnalysis.setOptimalWindDirection(environmentalParams.get("wind_direction").toString());
            aiOptimalAnalysis.setOptimalWindSpeed(environmentalParams.get("wind_speed").toString());
            aiOptimalAnalysis.setOptimalSoilPh(environmentalParams.get("ph").toString());
            aiOptimalAnalysis.setFeedConversion((Integer) coreIndicators.get("fertilizer_conversion"));
            aiOptimalAnalysis.setCultivationDifficulty(comprehensiveAssessment.get("cultivation_difficulty").toString());

            // 按顺序填充种植建议
            aiOptimalAnalysis.setWaterManagement(plantingSuggestions.getJSONObject(0).getString("content"));
            aiOptimalAnalysis.setFeedingManagement(plantingSuggestions.getJSONObject(1).getString("content"));
            aiOptimalAnalysis.setDiseasePrevention(plantingSuggestions.getJSONObject(2).getString("content"));
            aiOptimalAnalysis.setEnvironmentMonitoring(plantingSuggestions.getJSONObject(3).getString("content"));

        }
        aiOptimalAnalysis.setGrowthRate((Integer) coreIndicators.get("growth_rate"));
        aiOptimalAnalysis.setDiseaseResistance((Integer) coreIndicators.get("disease_resistance"));
        aiOptimalAnalysis.setMarketAcceptance((Integer) coreIndicators.get("market_acceptance"));

        aiOptimalAnalysis.setGrowthAssessment(comprehensiveAssessment.get("growth_assessment").toString());
        aiOptimalAnalysis.setGeneralRecommendations(comprehensiveAssessment.get("general_recommendations").toString());
        aiOptimalAnalysis.setMarketAnalysis(comprehensiveAssessment.get("market_analysis").toString());


        // 设置创建时间
        aiOptimalAnalysis.setCreateTime(new Date());
        // 执行新增操作
        return baseMapper.insert(aiOptimalAnalysis);
    }

    public List<AiOptimalAnalysis> getDataBySpeciesId(Long speciesId) {
        LambdaQueryWrapper<AiOptimalAnalysis> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(AiOptimalAnalysis::getSpeciesId, speciesId);
        return baseMapper.selectList(queryWrapper);
    }

    public List<AiOptimalAnalysis> getDataByGermplasmId(Long germplasmId) {
        LambdaQueryWrapper<AiOptimalAnalysis> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(AiOptimalAnalysis::getGermplasmId, germplasmId);
        return baseMapper.selectList(queryWrapper);
    }
}
