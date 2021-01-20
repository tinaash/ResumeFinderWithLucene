package com.kgisl.tech.lucene.indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import com.kgisl.tech.lucene.parser.DocFileParser;
import com.kgisl.tech.lucene.parser.PdfFileParser;

public class PdfDocFileIndexer {

	/**
	 * This method is for indexing PDF and Ms Doc files
	 * @param writer
	 * @param file
	 * @param lastModified
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void indexPdfDocFiles(IndexWriter writer, File file, long lastModified) throws FileNotFoundException, IOException {

		String fileContent = null;
		
		try {
			Document doc = new Document();

			if (file.getName().endsWith(".doc") || file.getName().endsWith(".docx")) {
				fileContent = DocFileParser.parseDocxFile(file.getAbsolutePath());
			}
			else if (file.getName().endsWith(".pdf")) {
				fileContent = PdfFileParser.parsePdfFile(file);
			}
			System.out.println("FileContents: \n" + fileContent);
			doc.add(new StringField("path", file.toString(), Field.Store.YES));
			doc.add(new LongPoint("modified", lastModified));
			doc.add(new TextField("contents", fileContent, Store.YES));
		    doc.add(new StringField("filename", file.getName(), Store.YES));

			if (doc != null) {
				//writer.addDocument(doc);
				writer.updateDocument(new Term("path", file.toString()), doc);
			}
			System.out.println("Indexed " + file.getAbsolutePath());
		} catch (Exception e) {
			System.out.println("Error occured while indexing file: " + file.getAbsolutePath());
		}
	}

}