package prospring.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import prospring.beans.Contact;
import prospring.beans.ContactTelDetail;
import prospring.dao.ImplSpringSQL.*;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("contactDao")
public class JdbcContactDao implements ContactDao, InitializingBean {

    private DataSource dataSource;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private SelectAllContacts selectAllContacts;
    private SelectContactByFirstName selectContactByFirstName;
    private UpdateContact updateContact;
    private InsertContact insertContact;
    private InsertContactTelDetail insertContactTelDetail;
    private StoredFunctionFirstNameByid storedFunctionFirstNameByid;

    private Log LOG = LogFactory.getLog(JdbcContactDao.class);

    public JdbcContactDao() {
    }

    @Resource(name = "dataSource")
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.selectAllContacts = new SelectAllContacts(dataSource);
        this.selectContactByFirstName = new SelectContactByFirstName(dataSource);
        this.updateContact = new UpdateContact(dataSource);
        this.insertContact = new InsertContact(dataSource);
        this.insertContactTelDetail = new InsertContactTelDetail(dataSource);
        this.storedFunctionFirstNameByid = new StoredFunctionFirstNameByid(dataSource);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public List<Contact> findAll() {
        return selectAllContacts.execute();
    }


    @Override
    public List<Contact> findAllWithDetail() {
        String sql = "select c.id, c.first_name, c.last_name, c.birth_date" +
                ", t.id as contact_tel_id, t.tel_type, t.tel_number from contact c " +
                "left join contact_tel_detail t on c.id = t.contact_id";
        return namedParameterJdbcTemplate.query(sql, new ContactWithDetailExtractor());
    }

    @Override
    public List<Contact> findByFirstName(String firstName) {
        Map<String, Object> param = new HashMap<>();
        param.put("first_name", firstName);
        return selectContactByFirstName.executeByNamedParam(param);
    }

    @Override
    public String findLastNameById(Long id) {
        String sql = "select last_name from contact where id=:id";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("id", id);

        return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, String.class);

    }

    @Override
    public String findFirstNameById(Long id) {

        List<String> result = storedFunctionFirstNameByid.execute(id);
        return result.get(0);

    }

    @Override
    public void insert(Contact contact) {
        Map<String, Object> param�ap = new HashMap<String, Object>();
        param�ap.put("first_name", contact.getFirstName());
        param�ap.put("last_name", contact.getLastName());
        param�ap.put("birth_date", contact.getBirthDate());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        insertContact.updateByNamedParam(param�ap, keyHolder);
        contact.setId(keyHolder.getKey().longValue());
        LOG.info("New contact inserted with id: " + contact.getId());
    }

    @Override
    public void insertWithDetail(Contact contact) {
        insertContactTelDetail = new InsertContactTelDetail(dataSource);
        Map<String, Object> param�ap = new HashMap<String, Object>();
        param�ap.put("first_name", contact.getFirstName());
        param�ap.put("last_name", contact.getLastName());
        param�ap.put("birth_date", contact.getBirthDate());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        insertContact.updateByNamedParam(param�ap, keyHolder);
        contact.setId(keyHolder.getKey().longValue());
        LOG.info("New contact inserted wi th id: " + contact.getId());
        List<ContactTelDetail> contactTelDetails = contact.getContactTelDetails();
        if (contactTelDetails != null) {
            for (ContactTelDetail contactTelDetail : contactTelDetails) {
                param�ap = new HashMap<String, Object>();
                param�ap.put("contact_id", contact.getId());
                param�ap.put("tel_type", contactTelDetail.getTelType());
                param�ap.put("tel_number", contactTelDetail.getTelNumber());
                insertContactTelDetail.updateByNamedParam(param�ap);
                insertContactTelDetail.flush();
            }
        }
    }

    @Override
    public void updateWithDetail(Contact contact) {

    }

    @Override
    public void update(Contact contact) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("first_name", contact.getFirstName());
        paramMap.put("last_name", contact.getLastName());
        paramMap.put("birth_date", contact.getBirthDate());
        paramMap.put("id", contact.getId());
        updateContact.updateByNamedParam(paramMap);
        LOG.info("Existing contact updated wi th id: " + contact.getId());
    }

    @Override
    public void delete(Long contactId) {

    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if (dataSource == null) {
            throw new BeanCreationException("Must set datasource on ContactDao!");
        }
        if (namedParameterJdbcTemplate == null) {
            throw new BeanCreationException("Null namedParameterJdbcTemplate on ContactDao");
        }
    }

    private static final class ContactMapper implements RowMapper<Contact> {

        @Override
        public Contact mapRow(ResultSet resultSet, int i) throws SQLException {
            Contact contact = new Contact();
            contact.setId(resultSet.getLong("id"));
            contact.setFirstName(resultSet.getString("first_name"));
            contact.setLastName(resultSet.getString("last_name"));
            contact.setBirthDate(resultSet.getDate("birth_date"));
            return contact;
        }
    }

    private static final class ContactWithDetailExtractor implements ResultSetExtractor<List> {
        @Override
        public List extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Long, Contact> map = new HashMap<Long, Contact>();
            Contact contact = null;
            while (rs.next()) {
                Long id = rs.getLong("id");
                contact = map.get(id);
                if (contact == null) {
                    contact = new Contact();
                    contact.setId(id);
                    contact.setFirstName(rs.getString("first_name"));
                    contact.setLastName(rs.getString("last_name"));
                    contact.setBirthDate(rs.getDate("birth_date"));
                    contact.setContactTelDetails(new ArrayList<ContactTelDetail>());
                    map.put(id, contact);
                }
                Long contactTelDetailid = rs.getLong("contact_tel_id");
                if (contactTelDetailid > 0) {
                    ContactTelDetail contactTelDetail = new ContactTelDetail();
                    contactTelDetail.setId(contactTelDetailid);
                    contactTelDetail.setContactId(id);
                    contactTelDetail.setTelType(rs.getString("tel_type"));
                    contactTelDetail.setTelNumber(rs.getString("tel_number"));
                    contact.getContactTelDetails().add(contactTelDetail);
                }
            }
            return new ArrayList<Contact>(map.values());
        }
    }

}

