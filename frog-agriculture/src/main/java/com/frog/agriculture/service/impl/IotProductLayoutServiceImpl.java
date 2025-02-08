package com.frog.agriculture.service.impl;

import java.util.List;

import com.frog.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.frog.agriculture.mapper.IotProductLayoutMapper;
import com.frog.agriculture.domain.IotProductLayout;
import com.frog.agriculture.service.IIotProductLayoutService;

/**
 * 产品布局Service业务层处理
 * 
 * @author nealtsiao
 * @date 2023-08-11
 */
@Service
public class IotProductLayoutServiceImpl implements IIotProductLayoutService 
{
    @Autowired
    private IotProductLayoutMapper iotProductLayoutMapper;

    /**
     * 查询产品布局列表
     * 
     * @param iotProductLayout 产品布局
     * @return 产品布局
     */
    @Override
    public List<IotProductLayout> selectIotProductLayoutList(IotProductLayout iotProductLayout)
    {
        iotProductLayout.setUserId(SecurityUtils.getUserId());
        return iotProductLayoutMapper.selectIotProductLayoutList(iotProductLayout);
    }

    /**
     * 保存
     * @param iotProductLayoutList
     * @return
     */
    @Override
    public int insertIotProductLayout(List<IotProductLayout> iotProductLayoutList)
    {
        int i = 0;
        //删除所有布局
        IotProductLayout iotProductLayout = new IotProductLayout();
        iotProductLayout.setProductId(iotProductLayoutList.get(0).getProductId());
        iotProductLayout.setUserId(SecurityUtils.getUserId());
        iotProductLayoutMapper.deleteIotProductLayoutByProductIdAndUserId(iotProductLayout);

        for(IotProductLayout item : iotProductLayoutList ){
            item.setUserId(SecurityUtils.getUserId());
            int j = iotProductLayoutMapper.insertIotProductLayout(item);
            i=i+j;
        }
        return  iotProductLayoutList.size() == i?1:0;
    }


    /**
     * 删除产品布局信息
     * 
     * @param productId 产品布局主键
     * @return 结果
     */
    @Override
    public int deleteIotProductLayoutByProductIdId(Long productId)
    {
        IotProductLayout iotProductLayout = new IotProductLayout();
        iotProductLayout.setProductId(productId);
        iotProductLayout.setUserId(SecurityUtils.getUserId());
        return iotProductLayoutMapper.deleteIotProductLayoutByProductIdAndUserId(iotProductLayout);
    }
}
