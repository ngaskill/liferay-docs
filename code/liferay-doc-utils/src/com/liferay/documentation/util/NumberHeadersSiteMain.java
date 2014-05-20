package com.liferay.documentation.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.Task;

public class NumberHeadersSiteMain extends Task {

	public static void main(String[] args) throws Exception {
		if (args == null || args.length < 4) {
			throw new IllegalArgumentException("Requires 2 arguments: product version purpose doc-set");
		}

		String product = args[0];
		String version = args[1];
		String purpose = args[2];
		String docSet = args[3];

		NumberHeadersSiteMain processor = new NumberHeadersSiteMain(product,
			version, purpose, docSet);

		boolean foundDuplicateIds = false;

		File articlesDir = new File("../" + docSet + "/articles");
		System.out.println(
			"Numbering headers for files in " + articlesDir.getPath() + " ...");

		if (!articlesDir.exists() || !articlesDir.isDirectory()) {
			throw new Exception(
				"FAILURE - bad articles directory " + articlesDir);
		}

		//TODO Create a list of all the markdown files in all the subdirectories. Make sure to use the file's path, not just its name.

		List<String> fileList = new ArrayList(); 

		// Get listing of markdown files

		String[] files = articlesDir.list(new FilenameFilter() {
				String filePatternArg =
					"([0-9]+)([^\\\\\\[\\]\\|:;%<>]+).markdown";
				Pattern fileNamePattern = Pattern.compile(filePatternArg);

				public boolean accept(File file, String name) {
					return (fileNamePattern.matcher(name).matches());
				}
			});

		for (int i = 0; i < files.length; i++) {
			fileList.add(files[i]);
		}

		if (fileList.isEmpty()) {
			throw new Exception(
				"FAILURE - no markdown files found in " + articlesDir);
		}

		//TODO process each file from fileList. Make sure to use the file's path, not just its name.

		// Process each file

		for (int i = 0; i < files.length; i++) {
			String filename = files[i];
			// String inFile = articlesDir.getPath() + "\\" + filename;
			// String outFile = articlesDir.getPath() + "/" + filename;
			File inFile = new File(articlesDir, filename);
			File outFile = new File(articlesDir, filename);
			String outFileTmp = outFile + ".tmp";

			try {
				LineNumberReader in =
					new LineNumberReader(new FileReader(inFile));
				BufferedWriter out =
					new BufferedWriter(new FileWriter(outFileTmp));

				String line;
				while ((line = in.readLine()) != null) {

					if (line.startsWith("#")) {
						
						line = line.trim();
						
						String newHeadingLine = processor.handleHeaderLine(line,
							filename, in.getLineNumber());
						if (newHeadingLine != null) {
							line = newHeadingLine;
						}
						else {
							foundDuplicateIds = true;
						}
					}

					out.append(line);
					out.append("\n");
				}
				in.close();
				
				out.flush();
				out.close();

				// Replace original file with modified temp file

				FileUtils.copyFile(
					new File(outFileTmp),
					new File(articlesDir.getPath() + "/" + filename));

				FileUtils.forceDelete(new File(outFileTmp));
			} catch (IOException e) {
				throw new Exception(e.getLocalizedMessage());
			}
		}

		if (foundDuplicateIds) {
			throw new Exception("FAILURE - Duplicate header IDs exist");
		}
	}

	public NumberHeadersSiteMain(String product, String version, String purpose,
			String docSet) {
		super();

		StringBuffer sb = new StringBuffer();

		sb.append("-");
		sb.append(product.toLowerCase().replace(' ', '-'));
		sb.append("-");
		sb.append(version.replace('.', '-'));
		sb.append("-");
		sb.append(purpose.replace(' ', '-'));
		sb.append("-");
		sb.append(docSet.replace(' ', '-'));

		_id_suffix = sb.toString();
		_id_suffix_len = _id_suffix.length();
	}

