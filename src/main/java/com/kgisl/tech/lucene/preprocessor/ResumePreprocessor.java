package com.kgisl.tech.lucene.preprocessor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import static com.kgisl.tech.lucene.indexer.TextFileIndexer.indexTextFiles;
import static com.kgisl.tech.lucene.indexer.PdfDocFileIndexer.indexPdfDocFiles;

/**
 * @author tina
 *
 */
public class ResumePreprocessor {

	public static void main(String[] args) {

		String docsPath = "resumes";				// Input folder
		String indexPath = "resumeIndexFiles"; 		// Output folder

		final Path docDir = Paths.get(docsPath);

		try {
			// org.apache.lucene.store.Directory instance
			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new StandardAnalyzer();

			// IndexWriter Configuration
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

			// IndexWriter writes new index files to the directory
			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Method to iterate all files and directories and create file Index based on file type
	static void indexDocs(final IndexWriter writer, Path path) throws IOException {

		if (Files.isDirectory(path)) {
			// Iterate directory
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					try {
						System.out.println("File being processed currently: " + file.getFileName());
						String fName = file.getFileName().toString();

						if (fName.endsWith(".txt")) {
							System.out.println("Before indexing text file " + file.getFileName());
							indexTextFiles(writer, file, attrs.lastModifiedTime().toMillis());
						}
						if (fName.endsWith(".doc") || fName.endsWith(".docx") || fName.endsWith(".pdf")) {
							System.out.println("Before indexing doc / pdf file " + file.getFileName());
							indexPdfDocFiles(writer, file.toFile(), attrs.lastModifiedTime().toMillis());
						}

					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			// Index the file
			indexTextFiles(writer, path, Files.getLastModifiedTime(path).toMillis());
		}
	}

}