package tech.hciot.dwis.business.application;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.NotifyStatusRepository;
import tech.hciot.dwis.business.domain.TechnicalDocumentInfoRepository;
import tech.hciot.dwis.business.domain.TechnicalDocumentRepository;
import tech.hciot.dwis.business.domain.model.NotifyStatus;
import tech.hciot.dwis.business.domain.model.TechnicalDocument;
import tech.hciot.dwis.business.domain.model.TechnicalDocumentInfo;
import tech.hciot.dwis.business.infrastructure.FileUtil;

@Service
@Slf4j
public class TechnicalDocumentService {

  @Value("${dwis.staticFilePath}")
  private String staticFilePath;

  @Autowired
  private TechnicalDocumentRepository technicalDocumentRepository;

  @Autowired
  private TechnicalDocumentInfoRepository technicalDocumentInfoRepository;

  @Autowired
  private NotifyStatusRepository notifyStatusRepository;

  public Page<TechnicalDocumentInfo> find(String accountId, Integer currentPage, Integer pageSize) {
    Specification<TechnicalDocumentInfo> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (accountId != null) {
        list.add(criteriaBuilder.equal(root.get("accountId"), accountId));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return technicalDocumentInfoRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public Page<TechnicalDocument> findForMgr(Integer currentPage, Integer pageSize) {
    Specification<TechnicalDocument> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return technicalDocumentRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public Integer add(TechnicalDocument technicalDocument) {
    technicalDocument.setCreateTime(new Date());
    TechnicalDocument savedTechnicalDocument = technicalDocumentRepository.save(technicalDocument);
    return savedTechnicalDocument.getId();
  }

  public void add(String title, String author, List<Integer> depList, MultipartFile file) {
    try {
      TechnicalDocument technicalDocument = TechnicalDocument.builder()
        .title(title)
        .author(author)
        .department(depList)
        .filename(FileUtil.generateFilename(file, "techdoc"))
        .content(file.getBytes())
        .publishStatus(TechnicalDocument.STATUS_UNPUBLISH)
        .createTime(new Date())
        .build();
      technicalDocumentRepository.save(technicalDocument);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw PlatformException.badRequestException("技术文件上传失败");
    }
  }

  @Transactional
  public void delete(Integer id) {
    notifyStatusRepository.deleteByNotifyIdAndNotifyType(id, 2);
    technicalDocumentRepository.deleteById(id);
  }

  @Transactional
  public void publish(Integer id) {
    technicalDocumentRepository.publish(id);
  }

  @Transactional
  public TechnicalDocument findById(Integer id, String accountId) {
    notifyStatusRepository.read(id, NotifyStatus.TYPE_TECHNOLIGY_DOCUMENT, accountId);
    return technicalDocumentRepository.findById(id).get();
  }

  public void content(String filename, HttpServletResponse response) {
    response.setHeader("content-type", "application/pdf");
    TechnicalDocument technicalDocument = technicalDocumentRepository.findByFilename(filename)
      .orElseThrow(() -> PlatformException.badRequestException("技术文件不存在"));
    byte[] b = technicalDocument.getContent();
    try (ServletOutputStream os = response.getOutputStream();
         InputStream is = new ByteArrayInputStream(b);) {
      int len;
      while( (len = is.read(b)) != -1) {
        os.write(b, 0, len);
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }
}