	private String extractHeading(String line, int indexOfFirstHeaderChar) {
		String heading2 = line.substring(indexOfFirstHeaderChar);
		heading2 = heading2.trim();

		// Replace each spaced dash, space, dot, and slash with a dash

		heading2 = heading2.replace(" - ", "-");
		heading2 = heading2.replace(' ', '-');
		heading2 = heading2.replace('.', '-');
		heading2 = heading2.replace('/', '-');
		heading2 = heading2.toLowerCase();

		// Filter out characters other than dashes, letters, and digits

		StringBuffer headingSb = new StringBuffer();
		for (int i = 0; i < heading2.length(); i++) {
			char ch = heading2.charAt(i);

			if (ch == '-' || Character.isLetterOrDigit(ch)) {
				headingSb.append(ch);
			}
		}
		heading2 = headingSb.toString();
		return heading2;
	}

	private String handleHeaderLine(String line, String filename,
		int lineNum) throws Exception {

		String newHeadingLine = null;

		// Check if the header contains an ID

		if (headerIdPattern.matcher(line).matches()) {

			// Extract the header ID

			int idStartIndex = line.indexOf("=");
			int idEndIndex = line.indexOf(")", idStartIndex);

			String id = null;
			if (idStartIndex > 0 && idEndIndex > (idStartIndex + 1)) {
				id = line.substring(idStartIndex + 1, idEndIndex);
			}

			if (id.length() > MAX_ID_LEN) {
				StringBuilder sb =
					new StringBuilder("FAILURE - ID longer than ");
				sb.append(MAX_ID_LEN);
				sb.append(" chars in ");
				sb.append(filename);
				sb.append(" - ");
				sb.append(id);
				throw new Exception(sb.toString());
			}
			
			// Check if the ID is already in use

			String filename2 = IDS.get(id);
			if (filename2 != null) {

				//print error
				
				System.out.println("Dup id:" + id + " file:" +
					filename + " line:" + lineNum + " (already used by file:" +
					filename2 + ")");
				return newHeadingLine;
			}
			else {

				// Add the ID

				IDS.put(id, filename);
				newHeadingLine = line;
			}
		}
		else {

			// Generate an ID based on the header text, file chapter
			// number, and counter

			// Find the start of the header text

			int indexOfFirstHeaderChar = -1;
			for (int i = 0; i < line.length(); i++) {
				char ch = line.charAt(i);
				if (ch != '#' && ch != ' ' && ch != '\t') {
					indexOfFirstHeaderChar = i;
					break;
				}
			}

			String heading = null;
			if (indexOfFirstHeaderChar > 0) {
				heading = extractHeading(line, indexOfFirstHeaderChar);
			}
			else {
				throw new Exception("WARNING - "  + filename + ":" +
					lineNum + " is missing header text.");
			}

			int idCount = -1;
			String newHeading = null;
			while (true) {

				newHeading = assembleId(heading, idCount);

				if (IDS.get(newHeading) == null) {

					// Heading is unique

					// Add the ID

					IDS.put(newHeading, filename);

					break;
				}

				idCount++;
			}

			newHeadingLine = line + " [](id=" + newHeading + ")";
		}

		return newHeadingLine;
	}

	private String assembleId(String heading, int idCount) {

		String count = "";
		if (idCount > -1) {
			count = "-" + idCount;
		}

		int idLength = heading.length() + _id_suffix_len + count.length();
		if (idLength >  MAX_ID_LEN) {
			heading = heading.substring(
				0,
				(heading.length() - Math.abs(idLength -  MAX_ID_LEN)));
		}

		StringBuffer sb = new StringBuffer(heading);
		sb.append(_id_suffix);
		sb.append(count);

		return sb.toString();
	}

	private static final int MAX_ID_LEN = 75;

	private static HashMap<String, String> IDS = new HashMap<String, String>();

	private static Pattern headerIdPattern;

	static {
		String patternArg = "(#)+([^\\\\\\[\\]\\|%<>]*)" +
				Pattern.quote("[") + Pattern.quote("]") +
				Pattern.quote("(") + "id" + Pattern.quote("=") +
				"([^\\\\\\[\\]\\|:;%]+)" + Pattern.quote(")") + 
				"([ \\t\\n\\x0B\\f\\r]*?)";

		headerIdPattern = Pattern.compile(patternArg);
	}

	private final String _id_suffix;
	private final int _id_suffix_len;

}
