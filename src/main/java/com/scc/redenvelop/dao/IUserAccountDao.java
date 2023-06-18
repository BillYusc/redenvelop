package com.scc.redenvelop.dao;

import com.scc.redenvelop.dto.OriginRedenvDto;
import com.scc.redenvelop.dto.PartRedEnvDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface IUserAccountDao {
    @Select("select account from user_account where id = #{userId}")
    Integer getRemaining(Integer userId);

    @Update("update user_account set account = account-#{sum} where id =#{userId}")
    void deduce(OriginRedenvDto originRedenv);

    @Update("update user_account set account = account+#{amount} where id = #{userId}")
    void income(PartRedEnvDto partRedEnvDto);

    @Select("select user_id from origin_redenv_record where id = #{redEnvelopeId}")
    Integer getUserIdByRed(Integer redEnvelopeId);
}
