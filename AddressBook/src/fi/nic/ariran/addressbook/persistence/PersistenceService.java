package fi.nic.ariran.addressbook.persistence;

import fi.nic.ariran.addressbook.domain.AddressBookItem;

public interface PersistenceService {

	void insertNewItem(AddressBookItem bean);
	
	Iterable<AddressBookItem> getAllItems();

	void close();
}
