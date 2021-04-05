package repository.solr;

@org.springframework.stereotype.Repository
public interface ReportSolrRepository
    extends org.springframework.data.solr.repository.SolrCrudRepository<models.Report, Long> {}
