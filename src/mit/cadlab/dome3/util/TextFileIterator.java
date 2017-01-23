// TextFileIterator.java
// Copyright (c) 2004 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * The TextFileIterator allows one to iterate through the lines of a text file without
 * loading it all in memory. The file is closed when the last line is read. If the file
 * is not read to the end, the file can be closed via the close method.
 */
public class TextFileIterator implements Iterator
{

	private BufferedReader in = null;
	private boolean hasMoreLines = true; // start optimistic!
	private String nextLine;

	public TextFileIterator(String s)
	{
		this(new File(s));
	}

	public TextFileIterator(File f)
	{
		try {
			in = new BufferedReader(new FileReader(f));
			readNextLine(); // will set hasMoreLines to false if end of file is reached
		}
		catch (FileNotFoundException ex) {
			throw new RuntimeException("File not found: " + f.getAbsolutePath());
		}
		catch (IOException ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	/**
	 * This method should be called before next() to ensure there is a next line.
	 * @return true if there is another line to be read; otherwise, returns false
	 */
	public boolean hasNext()
	{
		return hasMoreLines;
	}

	/**
	 * @return the next line in the file; this is always a String
	 * @throws NoSuchElementException if no more lines to be read
	 */
	public Object next() throws NoSuchElementException
	{
		if (hasMoreLines) {
			String currentLine = nextLine;
			readNextLine();
			return currentLine;
		}
		else
			throw new NoSuchElementException("End of file has been reached.");
	}

	/**
	 * Unsupported operation.
	 */
	public void remove()
	{
		throw new UnsupportedOperationException("TextFileIterator.remove() not supported");
	}

	/**
	 * @return the next line in the file but does not move the current iterator point
	 * @throws NoSuchElementException if no more lines to be read
	 */
	public String peek() throws NoSuchElementException
	{
		if (hasMoreLines) {
			return nextLine;
		}
		else
			throw new NoSuchElementException("End of file has been reached.");
	}

	/**
	 * Closes the file. This method does not need to be called if the iterator is used to
	 * traverse the entire file. This method should be called if the iterator does not
	 * traverse to the end of the file (hasNext() would still return true).
	 */
	public void close() {
		try {
			in.close();
		}
		catch (IOException e) {
			// ignore
		}
	}

	/**
	 * Reads the next line and closes file if there are no more lines to be read
	 */
	private void readNextLine()
	{
		try {
			nextLine = in.readLine();
			if (nextLine == null) {
				hasMoreLines = false;
				in.close();
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

}
