package js.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import js.common.AbstractTool;
import js.common.ToolException;
import js.common.ToolProgressListener;

/**
 * Java RCX firmware downloader - replaces lejosfirmdl
 * 
 * @author Lawrie Griffiths, Markus Heiden
 */
public class FirmdlTool extends AbstractTool
{
  private static final int SEGMENT_BREAK = 1024;
  private static final int IMAGE_START = 0x8000;
  private static final int IMAGE_MAXLEN = 0x8000;
  private static final int MAX_SEGMENTS = 2;

  /**
   * Constructor.
   */
  public FirmdlTool(ToolProgressListener listener)
  {
    super(listener);
  }

  /**
   * Execute firmware download with default firmware.
   * 
   * @param tty port
   * @param download download read image?
   * @param fastMode use fast mode?
   * @throws ToolException
   */
  public void start (String tty, boolean download, boolean fastMode)
      throws FirmdlException
  {
    // get firmware
    // Find the lejos bin directory
    // String dir = which("js.tools.Firmdl");
    // fileName = dir + "bin/lejos.srec";
    InputStream stream = Firmdl.class.getResourceAsStream("/lejos.srec");
    if (stream == null)
    {
      throw new FirmdlException("Unable to find default lejos.srec");
    }
    start(new InputStreamReader(stream), tty, download, fastMode);
  }

  /**
   * Execute firmware download.
   * 
   * @param reader reader to read firmware from
   * @param tty port
   * @param download download read image?
   * @param fastMode use fast mode?
   * @throws ToolException
   */
  public void start (Reader reader, String tty, boolean download,
      boolean fastMode) throws FirmdlException
  {
    try
    {
      // Load the s-record file
      Image image = srecLoad(reader, MAX_SEGMENTS, IMAGE_MAXLEN);
      int length = 0;
      for (int i = 0; i < image.segments.length; i++)
      {
        getProgressListener().log(
            "Segment " + i + ": length = " + image.segments[i].length);
        length += image.segments[i].length;
      }

      if (download)
      {
        Download d = new Download(getProgressListener());
        try
        {
          if (fastMode)
          {
            getProgressListener().operation("Installing fastmode firmware");
            d.open(tty, false);
            d.installFirmware(FastImage.fastdlImage,
                FastImage.fastdlImage.length, IMAGE_START);
            d.close();
          }
          getProgressListener().operation("Installing firmware");
          d.open(tty, fastMode);
          d.installFirmware(image.data, length, image.entry);
          d.close();
        }
        catch (ToolException e)
        {
          throw new FirmdlException(e);
        }
        finally
        {
          if (d.isOpen())
          {
            d.close();
          }
        }
      }
    }
    finally
    {
      try
      {
        reader.close();
      }
      catch (IOException e)
      {
        // ignore
      }
    }
  }

  //
  // private interface
  //

  /**
   * Load image from srec.
   */
  private Image srecLoad (Reader reader, int numimage_def, int maxlen)
      throws FirmdlException
  {
    BufferedReader bufferedReader = new BufferedReader(reader);
    Image image = new Image();

    try
    {
      String record;
      SRec srec = null;
      int segStartAddr = 0;
      int prevAddr = -SEGMENT_BREAK;
      int prevCount = SEGMENT_BREAK;
      int segIndex = -1;
      boolean strip = false;
      int imageIndex = -SEGMENT_BREAK;
      int length = 0, i;

      /* Read image file */
      int line = 0;
      while ((record = bufferedReader.readLine()) != null)
      {
        // Skip blank lines
        record = record.trim();
        if (record.length() == 0)
        {
          continue;
        }
        line++;

        int error;

        /* Decode line */
        try
        {
          char[] buf = record.toCharArray();
          srec = new SRec(buf, buf.length);
        }
        catch (IOException e)
        {
          throw new FirmdlException("Error on line " + line + " : "
              + e.getMessage(), e);
        }

        if (srec.type == 0)
        {
          /* Detect Firm0309.lgo header, set strip if found */
          if ((new String(srec.data)).equals("?LIB_VERSION_L00"))
          {
            getProgressListener().log("Setting strip");
            strip = true;
          }
        }
        else if (srec.type == 1)
        {
          /* Process s-record data */

          /* Start of a new segment? */
          if (srec.addr - prevAddr >= SEGMENT_BREAK)
          {
            segIndex++;
            image.segments[segIndex] = new Segment();
            if (segIndex >= numimage_def)
            {
              throw new FirmdlException("Expected number of image_def exceeded");
            }
            image.segments[segIndex].length = 0;
            segStartAddr = srec.addr;
            prevAddr = srec.addr - prevCount;
            image.segments[segIndex].offset = imageIndex + prevCount;
          }

          if (srec.addr < IMAGE_START
              || srec.addr + srec.count > IMAGE_START + maxlen)
          {
            throw new FirmdlException("Address (" + srec.addr
                + ") out of bounds (srec) on line " + line + "\nCount = "
                + srec.count + "\nmaxlen = " + maxlen);
          }

          // Data is not necessarily contiguous so can't just accumulate
          // srec.counts.
          image.segments[segIndex].length = srec.addr - segStartAddr
              + srec.count;

          imageIndex += srec.addr - prevAddr;
          for (i = 0; i < srec.count; i++)
          {
            image.data[imageIndex + i] = (byte) srec.data[i];
          }
          prevAddr = srec.addr;
          prevCount = srec.count;
        }
        else if (srec.type == 9)
        {
          // Process image entry point
          if (srec.addr < IMAGE_START || srec.addr > IMAGE_START + maxlen)
          {
            throw new FirmdlException("Address out of bounds (image) on line"
                + line);
          }

          image.entry = srec.addr;
        }
      }

      if (strip)
      {
        int pos;
        getProgressListener().log("Stripping");
        for (pos = IMAGE_MAXLEN - 1; pos >= 0 && image.data[pos] == 0; pos--)
        {
          image.segments[segIndex].length--;
        }
      }

      for (i = 0; i <= segIndex; i++)
      {
        length += image.segments[segIndex].length;
      }

      if (length == 0)
      {
        throw new FirmdlException("Image contains no data");
      }
    }
    catch (IOException e)
    {
      throw new FirmdlException("Unable to read srec: " + e.getMessage(), e);
    }
    finally
    {
      try
      {
        bufferedReader.close();
      }
      catch (IOException e)
      {
        // ignore
      }
    }

    assert image != null : "Postcondition: result != null";
    return image;
  }

  private static class Segment
  {
    public int length;
    public int offset;
  }

  private static class Image
  {
    public int entry;
    public Segment[] segments = new Segment[MAX_SEGMENTS];
    public byte[] data = new byte[IMAGE_MAXLEN];
  }
}