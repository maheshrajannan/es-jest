/**
 * 
 */
package com.elastic.gateway.domain;

import java.io.Serializable;

/**
 * @author maheshrajannan
 * Implementing this interface indicates that the object can be indexed in elastic search, 
 * in jest.
 */
public interface Indexable extends Serializable{
	String getId();
	void setId(String id);
}
