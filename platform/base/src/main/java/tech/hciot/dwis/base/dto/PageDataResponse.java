package tech.hciot.dwis.base.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDataResponse<T> {

  private long total;
  private int pageCount;
  private int pageSize;
  private int currentPage;
  private List<T> data;
}
