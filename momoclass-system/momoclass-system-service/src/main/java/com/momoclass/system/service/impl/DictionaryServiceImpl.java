package com.momoclass.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.momoclass.system.mapper.DictionaryMapper;
import com.momoclass.system.model.po.Dictionary;
import com.momoclass.system.service.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService{
    @Override
    public List<Dictionary> queryAll() {
        return null;
    }

    @Override
    public Dictionary getByCode(String code) {
        return null;
    }
}
