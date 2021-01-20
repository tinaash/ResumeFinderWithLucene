package com.kgisl.tech.lucene.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * Class for parsing word documents that are in .doc and .docx format 
 * @author tina
 * 
 */
public class DocFileParser {

	/**
	 * Method to parse contents of the .doc and .docx files
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String parseDocxFile(String fileName) throws IOException {

		System.out.println("Before parsing MS word file: " + fileName);
		String docContents = "";
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(fileName);
			XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));
			XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
			docContents = extractor.getText();
		 //	System.out.println("Document contents: " + docContents);
		} 
		catch(Exception ex) {
			ex.printStackTrace();
		} 
		finally {
			fis.close();
		}
		return docContents;
	}

	/**
	 * main method
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void main(String args[]) throws FileNotFoundException, IOException {

		File dir = new File("D:\\tina\\KGInd_R2_WS\\Q3 Lucene\\LuceneExamples\\resumes");
		File[] listOfFiles = dir.listFiles();

		if (listOfFiles == null) {
			System.out.println("No files present in the directory path");
		} else {
			for (int i = 0; i < listOfFiles.length; i++) {

				File file = listOfFiles[i];

				//String fileName = file.getName();
				String fileName = file.getCanonicalPath().toString();
				System.out.println("File name: " + fileName);

				if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
					System.out.println("Before converting " + fileName + " to text");

					String newFileContents = new String(parseDocxFile(fileName));
					System.out.println("newFileContents: \t " + newFileContents);

					String textFileName = (fileName.endsWith(".doc") ? fileName.replace(".doc", "") : fileName.replace(".docx", "")) + ".txt";

					PrintWriter prtWriter = new PrintWriter(textFileName);
					prtWriter.println(newFileContents);
					prtWriter.close();
				}
			}
		}
	}
}