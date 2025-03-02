package tech.hciot.dwis.business.application;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.HardnessControlRepository;
import tech.hciot.dwis.business.domain.model.HardnessControl;

@Service
@Slf4j
public class HardnessControlService {

  @Autowired
  private HardnessControlRepository hardnessControlRepository;

  /**
   * 硬度是否在范围内
   * @param design
   * @param hardness
   * @return
   */
  public boolean isHardnessInScope(String design, Integer hardness) {
    Optional<HardnessControl> controlOpt = hardnessControlRepository.findByDesign(design);
    if (controlOpt.isPresent()) {
      HardnessControl control = controlOpt.get();
      if (hardness >= control.getHardnessMin() && hardness <= control.getHardnessMax()) {
        return true;
      }
    }
    return false;
  }
}
