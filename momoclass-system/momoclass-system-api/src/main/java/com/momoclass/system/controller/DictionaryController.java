package com.momoclass.system.controller;

import com.momoclass.system.model.po.Dictionary;
import com.momoclass.system.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DictionaryController {
    @Autowired
    private DictionaryService dictionaryService;

    @GetMapping("/dictionary/all")
    public List<com.momoclass.system.model.po.Dictionary> queryAll() {
        return dictionaryService.queryAll();
    }

    @GetMapping("/dictionary/code/{code}")
    public Dictionary getByCode(@PathVariable String code) {
        return dictionaryService.getByCode(code);
    }
}
