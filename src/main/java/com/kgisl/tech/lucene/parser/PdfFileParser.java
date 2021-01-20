package com.kgisl.tech.lucene.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * This class parses PDF files and returns text
 */
public class PdfFileParser {

	public static String parsePdfFile(File file) throws IOException {

		System.out.println("Before parsing PDF file: " + file.getName());
		String contents = "";
		PDDocument doc = null;

		try {
			doc = PDDocument.load(file);
			PDFTextStripper stripper = new PDFTextStripper();

			stripper.setLineSeparator("\n");
			stripper.setStartPage(1);
			stripper.setEndPage(6); // index only first 6 pages
			contents = stripper.getText(doc);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			doc.close();
		}
		return contents;
	}

	/**
	 * main method
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void main(String args[]) throws FileNotFoundException, IOException {

		File dir = new File("C:\\Users\\tina\\Documents\\SamplePDFResumes");
		File[] listOfFiles = dir.listFiles();

		if (listOfFiles == null) {
			System.out.println("No files present in the directory path");
		} else {
			for (int i = 0; i < listOfFiles.length; i++) {

				File file = listOfFiles[i];

				System.out.println(file.getCanonicalPath().toString());
				String fileName = file.getName();
				System.out.println("File name: " + fileName);

				if (fileName.endsWith(".pdf")) {
					System.out.println("Before converting " + fileName + " to text");
					String newFileContents = new String(parsePdfFile(file));
					PrintWriter pw = new PrintWriter(
							"C:\\Users\\tina\\Documents\\SamplePDFResumes\\" + fileName.replace(".pdf", "") + ".txt");
					pw.print(newFileContents);
					pw.close();
				}
			}
		}
	}
}