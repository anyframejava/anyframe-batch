package com.sds.anyframe.batch.infra.database.support;

import java.util.ArrayList;
import java.util.List;

/**
 * public getter method
 *
 * @see org.springframework.jdbc.core.namedparam.ParsedSql
 */

@SuppressWarnings("rawtypes")
public class ParsedSql {

	private String originalSql;

	private List parameterNames = new ArrayList();

	private List parameterIndexes = new ArrayList();

	private int namedParameterCount;

	private int unnamedParameterCount;

	private int totalParameterCount;


	/**
	 * Create a new instance of the {@link ParsedSql} class.
	 * @param originalSql the SQL statement that is being (or is to be) parsed
	 */
	ParsedSql(String originalSql) {
		this.originalSql = originalSql;
	}

	/**
	 * Return the SQL statement that is being parsed.
	 */
	public String getOriginalSql() {
		return this.originalSql;
	}


	/**
	 * Add a named parameter parsed from this SQL statement.
	 * @param parameterName the name of the parameter
	 * @param startIndex the start index in the original SQL String
	 * @param endIndex the end index in the original SQL String
	 */
	@SuppressWarnings("unchecked")
	void addNamedParameter(String parameterName, int startIndex, int endIndex) {
		this.parameterNames.add(parameterName);
		this.parameterIndexes.add(new int[] {startIndex, endIndex});
	}

	/**
	 * Return all of the parameters (bind variables) in the parsed SQL statement.
	 * Repeated occurences of the same parameter name are included here.
	 */
	public List getParameterNames() {
		return this.parameterNames;
	}

	/**
	 * Return the parameter indexes for the specified parameter.
	 * @param parameterPosition the position of the parameter
	 * (as index in the parameter names List)
	 * @return the start index and end index, combined into
	 * a int array of length 2
	 */
	public int[] getParameterIndexes(int parameterPosition) {
		return (int[]) this.parameterIndexes.get(parameterPosition);
	}

	/**
	 * Set the count of named parameters in the SQL statement.
	 * Each parameter name counts once; repeated occurences do not count here.
	 */
	void setNamedParameterCount(int namedParameterCount) {
		this.namedParameterCount = namedParameterCount;
	}

	/**
	 * Return the count of named parameters in the SQL statement.
	 * Each parameter name counts once; repeated occurences do not count here.
	 */
	public int getNamedParameterCount() {
		return this.namedParameterCount;
	}

	/**
	 * Set the count of all of the unnamed parameters in the SQL statement.
	 */
	void setUnnamedParameterCount(int unnamedParameterCount) {
		this.unnamedParameterCount = unnamedParameterCount;
	}

	/**
	 * Return the count of all of the unnamed parameters in the SQL statement.
	 */
	public int getUnnamedParameterCount() {
		return this.unnamedParameterCount;
	}

	/**
	 * Set the total count of all of the parameters in the SQL statement.
	 * Repeated occurences of the same parameter name do count here.
	 */
	void setTotalParameterCount(int totalParameterCount) {
		this.totalParameterCount = totalParameterCount;
	}

	/**
	 * Return the total count of all of the parameters in the SQL statement.
	 * Repeated occurences of the same parameter name do count here.
	 */
	public int getTotalParameterCount() {
		return this.totalParameterCount;
	}


	/**
	 * Exposes the original SQL String.
	 */
	public String toString() {
		return this.originalSql;
	}
}
