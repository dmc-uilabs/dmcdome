/*
 *  Copyright (c) 2001, Jacob Smullyan.
 *
 *  This is part of SkunkDAV, a WebDAV client.  See http://skunkdav.sourceforge.net/ 
 *  for the latest version.
 * 
 *  SkunkDAV is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation; either version 2, or (at your option)
 *  any later version.
 * 
 *  SkunkDAV is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with SkunkDAV; see the file COPYING.  If not, write to the Free
 *  Software Foundation, 59 Temple Place - Suite 330, Boston, MA
 *  02111-1307, USA.
*/

package org.skunk.util;

import org.skunk.trace.Debug;

/**
 * a container for ints that keeps a gap in the array
 */
public class GappedIntArray
{
    private int[] content;
    static int DEFAULT_GAP_SIZE=80;
    private int preferredGapSize;
    private int currentGapSize;
    private int gapOffset;

    public GappedIntArray()
    {
	this(DEFAULT_GAP_SIZE, 0, null);
    }

    public GappedIntArray(int[] initialContent)
    {
	this(DEFAULT_GAP_SIZE, 0, initialContent);
    }

    public GappedIntArray(int gapSize, int gapOffset, int[] initialContent)
    {
	if (gapSize<1 || gapOffset<0)
	    throw new IllegalArgumentException("illegal arguments in constructor");
	this.preferredGapSize=gapSize;
	this.currentGapSize=gapSize;
	    
	if (initialContent==null)
	{
	    this.gapOffset=0; // argument for gapOffset ignored
	    content=new int[gapSize];
	}
	else
	{
	    this.gapOffset=gapOffset;
	    int initLen=initialContent.length;
	    content=new int[gapSize+initLen];
	    System.arraycopy(initialContent, 0, content, 0, gapOffset);
	    System.arraycopy(initialContent, 
			     gapOffset, 
			     content, 
			     gapOffset+gapSize, 
			     initLen-gapOffset);		
	}
    }

    private int translate(int coordinate)
    {
	return (coordinate<gapOffset) 
	    ? coordinate 
	    : coordinate + currentGapSize;
    }

    public int length()
    {
	return content.length-currentGapSize;
    }

    public int get(int offset) throws ArrayIndexOutOfBoundsException
    {
	return content[translate(offset)];
    }

    public int[] get(int offset, int length) throws ArrayIndexOutOfBoundsException
    {
	int endOffset=offset+length;
	int[] tmp=new int[length];
	if ( gapOffset>endOffset)
	{
	    System.arraycopy(content, offset, tmp, 0, length);	  
	}
	else if (gapOffset<=offset)
	{
	    System.arraycopy(content, offset+currentGapSize, tmp, 0, length);
	}
	else if (gapOffset==offset)
	{
	    System.arraycopy(content, gapOffset+currentGapSize, tmp, 0, length);
	}
	else
	{
	    int firstChunkLength=gapOffset-offset;
	    int secondChunkLength=length-firstChunkLength;
	    System.arraycopy(content, offset, tmp, 0, firstChunkLength);
	    System.arraycopy(content, gapOffset+currentGapSize, tmp, firstChunkLength, secondChunkLength);
	}
	return tmp;
    }

    public synchronized void append(int[] someInts) 
    {
	insertAt(length(), someInts);
    }

    public synchronized void set(int offset, int anInt)
    {
	content[translate(offset)]=anInt;	    
    }

    public synchronized void set(int offset, int[] someInts) throws ArrayIndexOutOfBoundsException
    {
 	if (Debug.DEBUG) Debug.trace(this, Debug.DP8, "in set: offset: {0}, ints to insert: {1}",
				     new Object[] { new Integer(offset), toString(someInts) });

	int endOffset=offset+someInts.length;
	if (endOffset>=length())
	    throw new ArrayIndexOutOfBoundsException("attempt to access beyond end of array; use insertAt instead");
	if (gapOffset<offset)
	{
	    System.arraycopy(someInts, 0, content, currentGapSize+offset, someInts.length);
	}
	else if (gapOffset>=endOffset)
	{
	    System.arraycopy(someInts, 0, content, offset, someInts.length);
	}
	else if (gapOffset==offset)
	{
	    System.arraycopy(someInts, 
			     0, 
			     content, 
			     offset+currentGapSize, 
			     someInts.length);
	}
	else //gapOffset is > offset, or gapOffset < offset && gapOffset<endOffset
	{
	    int firstChunkLength=Math.max(0, gapOffset-offset);
	    int secondChunkLength=someInts.length-firstChunkLength;
	    System.arraycopy(someInts, 0, content, offset, firstChunkLength);
	    System.arraycopy(someInts, 
			     firstChunkLength, 
			     content, 
			     offset+firstChunkLength+currentGapSize, 
			     secondChunkLength);
	}
    }

