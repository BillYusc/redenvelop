package com.scc.redenvelop.dao;

import com.scc.redenvelop.dto.GrabRedenvRecordDto;
import com.scc.redenvelop.entity.OriginRedenvRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface IOriginRedenvRecordDao {

    void offerRedenv(OriginRedenvRecord originRedenvRecord);

    @Select("select count(1) from redenvelop.origin_redenv_record where id = #{redEnvelopeId} and user_id =#{offerUserId}")
    Integer checkRedPack(GrabRedenvRecordDto grabRedenv);
}
