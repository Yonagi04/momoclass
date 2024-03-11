package com.momoclass.content.service.impl;

import com.momoclass.content.mapper.CourseCategoryMapper;
import com.momoclass.content.model.dto.CourseCategoryTreeDto;
import com.momoclass.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);

        Map<String, CourseCategoryTreeDto> mapTemp =
                courseCategoryTreeDtos.stream().filter(item->!id.equals(item.getId()
                )).collect(Collectors.toMap(key -> key.getId(), value -> value,
                        (key1, key2) -> key2));
        List<CourseCategoryTreeDto> courseCategoryList = new ArrayList<>();

        courseCategoryTreeDtos.stream().filter(item->!id.equals(item.getId())).forEach(item->{
            if (item.getParentid().equals(id)) {
                courseCategoryList.add(item);
            }

            CourseCategoryTreeDto courseCategoryParent = mapTemp.get(item.getParentid());
            if (courseCategoryParent != null) {
                if (courseCategoryParent.getChildrenTreeNodes() == null) {
                    courseCategoryParent.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }

                courseCategoryParent.getChildrenTreeNodes().add(item);
            }
        });
        return courseCategoryList;
    }
}