    public synchronized void remove(int offset, int len)
    {
 	if (Debug.DEBUG) Debug.trace(this, Debug.DP8, "in remove: offset: {0} length: {1}",
 		    new Object[] {new Integer(offset), new Integer(len)});
	if (len==0) return;
	if ((offset+len+currentGapSize)>content.length)
	    throw new ArrayIndexOutOfBoundsException("can't remove that much");
	if (len+currentGapSize<=preferredGapSize)
	{
	    if (offset==gapOffset)
	    {
		currentGapSize+=len;
		return;
	    }
	    else if (offset<gapOffset && offset+len>=gapOffset) // removal is contiguous with gap
	    {
		currentGapSize+=len;
		gapOffset=offset;
		/* 
		 * N.B.: it is possible here for currentGapSize 
		 * to be greater than preferredGapSize
		 */
		return;
	    }
	}
	//have to copy array
	int[] tmp=content;
	content=new int[tmp.length + preferredGapSize - len - currentGapSize];
	if (gapOffset<=offset)
	{
	    //copy from start to gapOffset
	    System.arraycopy(tmp, 0, content, 0, gapOffset); 
	    //skip gap, copy until offset
	    System.arraycopy(tmp, 
			     gapOffset+currentGapSize, 
			     content, 
			     gapOffset, 
			     offset-gapOffset);
	    int srcInd=currentGapSize+offset;
	    //skip removed ints
	    srcInd+=len;
	    //copy the rest
	    System.arraycopy(tmp, 
			     srcInd, 
			     content, 
			     offset+preferredGapSize, 
			     tmp.length-srcInd);
	}
	else 
	{
	    //copy from start to offset
	    System.arraycopy(tmp, 0, content, 0, offset); 		
	    if (offset+len>=gapOffset)    
	    {
		System.arraycopy(tmp, 
				 offset+len+currentGapSize, 
				 content, 
				 offset+preferredGapSize,
				 tmp.length-offset-currentGapSize-len);
	    }
	    else
	    {
		//skip removed items, copy from there to gapOffset
		System.arraycopy(tmp, 
				 offset+len, 
				 content, 
				 offset+preferredGapSize, 
				 gapOffset-offset-len);	
		//copy remainder
		System.arraycopy(tmp, 
				 gapOffset+currentGapSize, 
				 content, 
				 preferredGapSize+gapOffset-len, 
				 tmp.length-gapOffset-currentGapSize);
	    }
	   
	}
	gapOffset= offset;	
	currentGapSize=preferredGapSize;
	
    }

    public int getGapOffset()
    {
	return this.gapOffset;
    }

    public int getCurrentGapSize()
    {
	return this.currentGapSize;
    }
    
    public synchronized void insertAt(int offset, int[] someInts)
    {
 	if (Debug.DEBUG) Debug.trace(this, Debug.DP8, "inserting {0}", toString(someInts));
	int insertLen=someInts.length;
	if (insertLen==0) return;
	if (gapOffset==offset && insertLen<=currentGapSize)
	{
	    System.arraycopy(someInts, 0, content, gapOffset, insertLen);
	    currentGapSize-=insertLen;
	    gapOffset+=insertLen;
	}
	else 
	{
	    int[] tmp=content;
	    content=new int[tmp.length+preferredGapSize+insertLen-currentGapSize];
	    if (gapOffset<=offset)
	    {
		//copy from start to gapOffset
		System.arraycopy(tmp, 0, content, 0, gapOffset); 
		//skip gap, copy until offset
		System.arraycopy(tmp, 
				 gapOffset+currentGapSize, 
				 content, 
				 gapOffset, 
				 offset-gapOffset);
		//do insert
		System.arraycopy(someInts, 0, content, offset, insertLen);  
		int lastOff=offset+currentGapSize; 
		gapOffset= offset+insertLen;
		//copy the remainder
		System.arraycopy(tmp, 
				 lastOff, 
				 content, 
				 gapOffset+preferredGapSize, 
				 tmp.length-lastOff);
	    }
	    else 
	    {
		System.arraycopy(tmp, 0, content, 0, offset); 
		System.arraycopy(someInts, 0, content, offset, insertLen);
		System.arraycopy(tmp, 
				 offset, 
				 content, 
				 offset+insertLen+preferredGapSize, 
				 gapOffset-offset);
		int lastOff=gapOffset+currentGapSize;
		System.arraycopy(tmp, 
				 lastOff, 
				 content, 
				 insertLen+preferredGapSize+gapOffset, 
				 tmp.length-lastOff);			
		gapOffset= offset+insertLen;
	    }
	    
	    currentGapSize=preferredGapSize;
	}
    }

    public int[] toIntArray()
    {
	int[] tmp=new int[content.length-currentGapSize];
	System.arraycopy(content, 0, tmp, 0, gapOffset);
	System.arraycopy(content, 
			 gapOffset+currentGapSize, 
			 tmp, 
			 gapOffset, 
			 tmp.length-gapOffset);
	return tmp;
    }

    public static String toString(int [] array)
    {
	StringBuffer sb=new StringBuffer("[");
	int len=array.length-1;

	for (int i=0;i<=len;i++)
	{
	    sb.append(array[i]);
	    if (i<len)
		sb.append(", ");
	}
	return sb.append(']').toString();
    }

