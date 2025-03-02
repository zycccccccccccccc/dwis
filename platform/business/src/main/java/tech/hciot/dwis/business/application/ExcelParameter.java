package tech.hciot.dwis.business.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelParameter {
  public static final int PAGING_TYPE_ALL_SAME = 0; // 分页类型：每一页的模板是一样的
  public static final int PAGING_TYPE_FIRST_PAGE = 1; // 分页类型：第一页不同，其它页是一样的

  @Builder.Default
  public int pagingType = PAGING_TYPE_ALL_SAME; // 分页类型
  @Builder.Default
  public int totalRowSum = 0; // 总行数
  @Builder.Default
  public int totalColumnSum = 0; // 总列数
  @Builder.Default
  public int contentStartRow = 0; // 内容部分第一行行号
  @Builder.Default
  public int contentRowSum = 0; // 内容部分总行数

  @Builder.Default
  public int otherPageTotalRowSum = 0; // 其它页总行数
  @Builder.Default
  public int otherPageContentStartRow = 0; // 其它页内容部分第一行行号
  @Builder.Default
  public int otherPageContentRowSum = 0; // 其它页内容部分总行数

  @Builder.Default
  public int contentColumnSum = 1; // 内容部分分成几列
}
