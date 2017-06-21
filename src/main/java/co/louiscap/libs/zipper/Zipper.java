package co.louiscap.libs.zipper;

import java.io.*;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * A simple wrapper interface for a {@link java.util.zip.ZipOutputStream}, abstracting away
 * the implementation details and allowing a zip file to be created from a series of paths
 */
public final class Zipper {
	/**
	 * The size of created input buffers, in bytes
	 */
	private int bufferSize;
	/**
	 * How files will be added to any ZIP streams
	 */
	private int storageMethod;
	/**
	 * The prefix that should be removed from all paths before being added to the ZIP
	 */
	private String prefix;

	/**
	 * Create a new Zipper for compressing files that will use a 2048 byte buffer and will defualt to deflating
	 * files added to ZIP archives
	 */
	public Zipper() {
		this(2048, ZipOutputStream.DEFLATED);
	}

	/**
	 * Create a new Zipper for compressing files that will default to deflating files added to ZIP archives
	 * @param bufferSize
	 */
	public Zipper(int bufferSize) {
		this(bufferSize, ZipOutputStream.DEFLATED);
	}

	/**
	 * Create a new Zipper for compressing files
	 */
	public Zipper(int bufferSize, int storageMethod) {
		this.bufferSize = bufferSize;
		this.storageMethod = storageMethod;
		this.prefix = null;
	}

	/**
	 * Change the storage method used to create the zip file. Defaults to {@link java.util.zip.ZipOutputStream#DEFLATED}
	 *
	 * @param storageMethod Should be either {@link java.util.zip.ZipOutputStream#DEFLATED} or
	 * {@link java.util.zip.ZipOutputStream#STORED}, other values may have unexpected results
	 */
	public final void setStorageMethod(int storageMethod) {
		this.storageMethod = storageMethod;
	}

	/**
	 * Change how large the input read buffer will be (in bytes) when adding files to the ZIP archive.
	 * Defaults to 2048 bytes
	 *
	 * @param bufferSize
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	/**
	 * Set the path prefix that should be removed from the start of all input file paths. This is useful if you're
	 * receiving these paths from an external service that, for example, provides only absolute file paths. Such
	 * paths would otherwise cause issues when unzipping, because the name would be the full path on your system, not
	 * the relative path to where the documents should be unzipped.
	 * @param prefix
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Creates a ZIP file at the given output path, containing all of the specified files.
	 *
	 * @param outputPath The path at which the ZIP file will be written
	 * @param inputPaths The paths of the files to compress into the resultant ZIP file
	 * @throws IOException
	 * @return The checksum of the created ZIP archive
	 */
	public final void compress(final String outputPath, final String[] inputPaths) throws IOException {
		try (FileOutputStream outStream = new FileOutputStream(outputPath)) {
			compress(outStream, inputPaths);
		}
	}

	/**
	 *
	 * @param outputPath
	 * @param inputFileData
	 * @throws IOException
	 * @return The checksum of the created ZIP archive
	 */
	public final void compress(final String outputPath, final Map<String, byte[]> inputFileData) throws IOException {
		try (FileOutputStream outStream = new FileOutputStream(outputPath)) {
			compress(outStream, inputFileData);
		}
	}

	/**
	 *
	 * @param outStream
	 * @param inputFileData
	 * @throws IOException
	 */
	public final void compress(final OutputStream outStream, final Map<String, byte[]> inputFileData) throws IOException {
		try (
				BufferedOutputStream bufferedStream = new BufferedOutputStream(outStream);
				ZipOutputStream output = new ZipOutputStream(bufferedStream)
		) {
			byte[] buffer = new byte[bufferSize];

			for (Map.Entry<String, byte[]> inputFile : inputFileData.entrySet()) {
				ZipEntry entry = new ZipEntry(inputFile.getKey());
				output.putNextEntry(entry);
				try (InputStream input = new ByteArrayInputStream(inputFile.getValue())) {
					int count;
					while ((count = input.read(buffer, 0, bufferSize)) != -1) {
						output.write(buffer, 0, count);
					}
				}
			}
		}
	}

	/**
	 * Write the contents of a ZIP file to the given output stream, containing all of the specified files.
	 *
	 * @param outStream The stream to which the ZIP archive will be output
	 * @param inputPaths The paths of the files to compress into the resultant ZIP file
	 * @throws IOException
	 * @return The checksum of the created ZIP archive
	 */
	public final void compress(final OutputStream outStream, final String[] inputPaths) throws IOException {
		try (
			BufferedOutputStream bufferedStream = new BufferedOutputStream(outStream);
			ZipOutputStream output = new ZipOutputStream(bufferedStream)
		) {

			byte[] buffer = new byte[bufferSize];

			for (String inputPath : inputPaths) {
				String name = prefix == null ? inputPath : inputPath.replaceFirst(Pattern.quote(prefix), "");

				try (
						FileInputStream inStream = new FileInputStream(inputPath);
						BufferedInputStream input = new BufferedInputStream(inStream)
				) {
					ZipEntry entry = new ZipEntry(name);
					output.putNextEntry(entry);

					int count;
					while ((count = input.read(buffer, 0, bufferSize)) != -1) {
						output.write(buffer, 0, count);
					}
				}
			}
		}
	}
}
