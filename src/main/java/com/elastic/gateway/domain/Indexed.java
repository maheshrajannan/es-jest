/**
 * 
 */
package com.elastic.gateway.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import io.searchbox.annotations.JestId;

/**
 * @author maheshrajannan
 *
 */
public abstract class Indexed implements Indexable {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -7989323896269815385L;
	/**
	 * id.
	 */
	@JestId
	private String id=null;

	/* (non-Javadoc)
	 * @see com.elastic.gateway.domain.Indexable#getId()
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.elastic.gateway.domain.Indexable#setId(java.lang.String)
	 */
	public void setId(String id) {
		this.id=id;
	}
	public String toString(){
	     return new ToStringBuilder(this).
	    	       append(getId()).
	    	       toString();
	}
}
