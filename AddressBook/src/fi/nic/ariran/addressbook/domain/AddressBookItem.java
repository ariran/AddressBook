package fi.nic.ariran.addressbook.domain;

import java.util.UUID;

public class AddressBookItem implements Comparable<AddressBookItem> {

	private String internalId = null;
	private String lastName = null;
	private String firstNames = null;
	private String extraInfo = null;
	
	public AddressBookItem(final String uuid, final String lastName, final String firstNames) {
		if (uuid != null) {
			internalId = uuid;
		} else {
			internalId = UUID.randomUUID().toString();
		}
		this.lastName = lastName;
		this.firstNames = firstNames;
		extraInfo = "";
	}

	public String getInternalId() {
		return internalId;
	}

	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getFirstNames() {
		return firstNames;
	}
	
	public void setFirstNames(String firstNames) {
		this.firstNames = firstNames;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	@Override
	public int compareTo(AddressBookItem otherItem) {
        int lastCmp = lastName.compareTo(otherItem.getLastName());
        return (lastCmp != 0 ? lastCmp : firstNames.compareTo(otherItem.getFirstNames()));
    }
}
