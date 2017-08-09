package com.test.article.dao.util;
/**
 * 
 * <b>Descriptions:</b>
 * <p></p>
 * @author sawyer
 * @createDate 2016年8月7日
 * @file Pagination.java
 * @package com.management.common
 * @project management
 * @version 1.0
 *
 */
public interface Pagination {

	/**
	 * @return 当前页码
	 */
	int getPageIndex();

	/**
	 * @return 每页记录数
	 */
	int getLimit();

	/**
	 * @return 总记录数: 负数表尚未设置, 挡截器会使用count语句统计总数; 0或正整数表总数已设置, 挡截器将不会统计总数.
	 */
	int getResults();

	/**
	 * @param results 设置记录总数
	 */
	void setResults(int results);
}