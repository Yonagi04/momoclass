package com.momoclass.messagesdk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.momoclass.messagesdk.model.po.MqMessage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description
 * @date 2024/03/22 19:04
 */
public interface MqMessageMapper extends BaseMapper<MqMessage>{
    @Select("SELECT t.* FROM mq_message t WHERE t.id % #{shardTotal} = #{shardindex} and t.state='0' and t.message_type=#{messageType} limit #{count}")
    List<MqMessage> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardindex") int shardindex, @Param("messageType") String messageType, @Param("count") int count);
}
