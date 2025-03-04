package com.momoclass.content.model.dto;

import com.momoclass.content.model.po.CourseCategory;
import lombok.Data;

import java.util.List;

@Data
public class CourseCategoryTreeDto extends CourseCategory implements java.io.Serializable{

    List<CourseCategoryTreeDto> childrenTreeNodes;

}
