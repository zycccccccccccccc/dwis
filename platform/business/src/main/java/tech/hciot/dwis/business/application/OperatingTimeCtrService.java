package tech.hciot.dwis.business.application;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.OperatingTimeCtrRepository;
import tech.hciot.dwis.business.domain.model.OperatingTimeCtr;

@Service
@Slf4j
public class OperatingTimeCtrService {

  @Autowired
  private OperatingTimeCtrRepository operatingTimeCtrRepository;

  public Date getQAOperatingTime() {
    return getOperatingTime(OperatingTimeCtr.DEP_QA);
  }

  public Date getMachineOperatingTime() {
    return getOperatingTime(OperatingTimeCtr.DEP_MACHINE);
  }

  public Date getOperatingTime(String dep) {
    int minute = operatingTimeCtrRepository.findByDep(dep)
        .orElseThrow(() -> PlatformException.badRequestException("无法从操作时间控制表中获取到操作时间"))
        .getOperatingTime();
    return DateUtils.addMinutes(new Date(), -minute);
  }

  public int getOperatingTimeNumber(String dep) {
    return operatingTimeCtrRepository.findByDep(dep)
        .orElseThrow(() -> PlatformException.badRequestException("无法从操作时间控制表中获取到操作时间"))
        .getOperatingTime();
  }
}
