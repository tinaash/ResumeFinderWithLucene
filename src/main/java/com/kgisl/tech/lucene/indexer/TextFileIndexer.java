package com.kgisl.tech.lucene.indexer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

public class TextFileIndexer {

	/**
	 * This method is for indexing text files
	 * @param writer
	 * @param file
	 * @param lastModified
	 * @throws IOException
	 */
	public static void indexTextFiles(IndexWriter writer, Path file, long lastModified) throws IOException 
	{
		try (InputStream stream = Files.newInputStream(file)) 
		{
			//Create Document
			Document doc = new Document();
			doc.add(new StringField("path", file.toString(), Field.Store.YES));
			doc.add(new LongPoint("modified", lastModified));

			String textFileContent = new String(Files.readAllBytes(file));
			doc.add(new TextField("contents", textFileContent , Store.YES));
			System.out.println("FileContents: \n" + textFileContent);
			doc.add(new StringField("filename", file.getFileName().toString(), Store.YES));

			writer.updateDocument(new Term("path", file.toString()), doc);
			System.out.println("Indexed " + file.getFileName());
		}
		catch (Exception e) {
			System.out.println("Error occured while indexing file: " + file.getFileName());
		}

	}
}