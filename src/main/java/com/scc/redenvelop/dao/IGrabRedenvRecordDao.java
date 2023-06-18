package com.scc.redenvelop.dao;

import com.scc.redenvelop.dto.PartRedEnvDto;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IGrabRedenvRecordDao {
    @Insert("INSERT INTO redenvelop.grab_redenv_record " +
            "(env_id, amount, user_who_get, create_time) VALUES " +
            "(#{redEnvelopeId}, #{amount}, #{userId}, now())")
    void saveGrabRecord(PartRedEnvDto partRedEnvDto);
}
