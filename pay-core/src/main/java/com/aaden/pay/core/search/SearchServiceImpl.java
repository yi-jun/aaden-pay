package com.aaden.pay.core.search;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.prop.SimpleProperty;
import com.aaden.pay.core.search.exception.SearchException;
import com.aaden.pay.core.search.model.IndexModel;
import com.aaden.pay.core.utils.FileUtils;

/**
 *  @Description 基于lucene的搜索服务类
 *  @author aaden
 *  @date 2017年12月8日
 */
@Service("searchService")
public class SearchServiceImpl implements SearchService {

	protected SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	String idStr = "id", keyWordStr = "keywords", bodyStr = "contents", cityStr = "city", bankStr = "bank";

	String indexPath = SimpleProperty.getProperty("lucene_index_dir");

	Directory directory = null;

	Analyzer analyzer = new StandardAnalyzer();

	public SearchServiceImpl() {

		indexPath = FileUtils.getClassOrSystemPath(indexPath);
		if (indexPath == null) {
			throw new IllegalArgumentException("索引不存在:" + indexPath);
		}

		if (indexPath.startsWith("/")) {
			indexPath = indexPath.substring(1);
		}
	}

	@Override
	public void createIndex(List<IndexModel> models) throws SearchException {

		List<Document> list = new ArrayList<Document>();
		for (IndexModel index : models) {
			Document document = new Document();
			document.add(new StringField(idStr, index.getId(), Field.Store.YES));
			document.add(new StringField(bodyStr, index.getIndexBody(), Field.Store.YES));
			document.add(new StringField(cityStr, index.getCityCode(), Field.Store.YES));
			document.add(new StringField(bankStr, index.getBankCode(), Field.Store.YES));
			document.add(new TextField(keyWordStr, index.getIndexStr(), Field.Store.YES));
			if (logger.isDebugEnabled()) {
				logger.debug("新建索引,关键字:" + index.getIndexStr());
			}
			list.add(document);
		}

		IndexWriter writer = null;
		try {
			Path p = FileSystems.getDefault().getPath(indexPath);
			directory = FSDirectory.open(p);
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			writer = new IndexWriter(directory, iwc);
			writer.addDocuments(list);
		} catch (Exception e) {
			logger.error("createIndex IOException:", e);
			throw new SearchException(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					logger.error("createIndex IOException:", e);
				}
			}
			if (directory != null) {
				try {
					directory.close();
				} catch (IOException e) {
					logger.error("createIndex IOException:", e);
				}
			}
		}

	}

	@Override
	public List<IndexModel> queryBrank(String cityCode, String bankCode, String keyWord) {
		long bt = System.currentTimeMillis();
		List<IndexModel> rtnList = new ArrayList<IndexModel>();
		try {
			Path p = FileSystems.getDefault().getPath(indexPath);
			directory = FSDirectory.open(p);
			DirectoryReader ireader = DirectoryReader.open(directory);
			IndexSearcher isearcher = new IndexSearcher(ireader);

			// 构造一个布尔查询
			BooleanQuery query = new BooleanQuery();

			if (StringUtils.isNotBlank(cityCode)) {
				Query subQuery = new TermQuery(new Term(cityStr, cityCode));// 精确查询
				query.add(subQuery, BooleanClause.Occur.MUST);
			}
			if (StringUtils.isNotBlank(bankCode)) {
				Query subQuery = new TermQuery(new Term(bankStr, bankCode));// 精确查询
				query.add(subQuery, BooleanClause.Occur.MUST);
			}
			if (StringUtils.isNotBlank(keyWord)) {
				Query subQuery = new QueryParser(keyWordStr, analyzer).parse("\"" + keyWord + "\"");// 模糊查询
				query.add(subQuery, BooleanClause.Occur.MUST);
			}
			TopDocs result = isearcher.search(query, 256);
			if (result != null) {
				ScoreDoc[] resultList = result.scoreDocs;
				Document hitDoc = null;
				IndexModel indexs = null;
				for (int i = 0; i < resultList.length; i++) {
					hitDoc = isearcher.doc(resultList[i].doc);
					indexs = new IndexModel();
					indexs.setIndexStr(hitDoc.get(keyWordStr));
					indexs.setIndexBody(hitDoc.get(bodyStr));
					rtnList.add(indexs);
				}
			}
			ireader.close();
			directory.close();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("全文检索,城市:%s 银行:%s 关键字:%s 查询%s个结果.查询耗时:%s ms......", cityCode, bankCode, keyWord, rtnList.size(), (System.currentTimeMillis() - bt)));
			}
		} catch (Exception e) {
			logger.error("queryByText Exception:", e);
		} finally {
			if (directory != null) {
				try {
					directory.close();
				} catch (IOException e) {
					logger.error("createIndex IOException:", e);
				}
			}
		}
		return rtnList;
	}
}
