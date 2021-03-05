package repository.solr;

@org.springframework.stereotype.Repository
public interface StudentSolrRepository
    extends org.springframework.data.solr.repository.SolrCrudRepository<models.Student, Long> {}