    public String toString()
    {
	return toString(toIntArray());
    }
	    
    public static void main(String[] args)
    {
	GappedIntArray gapper=new GappedIntArray();
	System.out.println(gapper);
	int[] blah=new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
	gapper.insertAt(0, blah);
	System.out.println("inserting 10 items at 0 :\n"+gapper);
	System.out.println("length: "+gapper.length());
	gapper.remove(3, 1);
	System.out.println("removing 1 item at 3:\n"+gapper);
	System.out.println("length: "+gapper.length());
	gapper.remove(3, 2);
	System.out.println("removing 2 items at 3:\n"+gapper);
	System.out.println("length: "+gapper.length());
	gapper.insertAt(5, blah);
	System.out.println("inserting 10 items at 5:\n"+gapper);
	System.out.println("length: "+gapper.length());
	gapper.insertAt(7, blah);
	System.out.println("inserting 10 items at 6:\n"+gapper);
	System.out.println("length: "+gapper.length());
	gapper.remove(10, 5);
	System.out.println("removing 5 items at 10:\n"+gapper);
	System.out.println("length: "+gapper.length());
	gapper.remove(10, 0);
	System.out.println("removing zero items :\n"+gapper);
	System.out.println("length: "+gapper.length());
	gapper.remove(0, gapper.length()-1);
	System.out.println("removing all but one item:\n"+gapper);
	System.out.println("length: "+gapper.length());	
	gapper.insertAt(1, blah);
	System.out.println("inserting 10 items at 1:\n"+gapper);
	System.out.println("length: "+gapper.length());
	gapper.remove(0, gapper.length());
	System.out.println("removing all items:\n"+gapper);
	System.out.println("length: "+gapper.length());
	gapper.insertAt(0, blah);
	System.out.println("inserting 10 items at 0 :\n"+gapper);
	System.out.println("length: "+gapper.length());
	gapper.insertAt(5, blah);
	System.out.println("inserting 10 items at 5 :\n"+gapper);
	System.out.println("length: "+gapper.length());
	blah=new int[] {89, 45, 20};
	gapper.set(4, blah);
	System.out.println("setting three values at 4:\n"+gapper);
	System.out.println("length: "+gapper.length());
	System.out.println("obtaining an array of five items: "+gapper.toString(gapper.get(5, 5)));
	gapper.set(4, blah);
	System.out.println("setting three values at 12:\n"+gapper);
	System.out.println("length: "+gapper.length());	
	System.out.println("obtaining an array of five items: "+gapper.toString(gapper.get(0, 5)));
	System.out.println("obtaining an array of five items: "+gapper.toString(gapper.get(10, 5)));
	System.out.println("obtaining an array of five items: "+gapper.toString(gapper.get(12, 5)));
	System.out.println("obtaining an array of five items: "+gapper.toString(gapper.get(15, 5)));
	System.out.println("getting the last five items: "+gapper.toString(gapper.get(gapper.length()-5, 5)));
    }
}

/* $Log: GappedIntArray.java,v $
/* Revision 1.1.1.1  2003/05/05 16:12:32  renu
/* Reformatted and restructered source tree
/*
/* Revision 1.1.1.1  2002/09/18 19:52:17  caoq
/* initial import
/*
/* Revision 1.9  2001/02/13 01:37:38  smulloni
/* additional tweaks to html mode.  Flexicizer bug (possible
/* ArrayIndexOutOfBoundsException on reparse) fixed.
/*
/* Revision 1.8  2001/02/09 20:00:01  smulloni
/* fixed particularly nasty bug in GappedIntArray.set(int, int[]), and other
/* bugs in the syntax highlighting system.
/*
/* Revision 1.7  2001/02/06 00:11:18  smulloni
/* struggle, perhaps futile, with the TextEditorCustomizer and other implicated
/* classes
/*
/* Revision 1.6  2001/01/30 18:01:27  smulloni
/* first working beta of syntax highlighting.  Nasty bug in
/* GappedIntArray.remove() fixed.
/*
/* Revision 1.5  2001/01/29 22:28:47  smulloni
/* syntax highlighting package now uses a custom view for painting the
/* highlights.  Fixed bug in get(int, int[]) in GappedIntArray.
/*
/* Revision 1.4  2001/01/25 22:52:48  smulloni
/* added get(int, int) method to GappedIntArray
/*
/* Revision 1.3  2001/01/18 22:29:21  smulloni
/* experimental work on syntax package.
/*
/* Revision 1.2  2001/01/17 23:02:29  smulloni
/* beginning to rework SyntaxDocument, SyntaxTokenizer, to shift the
/* responsibility for handling context requirements from the document
/* to the tokenizer.
/*
/* Revision 1.1  2001/01/17 22:05:07  smulloni
/* a container for ints that holds a gap at the point of insert or removal, to
/* make subsequent operations at that point more efficient.  Is it actually more
/* efficient?  I don't know yet.
/* */
