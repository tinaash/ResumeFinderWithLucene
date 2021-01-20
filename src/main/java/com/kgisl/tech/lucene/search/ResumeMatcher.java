package com.kgisl.tech.lucene.search;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * @author tina
 * Class to find matching resumes
 */
public class ResumeMatcher 
{
	// Directory which stores the lucene indexes
	private static final String INDEX_DIR = "resumeIndexFiles";

	public static void main(String[] args) {

		Scanner input = new Scanner(System.in);
		System.out.println("Please enter keywords to find matching profiles : ");
		String searchStr = input.nextLine();
		input.close();

		System.out.println("Searching for resumes with keywords: " + searchStr + "\n...");

		IndexSearcher searcher;
		TopDocs foundDocs;

		// Query query = getQuery(searchStr);  // OR Query
		Query query = getExactMatchQuery(searchStr); // AND Query
		try {
			searcher = createSearcher();
			foundDocs = searchInContent(query, searcher);
			System.out.println("Number of Profiles matched: " + foundDocs.totalHits);

			for (ScoreDoc sd : foundDocs.scoreDocs) {
				Document d = searcher.doc(sd.doc);
				System.out.println("Matching Resume : " + d.get("filename") + ", Match Score: " + sd.score);

				List<Term> hitTerms = new ArrayList<Term>();
				List<Term> nohitTerms = new ArrayList<Term>();  //non-matched terms
				getMatchTerms(query, searcher, sd.doc, hitTerms, nohitTerms);
				System.out.println("matching keywords : " + hitTerms.toString());
				System.out.println("non matching keywords : " + nohitTerms.toString() + "\n");
			}

		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static TopDocs searchInContent(Query query, IndexSearcher searcher) throws Exception
	{
		TopDocs hits = searcher.search(query, 10);
		return hits;
	}

	private static Query getQuery(String textToFind) {

		// Create search query
		QueryParser qp = new QueryParser("contents", new StandardAnalyzer());
		Query query = null;
		try {
			query = qp.parse(textToFind);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		return query;
	}

	private static Query getExactMatchQuery(String searchString) {

		QueryParser qp = new QueryParser("contents", new StandardAnalyzer());
		qp.setDefaultOperator(QueryParser.Operator.AND);

		Query query = null;
		try {
			query = qp.parse(searchString);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		return query;
	}

	private static IndexSearcher createSearcher() throws IOException 
	{
		Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
		IndexReader reader = DirectoryReader.open(dir);

		IndexSearcher searcher = new IndexSearcher(reader);
		dir.close();
		return searcher;
	}

	static void getMatchTerms(Query query, IndexSearcher searcher, int docId, List<Term> match, List<Term> noMatch)
			throws IOException {

		if (query instanceof TermQuery) {
			// System.out.println("TermQuery");
			if (searcher.explain(query, docId).isMatch())
				match.add(((TermQuery) query).getTerm());
			else
				noMatch.add(((TermQuery) query).getTerm());
			return;
		}

		if (query instanceof BooleanQuery) {
			// System.out.println("BooleanQuery");
			for (BooleanClause clause : (BooleanQuery) query) {
				getMatchTerms(clause.getQuery(), searcher, docId, match, noMatch);
			}
			return;
		}

		if (query instanceof MultiTermQuery) {
			// System.out.println("MultiTermQuery");
			if (!(query instanceof FuzzyQuery)) 
				((MultiTermQuery) query).setRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_REWRITE);
			getMatchTerms(query.rewrite(searcher.getIndexReader()), searcher, docId, match, noMatch);
		}
	}

}