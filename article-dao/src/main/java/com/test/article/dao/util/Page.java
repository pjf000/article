package com.test.article.dao.util;

import java.util.List;

//start: 开始记录的起始数，如第 20 条,从0开始
//limit : 单页多少条记录
//pageIndex : 第几页，同start参数重复，可以选择其中一个使用
//传回的数据集合
//常见形式：
//
//{
//  "rows" : [{},{}], //数据集合
//  "results" : 100, //记录总数
//  "hasError" : false, //是否存在错误
//  "error" : "" // 仅在 hasError : true 时使用
//}
public class Page<T> implements Pagination {

	protected int start;
	/** 页码 */
	protected int pageIndex;
	/** 每页记录条数 */
	protected int limit;
	/** 总页数 */
	protected int totalPage;
	/** 总记录条数 */
	protected int results = 0;

	/** 用于存放查询结果 */
	protected List<T> rows;

	public Page(int pageIndex, int limit) {
		if (pageIndex <= 0) {
			throw new IllegalArgumentException(
					"pageIndex must be greater than 0.");
		}
		if (limit <= 0) {
			throw new IllegalArgumentException("limit must be greater than 0.");
		}
		this.pageIndex = pageIndex;
		this.limit = limit;
	}

	public Page() {
		this(1, 10);
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getResults() {
		return results;
	}

	public void setResults(int results) {
		this.results = results;
		if (results < 0) { // 如果总数为负数, 表未设置
			totalPage = 0;
		} else { // 计算总页数
			this.totalPage = (results / limit)+ (results % limit == 0 ? 0 : 1);
		}
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	@Override
	public String toString() {
		return "Page [pageIndex=" + pageIndex + ", limit=" + limit
				+ ", totalPage=" + totalPage + ", results=" + results
				+ ", rows=" + rows + "]";
	}

	public Page<T> bulid(List<T> rows) {
		setRows(rows);
		return this;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}



}