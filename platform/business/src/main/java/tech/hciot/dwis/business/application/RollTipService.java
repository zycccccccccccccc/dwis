package tech.hciot.dwis.business.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.hciot.dwis.business.domain.RollTipRepository;
import tech.hciot.dwis.business.domain.model.RollTip;

@Service
@Slf4j
public class RollTipService {

  @Autowired
  private RollTipRepository rollTipRepository;

  public String find() {
    return rollTipRepository.findById(1).orElse(RollTip.builder().build()).getTip();
  }

  @Transactional
  public void add(RollTip rollTip) {
    RollTip oldRollTip = rollTipRepository.findById(1).orElse(RollTip.builder().build());
    oldRollTip.setTip(rollTip.getTip());
    rollTipRepository.save(oldRollTip);
  }
}
