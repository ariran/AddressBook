package fi.nic.ariran.addressbook.persistence;

public class PersistenceServiceFactory {

	public static PersistenceService getInstance() {
		return new NeoPersistenceService("/var/neo4j");
	}
}
