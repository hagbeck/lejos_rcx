package js.tinyvm;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.Vector;

import js.common.AbstractTool;
import js.common.ToolProgressListener;
import js.tinyvm.io.BEDataOutputStream;
import js.tinyvm.io.ByteWriter;
import js.tinyvm.io.LEDataOutputStream;

/**
 * Tiny VM.
 */
public class TinyVMTool extends AbstractTool
{
  /**
   * Constructor.
   */
  public TinyVMTool(ToolProgressListener listener)
  {
    super(listener);
  }

  /**
   * Execute tiny vm.
   * 
   * @param classpath classpath
   * @param classes main classes to compile
   * @param all
   * @param stream output stream to write binary to
   * @param bigEndian write big endian output?
   * @throws TinyVMException
   */
  public void link (String classpath, String[] classes, boolean all,
      OutputStream stream, boolean bigEndian) throws TinyVMException
  {
    assert classpath != null : "Precondition: classpath != null";
    assert classes != null : "Precondition: classes != null";
    assert stream != null : "Precondition: stream != null";
    
    Binary binary = link(classpath, classes, all);
    dump(binary, stream, bigEndian);
  }

  /**
   * Link classes.
   * 
   * @param classpath class path
   * @param classes classes to link
   * @param all do not filter classes?
   * @return binary
   * @throws TinyVMException
   */
  public Binary link (String classpath, String[] classes, boolean all)
      throws TinyVMException
  {
    assert classpath != null : "Precondition: classpath != null";
    assert classes != null : "Precondition: classes != null";
    
    Binary result;
    try
    {
      if (classes.length >= 256)
      {
        throw new TinyVMException("Too many entry classes (max is 255!)");
      }

      ClassPath computedClasspath = new ClassPath(classpath);
      // TODO refactor
      Vector classVector = new Vector();
      for (int i = 0; i < classes.length; i++)
      {
        classVector.addElement(classes[i].replace('.', '/').trim());
      }
      result = Binary.createFromClosureOf(classVector, computedClasspath, all);
      for (int i = 0; i < classes.length; i++)
      {
        String clazz = classes[i].replace('.', '/').trim();
        if (!result.hasMain(clazz))
        {
          throw new TinyVMException("Class " + clazz
              + " doesn't have a static void main(String[]) method");
        }
      }
    }
    catch (Exception e)
    {
      // TODO make other classes throw TinyVMExceptions too
      throw new TinyVMException(e);
    }

    assert result != null : "Postconditon: result != null";
    return result;
  }

  /**
   * Dump binary to stream.
   * 
   * @param binary binary
   * @param stream stream to write to
   * @param bigEndian use big endian encoding?
   * @throws TinyVMException
   */
  public void dump (Binary binary, OutputStream stream, boolean bigEndian)
      throws TinyVMException
  {
    assert binary != null : "Precondition: binary != null";
    assert stream != null : "Precondition: stream != null";
    
    try
    {
      OutputStream bufferedStream = new BufferedOutputStream(stream, 4096);
      ByteWriter byteWriter = bigEndian ? (ByteWriter) new BEDataOutputStream(
          bufferedStream) : (ByteWriter) new LEDataOutputStream(bufferedStream);
      binary.dump(byteWriter);
      bufferedStream.close();
    }
    catch (Exception e)
    {
      // TODO make other classes throw TinyVMExceptions too
      throw new TinyVMException(e);
    }
  }
}