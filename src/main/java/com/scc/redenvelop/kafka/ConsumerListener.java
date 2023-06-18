package com.scc.redenvelop.kafka;

import com.scc.redenvelop.dao.IGrabRedenvRecordDao;
import com.scc.redenvelop.dao.IUserAccountDao;
import com.scc.redenvelop.dto.PartRedEnvDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.scc.redenvelop.utils.JsonObjectUtil.jsonToObj;

@Slf4j
@Component
public class ConsumerListener {

    @Autowired
    IUserAccountDao accountDao;
    @Autowired
    IGrabRedenvRecordDao grabRedenvRecordDao;

    /**
     * 红包入账消费者
     */
    @KafkaListener(topics = "grab")
    public void income(ConsumerRecord<?, ?> record) throws IOException {
        log.info("topic is {}, offset is {}, value is {}", record.topic(), record.offset(), record.value());
        PartRedEnvDto partRedEnvDto = (PartRedEnvDto) jsonToObj(PartRedEnvDto.class, (String) record.value());
        grabRedenvRecordDao.saveGrabRecord(partRedEnvDto);
        accountDao.income(partRedEnvDto);
    }
}

